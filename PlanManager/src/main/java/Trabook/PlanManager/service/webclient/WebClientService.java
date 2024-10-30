package Trabook.PlanManager.service.webclient;

import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.domain.webclient.UserListDTO;
import Trabook.PlanManager.domain.webclient.userInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebClientService {

    @Autowired
    WebClient webClient;

    public Mono<userInfoDTO> getUserInfo(long userId) {

        Mono<userInfoDTO> userInfoDTOMono = webClient.get()
                .uri("/auth/fetch-user?userId={userId}", userId)
                .retrieve()
                .bodyToMono(userInfoDTO.class);


        return userInfoDTOMono;


    }


    public Mono<List<User>> getUserListInfo(List<Long> userIdList) {
        String userIdsString = userIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        //System.out.println(userIdsString);
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/auth/fetch-users")
                                .queryParam("userIdList", userIdsString)
                                .build()
                )
                .retrieve()
                .bodyToMono(UserListDTO.class)
                .doOnNext(res -> log.info("get user list info: {}", res))
                .map(UserListDTO -> {
                    // Null 체크
                    if (UserListDTO == null || UserListDTO.getUsers() == null) {
                        // 적절한 처리 또는 대체값 반환
                        // System.out.println("null here");
                        return null;
                    }
                    return UserListDTO.getUsers();
                });
        //.map(UserListDTO::getUserList);


    }

    public User getUserInfoBlocking(long userId) {

        URI uri = UriComponentsBuilder
                .fromUriString("http://35.216.124.162:4060")
                .path("/auth/fetch-user")
                .queryParam("userId", userId)
                .encode(Charset.defaultCharset())
                .build()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<userInfoDTO> result = restTemplate.getForEntity(uri, userInfoDTO.class);
        System.out.println(result.toString());
        return result.getBody().getUser();
    }

    public List<User> getUserInfoListBlocking(List<Long> userIdList) {
        // User ID 리스트를 문자열로 변환
        String userIdsString = userIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // URI 구성
        URI uri = UriComponentsBuilder
                .fromUriString("http://35.216.124.162:4060")
                .path("/auth/fetch-users")
                .queryParam("userIdList", userIdsString)
                .encode(Charset.defaultCharset())
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        UserListDTO forObject = restTemplate.getForObject(uri, UserListDTO.class);
        return forObject.getUsers();


    }
}
