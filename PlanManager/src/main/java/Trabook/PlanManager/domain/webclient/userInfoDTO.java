package Trabook.PlanManager.domain.webclient;

import Trabook.PlanManager.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
public class userInfoDTO {
    private User user;

    private userInfoDTO() {}

    public userInfoDTO(User user) {
        this.user = user;
    }
}
