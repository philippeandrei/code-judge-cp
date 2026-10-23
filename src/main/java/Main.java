import apiController.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import java.util.*;
import service.*;

public class Main {
  public static void main(String[] args) {
    runApi();
  }

  public static void runApi() {
    Injector injector = Guice.createInjector(new AppModule());

    UserApiController userApiController = injector.getInstance(UserApiController.class);
    ProblemApiController problemApiController = injector.getInstance(ProblemApiController.class);
    ContestApiController contestApiController = injector.getInstance(ContestApiController.class);
    SubmissionsApiController submissionsApiController =
        injector.getInstance(SubmissionsApiController.class);

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
