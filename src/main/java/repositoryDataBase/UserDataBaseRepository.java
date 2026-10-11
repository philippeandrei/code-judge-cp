package repositoryDataBase;

import exception.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import model.User;
import repository.CrudRepository;

public class UserDataBaseRepository implements CrudRepository<User, UUID> {
  private static final String DB_URL = "jdbc:h2:./data/code-judge;AUTO_SERVER=TRUE";
  private static final String DB_USER = "sa";
  private static final String DB_PASSWORD = "";

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
  }

  private User mapRowToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setId(rs.getObject("id", UUID.class));
    user.setUsername(rs.getString("username"));
    user.setEmail(rs.getString("email"));
    return user;
  }

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      user.setId(UUID.randomUUID());
    }
    String sql = "MERGE INTO users (id, username, email) KEY(id) VALUES (?, ?, ?)";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, user.getId());
      stmt.setString(2, user.getUsername());
      stmt.setString(3, user.getEmail());
      stmt.executeUpdate();
      return user;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to save user", e);
    }
  }

  @Override
  public Optional<User> findById(UUID id) {
    String sql = "SELECT id, username, email FROM users WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapRowToUser(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to find user by id", e);
    }
  }

  @Override
  public List<User> findAll() {
    String sql = "SELECT id, username, email FROM users";
    List<User> users = new ArrayList<>();

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          users.add(mapRowToUser(rs));
        }
        return users;
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to fetch all users", e);
    }
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = "DELETE FROM users WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setObject(1, id);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to delete user", e);
    }
  }

  public Optional<User> getUserByUsername(String username) {
    String sql = "SELECT id, username, email FROM users WHERE username = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapRowToUser(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to find user by username", e);
    }
  }
}
