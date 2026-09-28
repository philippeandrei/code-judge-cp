package org.example;

import io.javalin.Javalin;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        record User(int id, String name, String email) {}
        Javalin app = Javalin.create(config -> {



            config.routes.get("/", ctx -> ctx.result("Hello from Javalin!"));

            config.routes.get("/problems", ctx -> {
               ctx.result("Problem 1 problem 2");
            });
            config.routes.get("/api/user", ctx -> {
                User user = new User(1, "Alice Smith", "alice@example.com");
                ctx.json(user); // Automatically converts to JSON
            });
            config.routes.post("/api/problem", ctx -> {
                Map<String, String> body = ctx.bodyAsClass(Map.class);
                String name = body.get("username");
                User user = new User(1, name, "alice@example.com");

                ctx.status(200).json(user);
            });



        }).start(7070);


    }
}