package dto;

import java.util.UUID;

public class DeleteUserRequest {
  private UUID id;

  public DeleteUserRequest() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
}
