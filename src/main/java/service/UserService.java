package service;

import exception.UserNotFoundException;
import model.User;
import repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserService {
    public UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(String username, String email)  {
        User user = new User(username, email);
        userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("The user can not be found."));
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException("The user can not be found."));
    }

    public void deleteUserById(UUID id)  {
        if(!userRepository.deleteById(id))
            throw new UserNotFoundException("The user can not be found");
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
