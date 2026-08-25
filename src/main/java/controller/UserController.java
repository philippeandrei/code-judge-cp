package controller;

import exception.UserNotFoundException;
import model.User;
import service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class UserController {
    public UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //create-user <username> <email>
    public void handleCreateUser(String username, String email) throws IOException {
        if (username == null || email == null) {
            System.out.println("[Syntax error] Use valid username and email");
            return;
        }

        userService.registerUser(username, email);
        System.out.println("User created.");

    }
    //get-user-id <id>

    public void handleGetUserById(UUID id) {
        try {
            User user = userService.getUserById(id);
            System.out.println("Found user: " + user.getUsername());

        } catch (UserNotFoundException e){
            System.out.println("[User not found error] " + e);
        } catch (Exception e){
            System.out.println("[Unknown error] " + e);
        }
    }
    //get-user <username>
    public void handleGetUserByUsername(String username){
        try {
            User user = userService.getUserByUsername(username);
            System.out.println("Found user: " + user.getUsername() + " by id: " + user.getId());
        } catch (UserNotFoundException e) {
            System.out.println("[User not found error] " + e);
        }  catch (Exception e) {
            System.out.println("[Unknown error] " + e);
        }
    }
    //delete-user <id>
    public void handleDeleteUserById(UUID id){
        try{
            userService.deleteUserById(id);
        } catch (UserNotFoundException e) {
            System.out.println("[User not found error] " + e);
        }  catch (Exception e) {
            System.out.println("[Unknown error] " + e);
        }
    }
    //get-all-users
    public void handleGetAllUsers(){
        try {
            List<User> users = userService.getAllUsers();
            for(User user: users){
                System.out.println("User: " + user.getUsername() + ", id: " + user.getId());
            }
        } catch (UserNotFoundException e) {
            System.out.println("[User not found error] " + e);
        }  catch (Exception e) {
            System.out.println("[Unknown error] " + e);
        }
    }
}
