package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.service.destination.DestinationRedisService;
import Trabook.PlanManager.service.destination.DestinationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/placeAdmin")
public class AdminController {

    /*
    @Autowired
    private RedisOperations<String,Place> operations;

    @Resource(name="redisTemplate")
    private ZSetOperations<String,Place> zsetOps;
*/

    private ObjectMapper objectMapper = new ObjectMapper();

    private final RedisTemplate<String, String> redisTemplate;
    private final DestinationService destinationService;
    private final DestinationRedisService destinationRedisService;

    @Autowired
    public AdminController(@Qualifier("topPlaceRedisTemplate") RedisTemplate<String, String> redisTemplate,
                           DestinationService destinationService,DestinationRedisService destinationRedisService) {
        this.redisTemplate = redisTemplate;
        this.destinationService = destinationService;
        this.destinationRedisService = destinationRedisService;
    }



    //상위 5개 여행지 가져오고 레디스 서버에 업데이트
    @ResponseBody
    @GetMapping("/updateHottestPlace")
    public void updateHottestPlace()  {

        List<Place> TopPlaces = destinationService.getHottestPlace(); //상위 5개 순위 데이터
        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
        System.out.println("ok");
        for(Place place : TopPlaces){
            System.out.println("insert"+place.getPlaceName()+ "and score is "+ place.getRatingScore() );
            try {
                String placeString = objectMapper.writeValueAsString(place);
                Double score = place.getRatingScore();
                Boolean add = zsetOps.add("topPlaces", placeString, score);
                System.out.println(add);
            } catch (JsonProcessingException e) {

            }
        }



    }
/*
    @ResponseBody
    @GetMapping("/popular")
    public List<Place> getHottestPlace(){
        return destinationRedisService.getHottestPlace();

    }


 */

}
