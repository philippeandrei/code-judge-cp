package repositoryDataBase;

import exception.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import model.Category;
import model.CategoryDifficultyEnum;
import model.Problem;
import model.TestCase;
import repository.CrudRepository;

public class ProblemDataBaseRepository implements CrudRepository<Problem, UUID> {
  private static final String DB_URL = "jdbc:h2:./data/code-judge;AUTO_SERVER=TRUE";
  private static final String DB_USER = "sa";
  private static final String DB_PASSWORD = "";

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
  }

  private Problem mapRowToProblem(ResultSet rs) throws SQLException {
    Problem problem = new Problem();
    problem.setId(rs.getObject("id", UUID.class));
    problem.setTitle(rs.getString("title"));
    problem.setStatement(rs.getString("statement"));
    problem.setTimeLimitMs(rs.getLong("timelimitms"));
    problem.setMemoryLimitKb(rs.getLong("memorylimitkb"));
    return problem;
  }

  private List<TestCase> findTestCasesByProblemId(Connection conn, UUID problemId)
      throws SQLException {
    String sql = "SELECT id, input, expectedoutput FROM test_cases WHERE problemid = ?";
    List<TestCase> list = new ArrayList<>();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, problemId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          TestCase tc = new TestCase(rs.getString("input"), rs.getString("expectedoutput"));
          list.add(tc);
        }
      }
    }
    return list;
  }

  private List<Category> findCategoriesByProblemId(Connection conn, UUID problemId)
      throws SQLException {
    String sql =
        "SELECT c.id, c.name, c.difficulty FROM categories c "
            + "JOIN problem_categories pc ON c.id = pc.categoryid "
            + "WHERE pc.problemid = ?";
    List<Category> list = new ArrayList<>();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, problemId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Category cat = new Category();
          cat.setId(rs.getObject("id", UUID.class));
          cat.setName(rs.getString("name"));
          cat.setDifficulty(CategoryDifficultyEnum.valueOf(rs.getString("difficulty")));
          list.add(cat);
        }
      }
    }
    return list;
  }

  @Override
  public Problem save(Problem problem) {
    if (problem.getId() == null) {
      problem.setId(UUID.randomUUID());
    }

    String saveProblemSql =
            "MERGE INTO problems (id, title, statement, timelimitms, memorylimitkb) "
                    + "KEY(id) VALUES (?, ?, ?, ?, ?)";
    String clearTestCasesSql = "DELETE FROM test_cases WHERE problemid = ?";
    String insertTestCaseSql =
            "INSERT INTO test_cases (id, problemid, input, expectedoutput) VALUES (?, ?, ?, ?)";
    String clearCategoriesSql = "DELETE FROM problem_categories WHERE problemid = ?";
    String insertCategorySql =
            "MERGE INTO problem_categories (problemid, categoryid) KEY(problemid, categoryid) VALUES (?, ?)";

    try (Connection conn = getConnection()) {
      conn.setAutoCommit(false);
      try {
        // 1. Save problem
        try (PreparedStatement stmt = conn.prepareStatement(saveProblemSql)) {
          stmt.setObject(1, problem.getId());
          stmt.setString(2, problem.getTitle());
          stmt.setString(3, problem.getStatement());
          stmt.setLong(4, problem.getTimeLimitMs());
          stmt.setLong(5, problem.getMemoryLimitKb());
          stmt.executeUpdate();
        }

        // 2. Save test cases
        try (PreparedStatement clearStmt = conn.prepareStatement(clearTestCasesSql)) {
          clearStmt.setObject(1, problem.getId());
          clearStmt.executeUpdate();
        }
        if (problem.getTestCases() != null) {
          try (PreparedStatement insertStmt = conn.prepareStatement(insertTestCaseSql)) {
            for (TestCase tc : problem.getTestCases()) {
              insertStmt.setObject(1, UUID.randomUUID());
              insertStmt.setObject(2, problem.getId());
              insertStmt.setString(3, tc.getInput());
              insertStmt.setString(4, tc.getExpectedOutput());
              insertStmt.addBatch();
            }
            insertStmt.executeBatch();
          }
        }

        // 3. Save categories
        try (PreparedStatement clearStmt = conn.prepareStatement(clearCategoriesSql)) {
          clearStmt.setObject(1, problem.getId());
          clearStmt.executeUpdate();
        }
        if (problem.getCategories() != null && !problem.getCategories().isEmpty()) {
          String findCatSql = "SELECT id FROM categories WHERE name = ?";
          String insertCatSql = "INSERT INTO categories (id, name, difficulty) VALUES (?, ?, ?)";

          try (PreparedStatement insertLinkStmt = conn.prepareStatement(insertCategorySql);
               PreparedStatement findCatStmt = conn.prepareStatement(findCatSql);
               PreparedStatement insertCatStmt = conn.prepareStatement(insertCatSql)) {

            for (Category cat : problem.getCategories()) {
              UUID categoryId = cat.getId();

              if (categoryId == null) {
                findCatStmt.setString(1, cat.getName());
                try (ResultSet rs = findCatStmt.executeQuery()) {
                  if (rs.next()) {
                    categoryId = rs.getObject("id", UUID.class);
                  } else {
                    categoryId = UUID.randomUUID();
                    insertCatStmt.setObject(1, categoryId);
                    insertCatStmt.setString(2, cat.getName());
                    insertCatStmt.setString(3, cat.getDifficulty() != null
                            ? cat.getDifficulty().name()
                            : CategoryDifficultyEnum.EASY.name());
                    insertCatStmt.executeUpdate();
                  }
                }
                cat.setId(categoryId);
              }

              insertLinkStmt.setObject(1, problem.getId());
              insertLinkStmt.setObject(2, categoryId);
              insertLinkStmt.addBatch();
            }
            insertLinkStmt.executeBatch();
          }
        }

        conn.commit();
        return problem;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to save problem", e);
    }
  }


  @Override
  public Optional<Problem> findById(UUID id) {
    String sql =
        "SELECT id, title, statement, timelimitms, memorylimitkb FROM problems WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Problem problem = mapRowToProblem(rs);
          problem.setTestCases(findTestCasesByProblemId(conn, problem.getId()));
          problem.setCategories(findCategoriesByProblemId(conn, problem.getId()));
          return Optional.of(problem);
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to find problem by id", e);
    }
  }

  public Optional<Problem> getProblemByTitle(String title) {
    String sql =
        "SELECT id, title, statement, timelimitms, memorylimitkb FROM problems WHERE title = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, title);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Problem problem = mapRowToProblem(rs);
          problem.setTestCases(findTestCasesByProblemId(conn, problem.getId()));
          problem.setCategories(findCategoriesByProblemId(conn, problem.getId()));
          return Optional.of(problem);
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to find problem by title", e);
    }
  }

  @Override
  public List<Problem> findAll() {
    String sql = "SELECT id, title, statement, timelimitms, memorylimitkb FROM problems";
    List<Problem> problems = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        Problem problem = mapRowToProblem(rs);
        problem.setTestCases(findTestCasesByProblemId(conn, problem.getId()));
        problem.setCategories(findCategoriesByProblemId(conn, problem.getId()));
        problems.add(problem);
      }
      return problems;

    } catch (SQLException e) {
      throw new DataAccessException("Failed to fetch all problems", e);
    }
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = "DELETE FROM problems WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setObject(1, id);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to delete problem", e);
    }
  }
}
