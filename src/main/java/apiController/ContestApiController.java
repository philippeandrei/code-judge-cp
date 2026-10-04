package apiController;

import dto.ContestResponseDto;
import dto.CreateContestRequest;
import dto.CreateProblemRequest;
import dto.ProblemResponseDto;
import exception.ContestNotFoundException;
import exception.ProblemNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.Contest;
import model.Problem;
import service.ContestService;

import java.util.UUID;

public class ContestApiController {
    public ContestService contestService;
    public ContestApiController(ContestService contestService){
        this.contestService = contestService;
    }
    public void create(Context ctx) {
        CreateContestRequest payload = ctx.
                bodyAsClass(CreateContestRequest.class);
        try {
            Contest created = contestService.createContest(
                    payload.getName(),
                    payload.getStartTime(),
                    payload.getEndTime(),
                    payload.getProblems()
            );

            ContestResponseDto response = new ContestResponseDto(created);
            ctx.status(HttpStatus.CREATED).json(response);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Exception: " + e);
        }

    }
    public void getAll(Context ctx) {
        String name = ctx.queryParam("name");
        if(name != null){
            try {
                Contest contest = contestService.getContestByName(name);
                ctx.json(contest);
            } catch (Exception e) {
                ctx.status(HttpStatus.NOT_FOUND).json("The contest can not be found");
            }
        }
    }

    public void getContestId(Context ctx){
        UUID id = UUID.fromString(ctx.pathParam("id"));
        try {
            ContestResponseDto response = new ContestResponseDto(contestService.getContestById(id));
            ctx.json(response);
        } catch (Exception e){
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
    public void addProblem(Context ctx){
        UUID id = UUID.fromString(ctx.pathParam("id"));
        UUID problemId = UUID.fromString(ctx.pathParam("problemId"));

        try {
            ContestResponseDto response = new ContestResponseDto(contestService.addProblemToContest(problemId, id));
            ctx.json(response);
        } catch (Exception e){
            ctx.status(HttpStatus.NOT_FOUND).json("Can not find contest  or problem with id " + e);
        }
    }


}
