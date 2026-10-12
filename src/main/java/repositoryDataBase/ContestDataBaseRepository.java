package repositoryDataBase;

import exception.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import model.Contest;
import model.Problem;
import repository.CrudRepository;

public class ContestDataBaseRepository implements CrudRepository<Contest, UUID> {
  private static final String DB_URL = "jdbc:h2:./data/code-judge;AUTO_SERVER=TRUE";
  private static final String DB_USER = "sa";
  private static final String DB_PASSWORD = "";

  private final ProblemDataBaseRepository problemRepository;

  public ContestDataBaseRepository() {
    this.problemRepository = new ProblemDataBaseRepository();
  }

  public ContestDataBaseRepository(ProblemDataBaseRepository problemRepository) {
    this.problemRepository = problemRepository;
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
  }

  private Contest mapRowToContest(ResultSet rs)
      throws SQLException { // transforma din raspuns rs in  object contest valid
    Contest contest = new Contest();
    contest.setId(rs.getObject("id", UUID.class));
    contest.setName(rs.getString("name"));

    Timestamp start = rs.getTimestamp("starttime");
    if (start != null) contest.setStartTime(start.toLocalDateTime());

    Timestamp end = rs.getTimestamp("endtime");
    if (end != null) contest.setEndTime(end.toLocalDateTime());

    return contest;
  }

  private List<Problem> findProblemsByContestId(Connection conn, UUID contestId)
      throws SQLException {
    String sql = "SELECT problemid FROM contest_problems WHERE contestid = ?";
    List<Problem> problems = new ArrayList<>();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, contestId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          UUID problemId = rs.getObject("problemid", UUID.class);
          problemRepository.findById(problemId).ifPresent(problems::add);
        }
      }
    }
    return problems;
  }

  @Override
  public Contest save(Contest contest) {
    if (contest.getId() == null) {
      contest.setId(UUID.randomUUID());
    }
    String saveContestSql =
        "MERGE INTO contests (id, name, starttime, endtime) KEY(id) VALUES (?, ?, ?, ?)";
    String clearProblemsSql = "DELETE FROM contest_problems WHERE contestid = ?";
    String insertProblemSql =
        "MERGE INTO contest_problems (contestid, problemid) KEY(contestid, problemid) VALUES (?, ?)";
    try (Connection conn = getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement stmt = conn.prepareStatement(saveContestSql)) {
          stmt.setObject(1, contest.getId());
          stmt.setString(2, contest.getName());
          stmt.setTimestamp(
              3, contest.getStartTime() != null ? Timestamp.valueOf(contest.getStartTime()) : null);
          stmt.setTimestamp(
              4, contest.getEndTime() != null ? Timestamp.valueOf(contest.getEndTime()) : null);
          stmt.executeUpdate();
        }

        try (PreparedStatement clearStmt = conn.prepareStatement(clearProblemsSql)) {
          clearStmt.setObject(1, contest.getId());
          clearStmt.executeUpdate();
        }

        if (contest.getProblems() != null && !contest.getProblems().isEmpty()) {
          try (PreparedStatement insertStmt = conn.prepareStatement(insertProblemSql)) {
            for (Problem problem : contest.getProblems()) {
              if (problem.getId() == null) {
                problemRepository.save(problem); // creaza problema daca nu exista
              }
              insertStmt.setObject(1, contest.getId());
              insertStmt.setObject(2, problem.getId());
              insertStmt.addBatch();
            }
            insertStmt.executeBatch();
          }
        }

        conn.commit(); // rul toate o data
        return contest;
      } catch (SQLException e) {
        conn.rollback(); // daca nici una nu a mers pot reverti toate cele 4 requesturi
        throw e;
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to save contest: " + e.getMessage(), e);
    }
  }

  @Override
  public Optional<Contest> findById(UUID id) {
    String sql = "SELECT id, name, starttime, endtime FROM contests WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Contest contest = mapRowToContest(rs);
          contest.setProblems(findProblemsByContestId(conn, contest.getId()));
          return Optional.of(contest);
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to find contest by id", e);
    }
  }

  @Override
  public List<Contest> findAll() {
    String sql = "SELECT id, name, starttime, endtime FROM contests";
    List<Contest> contests = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        Contest contest = mapRowToContest(rs);
        contest.setProblems(findProblemsByContestId(conn, contest.getId()));
        contests.add(contest);
      }
      return contests;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to fetch all contests", e);
    }
  }

  public Optional<Contest> getContestByName(String name) {
    String sql = "SELECT id, name, starttime, endtime FROM contests WHERE name = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, name);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Contest contest = mapRowToContest(rs);
          contest.setProblems(findProblemsByContestId(conn, contest.getId()));
          return Optional.of(contest);
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to find contest by name", e);
    }
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = "DELETE FROM contests WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to delete contest", e);
    }
  }
}
