package apiController;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dto.ContestResponseDto;
import dto.CreateContestRequest;
import exception.ContestNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import model.Contest;
import service.ContestService;

@Singleton
public class ContestApiController {
  public ContestService contestService;
  @Inject
  public ContestApiController(ContestService contestService) {
    this.contestService = contestService;
  }

  public void create(Context ctx) {
    CreateContestRequest payload = ctx.bodyAsClass(CreateContestRequest.class);
    try {
      Contest created =
          contestService.createContest(
              payload.getName(),
              payload.getStartTime(),
              payload.getEndTime(),
              payload.getProblems());

      ContestResponseDto response = new ContestResponseDto(created);
      ctx.status(HttpStatus.CREATED).json(response);
    } catch (Exception e) {
      ctx.status(HttpStatus.BAD_REQUEST).json("Exception: " + e);
    }
  }

  public void getAll(Context ctx) {
    String name = ctx.queryParam("name");
    if (name != null) {
      try {
        Contest contest = contestService.getContestByName(name);
        ctx.json(contest);
      } catch (Exception e) {
        ctx.status(HttpStatus.NOT_FOUND).json("The contest can not be found");
      }
    } else {
      try {
        List<Contest> contest = contestService.getAll();
        ctx.json(contest);
      } catch (Exception e) {
        ctx.status(HttpStatus.NOT_FOUND).json("No contests can be found");
      }
    }
  }

  public void getContestId(Context ctx) {
    UUID id = UUID.fromString(ctx.pathParam("id"));
    try {
      ContestResponseDto response = new ContestResponseDto(contestService.getContestById(id));
      ctx.json(response);
    } catch (Exception e) {
      ctx.status(HttpStatus.NOT_FOUND).json("Can not find contest with id");
    }
  }

  public void delete(Context ctx) {
    String payload = ctx.pathParamAsClass("id", String.class).get();
    UUID id = UUID.fromString(payload);
    try {
      contestService.deleteContestById(id);
      ctx.status(HttpStatus.NO_CONTENT);
    } catch (ContestNotFoundException e) {
      ctx.status(HttpStatus.NOT_FOUND).json("Contest with id not found");
    }
  }

  public void addProblem(Context ctx) {
    UUID id = UUID.fromString(ctx.pathParam("id"));
    UUID problemId = UUID.fromString(ctx.pathParam("problemId"));

    try {
      ContestResponseDto response =
          new ContestResponseDto(contestService.addProblemToContest(problemId, id));
      ctx.json(response);
    } catch (Exception e) {
      ctx.status(HttpStatus.NOT_FOUND).json("Can not find contest  or problem with id " + e);
    }
  }
}
