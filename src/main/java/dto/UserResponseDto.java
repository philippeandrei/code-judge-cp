package dto;

import model.User;

public class UserResponseDto {
    private String username;
    private String email;
    public UserResponseDto(){}

    public UserResponseDto(String username, String email){
        this.username = username;
        this.email = email;
    }

    public UserResponseDto(User user){
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return username;
    }

    public void setUser(String user) {
        this.username = user;
    }
}
