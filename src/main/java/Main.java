import controller.UserController;
import repository.UserRepository;
import service.UserService;

import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
            run();
    }




    public static void run(){
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);

        Scanner sc = new Scanner(System.in);

        while(true) {
            System.out.print("\n~");
            String command = sc.nextLine().trim();

            if (command.equalsIgnoreCase("exit")) {
                System.out.print("Session terminated");
                break;
            }
            processCommand(command, userController);
        }
        sc.close();
    }

    public static void processCommand(String input, UserController userController){
        //create-user <username> <email>


        //display-user <username>
        String[] tokens = input.split("\\s+");
        String command = tokens[0].toLowerCase();

        try {
            switch (command){
                case "create-user":
                    if(tokens.length != 3){
                        System.out.print("[Syntax Error] Use command: create-user <username> <email>");
                        break;
                    }
                    String username = tokens[1].toLowerCase();
                    String email = tokens[2].toLowerCase();
                    userController.handleCreateUser(username, email);
                    break;
                case "get-user":
                    if(tokens.length != 2){
                        System.out.print("[Syntax Error] Use command: get-user <username>");
                        break;
                    }
                    username = tokens[1].toLowerCase();
                    userController.handleGetUserByUsername(username);
                    break;
                case "get-all-users":
                    if(tokens.length != 1){
                        System.out.print("[Syntax Error] Use command: get-all-users");
                        break;
                    }
                    userController.handleGetAllUsers();
                    break;

                case "delete-user":
                    if(tokens.length != 2){
                        System.out.print("[Syntax Error] Use command: delete-user <id>");
                        break;
                    }
                    String id = tokens[1];
                    userController.handleDeleteUserById(UUID.fromString(id));
                    break;
                case "get-user-id":
                    if(tokens.length != 2){
                        System.out.print("[Syntax Error] Use command: get-user-id <id>");
                        break;
                    }
                    id = tokens[1];
                    userController.handleGetUserById(UUID.fromString(id));
                    break;
                default:
                    System.out.print("For all commands check the avalbile documentation");
                    break;
            }
        } catch (Exception e){

        }
    }


}