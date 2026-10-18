package apiController;

import dto.CreateSubmissionRequest;
import dto.SubmissionResponseDto;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import model.Submission;
import model.SubmissionStatusEnum;
import service.JudgeService;
import service.SubmissionQueueService;
import service.SubmissionService;

import static model.SubmissionStatusEnum.PENDING;

public class SubmissionsApiController {
  public JudgeService judgeService;
  public SubmissionService submissionService;
  public SubmissionQueueService submissionQueueService;
  public SubmissionsApiController(JudgeService judgeService, SubmissionService submissionService, SubmissionQueueService submissionQueueService) {
    this.judgeService = judgeService;
    this.submissionService = submissionService;
    this.submissionQueueService = submissionQueueService;
  }

  public void submit(Context ctx) throws Exception {
    CreateSubmissionRequest req = ctx.bodyAsClass(CreateSubmissionRequest.class);

      Submission submission = submissionService.createSubmission(req.getUserId(), req.getProblemId(), req.getSourceCode(), LocalDateTime.now());
      submission.setStatus(SubmissionStatusEnum.PENDING);
      submissionService.submissionDataBaseRepository.save(submission);

      submissionQueueService.enqueue(submission.getId());

      ctx.status(HttpStatus.ACCEPTED).json(submission);
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
