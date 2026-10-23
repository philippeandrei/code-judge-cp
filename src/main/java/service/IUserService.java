package service;

import java.util.List;
import java.util.UUID;
import model.User;

public interface IUserService {
  User registerUser(String username, String email);
  User getUserById(UUID id);
  User getUserByUsername(String username);
  void deleteUserById(UUID id);
  List<User> getAllUsers();
}
