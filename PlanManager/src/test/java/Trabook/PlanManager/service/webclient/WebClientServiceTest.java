package Trabook.PlanManager.service.webclient;

import Trabook.PlanManager.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;


class WebClientServiceTest {

    WebClientService webClientService = new WebClientService();

    @Test
    void getUserInfo() {
        //given
        User user = new User(3,"test","test");
        //when
        User userInfo = webClientService.getUserInfo(3);

        System.out.println(userInfo.toString());

        //then
        Assertions.assertThat(userInfo.getUserId()).isEqualTo(user.getUserId());
    }
}