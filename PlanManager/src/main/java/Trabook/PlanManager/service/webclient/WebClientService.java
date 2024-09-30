package Trabook.PlanManager.service.webclient;

import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.domain.webclient.userInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
@Slf4j
@Service
public class WebClientService {

    public User getUserInfo(long userId) {

        URI uri = UriComponentsBuilder
                .fromUriString("http://35.216.95.239:4060")
                .path("/auth/fetch-user")
                .queryParam("userId",userId)
                .encode(Charset.defaultCharset())
                .build()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<userInfoDTO> result = restTemplate.getForEntity(uri, userInfoDTO.class);
        System.out.println(result.toString());
        return result.getBody().getUser();
    }

}
