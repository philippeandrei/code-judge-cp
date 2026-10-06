package model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Submission {
  private UUID id;
  private UUID userId;
  private UUID problemId;
  private String sourceCode;
  private SubmissionStatusEnum status;
  private LocalDateTime submittedTime;
  private String errorMessage;

  public Submission() {}

  public Submission(
      UUID userId,
      UUID problemId,
      String sourceCode,
      LocalDateTime submittedTime,
      String errorMessage) {
    this.id = UUID.randomUUID();
    this.userId = userId;
    this.problemId = problemId;
    this.sourceCode = sourceCode;
    this.status = SubmissionStatusEnum.PENDING;
    this.submittedTime = submittedTime;
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Submission that = (Submission) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  public LocalDateTime getSubmittedTime() {
    return submittedTime;
  }

  public void setSubmittedTime(LocalDateTime submittedTime) {
    this.submittedTime = submittedTime;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

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

  public SubmissionStatusEnum getStatus() {
    return status;
  }

  public void setStatus(SubmissionStatusEnum status) {
    this.status = status;
  }
}
