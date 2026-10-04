package apiController;

import dto.CreateUserRequest;
import dto.UserResponseDto;
import exception.UserNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.User;
import service.UserService;

import java.util.UUID;

public class UserApiController {
    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    // TODO: Learn about DTOs (Data Transfer Objects)

    public void getAll(Context ctx){
        String username = ctx.queryParam("username");
        if(username != null){
            try {
                User user = userService.getUserByUsername(username);
                ctx.json(user);

            } catch (Exception e) {
                ctx.status(HttpStatus.NOT_FOUND).json("The user can not be found");
            }

        }
        else {
            ctx.json(userService.getAllUsers());
        }
    }


    public void getUserId(Context ctx){
        UUID id = UUID.fromString(ctx.pathParam("id"));
        try {
            UserResponseDto response = new UserResponseDto(userService.getUserById(id));
            ctx.json(response);
        } catch (Exception e){
            ctx.status(HttpStatus.NOT_FOUND).json("Can not find user with id");
        }

    }

    // TODO: Learn about DTOs
    public void create(Context ctx) {
        CreateUserRequest payload = ctx.bodyAsClass(CreateUserRequest.class);

        User created = userService.registerUser(payload.getUsername(), payload.getEmail());
        ctx.status(HttpStatus.CREATED).json(created);
    }

    public void delete(Context ctx) {
        String payload = ctx.pathParamAsClass("id", String.class).get();
        UUID id = UUID.fromString(payload);
        try {
            userService.deleteUserById(id);
            ctx.status(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND).json("User with id not found");
        }
    }
}
