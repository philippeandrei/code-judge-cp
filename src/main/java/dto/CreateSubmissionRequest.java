package dto;

import java.util.UUID;

public class CreateSubmissionRequest {
  private UUID userId;
  private UUID problemId;
  private String sourceCode;

  public CreateSubmissionRequest() {}

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getProblemId() {
    return problemId;
  }

  public void setProblemId(UUID problemId) {
    this.problemId = problemId;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }
}
