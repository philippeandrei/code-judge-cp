package service;

import exception.UserNotFoundException;
import java.util.List;
import java.util.UUID;
import model.User;
import repositoryDataBase.UserDataBaseRepository;

public class UserService {
  public UserDataBaseRepository userDataBaseRepository;

  public UserService(UserDataBaseRepository userDataBaseRepository) {
    this.userDataBaseRepository = userDataBaseRepository;
  }

  public User registerUser(String username, String email) {
    User user = new User(username, email);
    userDataBaseRepository.save(user);
    return user;
  }

  public User getUserById(UUID id) {
    return userDataBaseRepository
        .findById(id)
        .orElseThrow(() -> new UserNotFoundException("The user can not be found."));
  }

  public User getUserByUsername(String username) {
    return userDataBaseRepository
        .getUserByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("The user can not be found."));
  }

  public void deleteUserById(UUID id) throws UserNotFoundException {
    if (!userDataBaseRepository.deleteById(id))
      throw new UserNotFoundException("The user can not be found");
  }

  public List<User> getAllUsers() {
    return userDataBaseRepository.findAll();
  }
}
