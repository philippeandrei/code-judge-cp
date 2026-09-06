import controller.ContestController;
import controller.ProblemController;
import controller.UserController;
import repository.ContestRepository;
import repository.ProblemRepository;
import repository.UserRepository;
import service.ContestService;
import service.ProblemService;
import service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        run();
    }


    public static void run() {
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);

        ProblemRepository problemRepository = new ProblemRepository();
        ProblemService problemService = new ProblemService(problemRepository);
        ProblemController problemController = new ProblemController(problemService);

        ContestRepository contestRepository = new ContestRepository();
        ContestService contestService = new ContestService(contestRepository, problemRepository);
        ContestController contestController = new ContestController(contestService, problemService);

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("\n~");
            String command = sc.nextLine().trim();

            if (command.equalsIgnoreCase("exit")) {
                System.out.print("Session terminated");
                break;
            }
            processCommand(command, userController, problemController, contestController);
        }
        sc.close();
    }

    public static void processCommand(String input, UserController userController, ProblemController problemController, ContestController contestController) {

        List<String> tokens = parseTokens(input);
        if (tokens.isEmpty()) {
            return;
        }
        String command = tokens.get(0).toLowerCase();

        try {
            switch (command) {
                case "create-user":
                    if (tokens.size() != 3) {
                        System.out.print("[Syntax Error] Use command: create-user <username> <email>");
                        break;
                    }
                    String username = tokens.get(1).toLowerCase();
                    String email = tokens.get(2).toLowerCase();
                    userController.handleCreateUser(username, email);
                    break;
                case "get-user":
                    if (tokens.size() != 2) {
                        System.out.print("[Syntax Error] Use command: get-user <username>");
                        break;
                    }
                    username = tokens.get(1).toLowerCase();
                    userController.handleGetUserByUsername(username);
                    break;
                case "get-all-users":
                    if (tokens.size() != 1) {
                        System.out.print("[Syntax Error] Use command: get-all-users");
                        break;
                    }
                    userController.handleGetAllUsers();
                    break;

                case "delete-user":
                    if (tokens.size() != 2) {
                        System.out.print("[Syntax Error] Use command: delete-user <id>");
                        break;
                    }
                    String id = tokens.get(1);
                    userController.handleDeleteUserById(UUID.fromString(id));
                    break;
                case "get-user-id":
                    if (tokens.size() != 2) {
                        System.out.print("[Syntax Error] Use command: get-user-id <id>");
                        break;
                    }
                    id = tokens.get(1);
                    userController.handleGetUserById(UUID.fromString(id));
                    break;
                case "create-problem":
                    if (tokens.size() != 5) {
                        System.out.print("[Syntax Error] Use command: create-problem \"<title>\" \"<statement>\" <timeLimitMs> <memoryLimitKb>");
                        break;
                    }
                    //create-problem <title> <statement> <timeLimitMs> <memoryLimitKb>
                    String title = tokens.get(1);
                    String statement = tokens.get(2);
                    try {
                        long timeLimitMs = Long.parseLong(tokens.get(3));
                        long memoryLimitKb = Long.parseLong(tokens.get(4));
                        problemController.handleCreateProblem(title, statement, timeLimitMs, memoryLimitKb);
                    } catch (NumberFormatException e) {
                        System.out.print("[Syntax Error] Time limit and memory limit must be valid numbers.");
                    }
                    break;
                case "add-test-case":
                    //add-test-case <problemId> <input> <expectedOutput>
                    if (tokens.size() != 4) {
                        System.out.print("[Syntax Error] Use command: create-problem \"<title>\" \"<statement>\" <timeLimitMs> <memoryLimitKb>");
                        break;
                    }

                    String problemId = tokens.get(1);
                    String inputForProblem = tokens.get(2);
                    String expectedOutput = tokens.get(3);
                    problemController.handleAddTestCaseToProblem(UUID.fromString(problemId), inputForProblem, expectedOutput);
                    break;
                case "search-problem-id":
                    //search-problem-id <id>
                    if (tokens.size() != 2) {
                        System.out.print("[Syntax Error] Use command: search-problem-id <id>");
                        break;
                    }

                    String problemSearchId = tokens.get(1);

                    problemController.handleGetProblemById(UUID.fromString(problemSearchId));
                    break;
                case "search-problem-title":
                    //    //search-problem-title <title>
                    if (tokens.size() != 2) {
                        System.out.print("[Syntax Error] Use command: search-problem-title \"<title>\"");
                        break;
                    }

                    String problemTitle = tokens.get(1);
                    problemController.handleGetProblemByTitle(problemTitle);
                    break;
                case "delete-problem":
                    if(tokens.size() != 2){
                        System.out.print("[Syntax Error] Use command: delete-problem <id>");
                        break;
                    }
                    problemController.handleDeleteProblemById(UUID.fromString(tokens.get(1)));
                    break;
                case "create-contest":
                    if(tokens.size() != 4){
                        System.out.print("[Syntax Error] Use command: create-contest \"<name>\" \"<startTime>\" \"<endTime>\"");
                        break;
                    }
                    //create-contest <name> <startTime> <endTime>
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                    try {
                        LocalDateTime startTime = LocalDateTime.parse(tokens.get(2),
                                formatter);
                        LocalDateTime endTime   = LocalDateTime.parse(tokens.get(3),
                                formatter);
                        contestController.handleCreateContest(tokens.get(1), startTime, endTime);

                    } catch (DateTimeParseException e) {
                        System.out.println("[Syntax Error] Invalid date format! Expected format: yyyy-MM-dd HH:mm");
                    }
                    break;
                case "get-contest-id":
                    //get-contest-id <id>
                    if(tokens.size() != 2){
                        System.out.println("[Syntax Error] Use command: get-contest-id <id>");
                        break;
                    }
                    contestController.handleGetContestById(UUID.fromString(tokens.get(1)));
                    break;


                case "get-contest-name":
                    if(tokens.size() != 2) {
                        System.out.println("[Syntax Error] Use command: get-contest-name \"<name>\"");
                        break;
                    }
                    contestController.handleGetContestByName(tokens.get(1));
                    break;
                case "delete-contest" :
                    //delete-contest-id <id>
                    if(tokens.size() != 2){
                        System.out.println("[Syntax Error] Use command: delete-contest <id>");
                        break;
                    }
                    contestController.handleDeleteContestId(UUID.fromString(tokens.get(1)));
                    break;
                case "contest-add":
                    //contest-add <contestID> <problemID>
                    if(tokens.size() != 3){
                        System.out.println("[Syntax Error] Use command: contest-add <contestID> <problemID>");
                        break;
                    }
                    contestController.handleAddProblemToContest(UUID.fromString(tokens.get(1)), UUID.fromString(tokens.get(2)));
                    break;

                default:
                    System.out.print("For all commands check the available documentation");
                    break;
            }
        } catch (Exception e) {

        }
    }

    private static List<String> parseTokens(String input) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(input);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1));
            } else {
                tokens.add(matcher.group(2));
            }
        }
        return tokens;
    }

}