package service;

import exception.UserNotFoundException;
import model.User;
import repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class UserService {
    public UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String email) throws IOException {
        User user = new User(username, email);
        userRepository.save(user);
        return user;
    }

    public User getUserById(UUID id) throws UserNotFoundException{
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("The user can not be found."));
    }

    public User getUserByUsername(String username) throws UserNotFoundException{
        return userRepository.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException("The user can not be found."));
    }

    public void deleteUserById(UUID id) throws UserNotFoundException, IOException {
        if(!userRepository.deleteById(id))
            new UserNotFoundException("The user can not be found");
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
