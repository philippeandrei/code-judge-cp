package apiController;

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
    public void getAll(Context ctx) {
        // TODO: use pathParam for /username and /id
        String username = ctx.queryParam("username");
        String idParam = ctx.queryParam("id");

        if (username != null && !username.trim().isEmpty()) {
            ctx.json(userService.getUserByUsername(username));
        } else if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                System.out.println("Ramura cu id");
                UUID id = UUID.fromString(idParam);
                ctx.json(userService.getUserById(id));
            } catch (IllegalArgumentException e) {
                ctx.status(400).result("Format UUID invalid.");
            }
        } else {
            ctx.json(userService.getAllUsers());
        }
    }

    // TODO: Learn about DTOs
    public void create(Context ctx) {
        User payload = ctx.bodyAsClass(User.class);
        User created = userService.registerUser(payload.getUsername(), payload.getEmail());
        ctx.status(HttpStatus.CREATED).json(created);
    }

    public void delete(Context ctx) {
        UUID id = UUID.fromString(ctx.queryParam("id"));
        userService.deleteUserById(id);
        ctx.status(HttpStatus.NO_CONTENT);
    }
}
