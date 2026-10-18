package service;

import exception.SubmissionNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import model.Submission;
import repositoryDataBase.SubmissionDataBaseRepository;

public class SubmissionService {
  public SubmissionDataBaseRepository submissionDataBaseRepository;

  public SubmissionService(SubmissionDataBaseRepository submissionDataBaseRepository) {
    this.submissionDataBaseRepository = submissionDataBaseRepository;
  }

  public Submission createSubmission(
      UUID userId, UUID problemId, String sourceCode, LocalDateTime submittedTime) {
    Submission submission = new Submission(userId, problemId, sourceCode, submittedTime, "");
    submissionDataBaseRepository.save(submission);
    return submission;
  }

  public Submission getSubmissionById(UUID id) {
    return submissionDataBaseRepository
        .findById(id)
        .orElseThrow(() -> new SubmissionNotFoundException("Couldn't find submission: " + id));
  }

  public List<Submission> getAllSubmissions() {
    return submissionDataBaseRepository.findAll();
  }

  public List<Submission> getAllSubmissionsOfUser(UUID id) {
    return submissionDataBaseRepository.getSubmissionsOfUser(id);
  }

  public List<Submission> getAllSubmissionsOfProblem(UUID id) {
    return submissionDataBaseRepository.getSubmissionsOfProblem(id);
  }

  public List<Submission> getAllSubmissionOfProblemFromUser(UUID problemId, UUID userId) {
    return submissionDataBaseRepository.getSubmissionsOfUserOfProblem(userId, problemId);
  }

  public boolean deleteSubmission(UUID id) {
    return submissionDataBaseRepository.deleteById(id);
  }
}
