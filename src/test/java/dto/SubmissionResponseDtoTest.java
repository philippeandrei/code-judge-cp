package dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.UUID;
import model.Submission;
import org.junit.jupiter.api.Test;

class SubmissionResponseDtoTest {
  @Test
  void includesSubmissionIdInResponse() {
    UUID submissionId = UUID.randomUUID();
    Submission submission =
        new Submission(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "class Solution {}",
            LocalDateTime.now(),
            "",
            "java");
    submission.setId(submissionId);

    SubmissionResponseDto response = new SubmissionResponseDto(submission);

    assertEquals(submissionId, response.getId());
  }
}
