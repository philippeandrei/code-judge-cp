package repositoryDataBase;

import exception.DataAccessException;
import model.Submission;
import model.SubmissionStatusEnum;
import model.User;
import repository.CrudRepository;
import java.sql.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SubmissionDataBaseRepository implements CrudRepository<Submission, UUID> {
    private static final String DB_URL = "jdbc:h2:./data/code-judge;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private Submission mapRowToSubmission(ResultSet rs) throws SQLException {
        Submission submission = new Submission();
        submission.setId(rs.getObject("id", UUID.class));
        submission.setProblemId(rs.getObject("problemid", UUID.class));
        submission.setUserId(rs.getObject("userid", UUID.class));
        String status = rs.getString("status");
        if (status != null) {
            submission.setStatus(SubmissionStatusEnum.valueOf(status));
        }
        submission.setSourceCode(rs.getString("sourcecode"));
        Timestamp submittedtime = rs.getTimestamp("submittedtime");
        if (submittedtime != null) {
            submission.setSubmittedTime(submittedtime.toLocalDateTime());
        }
        submission.setErrorMessage(rs.getString("errormessage"));
        return submission;
    }


    @Override
    public Submission save(Submission submission) {
        if (submission.getId() == null) {
            submission.setId(UUID.randomUUID());
        }
        if (submission.getStatus() == null) {
            submission.setStatus(SubmissionStatusEnum.PENDING);
        }
        if (submission.getSubmittedTime() == null) {
            submission.setSubmittedTime(LocalDateTime.now());
        }
        String sql = "MERGE INTO submissions (id, userid, problemid, sourcecode, status, submittedtime, errormessage) KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, submission.getId());
            stmt.setObject(2, submission.getUserId());
            stmt.setObject(3, submission.getProblemId());
            stmt.setString(4, submission.getSourceCode());
            stmt.setString(5, submission.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(submission.getSubmittedTime()));
            stmt.setString(7, submission.getErrorMessage());
            stmt.executeUpdate();
            return submission;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save submission", e);
        }
    }

    @Override
    public Optional<Submission> findById(UUID id) {
        String sql = "SELECT * FROM submissions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToSubmission(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find submission by id", e);
        }
    }

    @Override
    public List<Submission> findAll() {
        String sql = "SELECT * FROM submissions";
        List<Submission> submissions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapRowToSubmission(rs));
                }
                return submissions;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch all submissions", e);
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM submissions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete submission", e);
        }
    }




    //getSubmissionsOfProblem (id)
    //getSubmissionsOfUser(UUID userID)

    public List<Submission> getSubmissionsOfProblem(UUID id) {
        String sql = "SELECT * FROM submissions WHERE problemid = ?";
        List<Submission> submissions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapRowToSubmission(rs));
                }
                return submissions;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch all submissions of problem ", e);
        }
    }
    public List<Submission> getSubmissionsOfUser(UUID id) {
        String sql = "SELECT * FROM submissions WHERE userid = ?";
        List<Submission> submissions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapRowToSubmission(rs));
                }
                return submissions;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch all submissions of user ", e);
        }
    }
    //getSubmissionsOfUserOfProblem

    public List<Submission> getSubmissionsOfUserOfProblem(UUID userId, UUID problemId) {
        String sql = "SELECT * FROM submissions WHERE userid = ? AND problemid = ?";
        List<Submission> submissions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, problemId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapRowToSubmission(rs));
                }
                return submissions;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch all submissions of user ", e);
        }
    }

}
