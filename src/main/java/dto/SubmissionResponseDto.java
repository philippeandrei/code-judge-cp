package dto;

import model.Submission;
import model.SubmissionStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;


public class SubmissionResponseDto {
    private UUID userId;
    private UUID problemId;
    private String sourceCode;
    private SubmissionStatusEnum status;
    private LocalDateTime submittedTime;

    public SubmissionResponseDto(){}
    public SubmissionResponseDto(Submission submission) {
        this.userId = submission.getUserId();
        this.problemId = submission.getProblemId();
        this.sourceCode = submission.getSourceCode();
        this.status = submission.getStatus();
        this.submittedTime = submission.getSubmittedTime();
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

    public LocalDateTime getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(LocalDateTime submittedTime) {
        this.submittedTime = submittedTime;
    }
}
