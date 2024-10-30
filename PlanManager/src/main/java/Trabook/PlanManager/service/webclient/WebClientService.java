package Trabook.PlanManager.service.webclient;

import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.domain.webclient.UserListDTO;
import Trabook.PlanManager.domain.webclient.userInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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


        /*

        URI uri = UriComponentsBuilder
                .fromUriString("http://10.178.0.4:4060")
                .path("/auth/fetch-user")
                .queryParam("userId",userId)
                .encode(Charset.defaultCharset())
                .build()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<userInfoDTO> result = restTemplate.getForEntity(uri, userInfoDTO.class);
        System.out.println(result.toString());
        return result.getBody().getUser();



         */
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

}
