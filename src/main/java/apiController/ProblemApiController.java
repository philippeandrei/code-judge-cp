package apiController;

import dto.CreateProblemRequest;
import dto.ProblemResponseDto;
import exception.ProblemNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.UUID;
import model.Problem;
import service.ProblemService;

public class ProblemApiController {
  private final ProblemService problemService;

  public ProblemApiController(ProblemService problemService) {
    this.problemService = problemService;
  }

  public void create(Context ctx) {
    CreateProblemRequest payload = ctx.bodyAsClass(CreateProblemRequest.class);

    Problem created =
        problemService.createProblem(
            payload.getTitle(),
            payload.getStatement(),
            payload.getTimeLimitMs(),
            payload.getMemoryLimitKb(),
            payload.getTestCases(),
            payload.getCategories());

    // Map Problem entity to ProblemResponseDto
    ProblemResponseDto response = new ProblemResponseDto(created);
    ctx.status(HttpStatus.CREATED).json(response);
  }

  public void getAll(Context ctx) {
    String title = ctx.queryParam("title");
    if (title != null) {
      try {
        Problem problem = problemService.getProblemByTitle(title);
        ctx.json(problem);
      } catch (Exception e) {
        ctx.status(HttpStatus.NOT_FOUND).json("The problem can not be found");
      }
    }
  }

  public void getProblemId(Context ctx) {
    UUID id = UUID.fromString(ctx.pathParam("id"));
    try {
      ProblemResponseDto response = new ProblemResponseDto(problemService.getProblemById(id));
      ctx.json(response);
    } catch (Exception e) {
      ctx.status(HttpStatus.NOT_FOUND).json("Can not find problem with id");
    }
  }

  public void delete(Context ctx) {
    String payload = ctx.pathParamAsClass("id", String.class).get();
    UUID id = UUID.fromString(payload);
    try {
      problemService.deleteProblemById(id);
      ctx.status(HttpStatus.NO_CONTENT);
    } catch (ProblemNotFoundException e) {
      ctx.status(HttpStatus.NOT_FOUND).json("Problem with id not found");
    }
  }
}
