import apiController.UserApiController;
import controller.ContestController;
import controller.JudgeController;
import controller.ProblemController;
import controller.UserController;
import io.javalin.Javalin;
import repository.ContestRepository;
import repository.ProblemRepository;
import repository.UserRepository;
import service.ContestService;
import service.JudgeService;
import service.ProblemService;
import service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        runApi();
    }

    public static void runApi(){
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);
        UserApiController userApiController = new UserApiController(userService);

        ProblemRepository problemRepository = new ProblemRepository();
        ProblemService problemService = new ProblemService(problemRepository);
        ProblemController problemController = new ProblemController(problemService);

        ContestRepository contestRepository = new ContestRepository();
        ContestService contestService = new ContestService(contestRepository, problemRepository);
        ContestController contestController = new ContestController(contestService, problemService);

        JudgeService judgeService = new JudgeService();
        JudgeController judgeController = new JudgeController(judgeService);

        Javalin.create(config -> {
            config.routes.get("/api/users", userApiController::getAll);
            config.routes.post("/api/users", userApiController::create);
            config.routes.delete("/api/users", userApiController::delete);
        }).start(7070);
    }
}