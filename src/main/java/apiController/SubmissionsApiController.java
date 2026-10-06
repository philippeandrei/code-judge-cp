package apiController;

import dto.CreateSubmissionRequest;
import dto.SubmissionResponseDto;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import model.Submission;
import service.JudgeService;
import service.SubmissionService;

public class SubmissionsApiController {
  public JudgeService judgeService;
  public SubmissionService submissionService;

  public SubmissionsApiController(JudgeService judgeService, SubmissionService submissionService) {
    this.judgeService = judgeService;
    this.submissionService = submissionService;
  }

  public void submit(Context ctx) throws Exception {
    CreateSubmissionRequest req = ctx.bodyAsClass(CreateSubmissionRequest.class);

    try {
      Path tempDir = Files.createTempDirectory("judge_submission_");
      Path sourceFilePath = tempDir.resolve("Solution.java");
      Files.writeString(sourceFilePath, req.getSourceCode());

      // 2. Run the judge service with that file path
      Submission submission =
          judgeService.submitProblem(
              req.getUserId(), req.getProblemId(), sourceFilePath.toString());

      // 3. Return the result
      if (submission == null) {
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Failed to process submission");
        return;
      }
      ctx.status(HttpStatus.CREATED)
          .json(submission);

    } catch (Exception e) {
      ctx.status(HttpStatus.BAD_REQUEST).json("Exception returned: " + e);
    }
  }

  public void getById(Context ctx) {
    UUID id = UUID.fromString(ctx.pathParam("id"));
    try {
      SubmissionResponseDto response =
          new SubmissionResponseDto(submissionService.getSubmissionById(id));
      ctx.json(response);
    } catch (Exception e) {
      ctx.status(HttpStatus.NOT_FOUND).json("Can not find submissions with id");
    }
  }

  public void getSubmissionsOfUser(Context ctx) {
    UUID id = UUID.fromString(ctx.queryParam("userId"));
    try {
      List<Submission> submissions = submissionService.getAllSubmissionsOfUser(id);
      ctx.json(submissions);
    } catch (Exception e) {
      ctx.status(HttpStatus.NOT_FOUND).json("Exception " + e);
    }
  }
}
