import controller.UserController;
import repository.UserRepository;
import service.UserService;

public class Main {
    public static void main(String[] args) {
            run();
    }



    public static void run(){
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);
    }

}