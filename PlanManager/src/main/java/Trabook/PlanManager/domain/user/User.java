package Trabook.PlanManager.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {
    private long userId;
    private String username;
    private String status_message;
    private String image;

    public User() {}

    public User(long userId, String username, String status_message, String image) {
        this.userId = userId;
        this.username = username;
        this.status_message = status_message;
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", status_message='" + status_message + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
