import apiController.*;
import io.javalin.Javalin;
import java.util.*;
import repository.ContestRepository;
import repository.ProblemRepository;
import repository.SubmissionRepository;
import repository.UserRepository;
import service.*;

public class Main {
  public static void main(String[] args) {
    runApi();
  }

  public static void runApi() {
    UserRepository userRepository = new UserRepository();
    UserService userService = new UserService(userRepository);
    UserApiController userApiController = new UserApiController(userService);

    ProblemRepository problemRepository = new ProblemRepository();
    ProblemService problemService = new ProblemService(problemRepository);
    ProblemApiController problemApiController = new ProblemApiController(problemService);

    ContestRepository contestRepository = new ContestRepository();
    ContestService contestService = new ContestService(contestRepository, problemRepository);
    ContestApiController contestApiController = new ContestApiController(contestService);

    JudgeService judgeService = new JudgeService();
    JudgeApiController judgeApiController = new JudgeApiController(judgeService);

    SubmissionRepository submissionRepository = new SubmissionRepository();
    SubmissionService submissionService = new SubmissionService(submissionRepository);
    SubmissionsApiController submissionsApiController =
        new SubmissionsApiController(judgeService, submissionService);

    Javalin.create(
            config -> {
              config.routes.get("/api/users", userApiController::getAll);
              config.routes.post("/api/users", userApiController::create);
              config.routes.delete("/api/users/{id}", userApiController::delete);
              config.routes.get("/api/users/{id}", userApiController::getUserId);

              config.routes.post("/api/problems", problemApiController::create);
              config.routes.get("/api/problems", problemApiController::getAll);
              config.routes.get("/api/problems/{id}", problemApiController::getProblemId);
              config.routes.delete("/api/problems/{id}", problemApiController::delete);

              config.routes.post("/api/contests", contestApiController::create);
              config.routes.get("/api/contests", contestApiController::getAll);
              config.routes.get("/api/contests/{id}", contestApiController::getContestId);
              config.routes.delete("/api/contests/{id}", contestApiController::delete);
              config.routes.get(
                  "/api/contests/{id}/add/{problemId}", contestApiController::addProblem);

              config.routes.post("/api/submit", submissionsApiController::submit);
              config.routes.get("/api/submissions/{id}", submissionsApiController::getById);
              config.routes.get(
                  "/api/submissions/", submissionsApiController::getSubmissionsOfUser);
            })
        .start(7070);
  }
}
