package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.destination.DestinationRedisService;
import Trabook.PlanManager.service.destination.DestinationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/plan/admin")
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
    private final PlanService planService;

    @Autowired
    public AdminController(@Qualifier("topRedisTemplate") RedisTemplate<String, String> redisTemplate,
                           DestinationService destinationService,DestinationRedisService destinationRedisService,PlanService planService) {
        this.redisTemplate = redisTemplate;
        this.destinationService = destinationService;
        this.destinationRedisService = destinationRedisService;
        this.planService = planService;
    }
/*
    @Scheduled(cron = "0 * * * * *")
    //@Scheduled(cron = "0 0 0/1 * * *")
    //@ResponseBody
    //@GetMapping("/updateHottestPlan")
    public void updateHottestPlan()  {
        List<PlanListResponseDTO> TopPlans = planService.getHottestPlan(0L);
        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
        objectMapper.registerModule(new JavaTimeModule());

        for(PlanListResponseDTO plan : TopPlans){
            System.out.println("insert" + plan.getPlanTitle() + " and like is " + plan.getLikes());
            try {
                String planString = objectMapper.writeValueAsString(plan);
                int score = plan.getLikes();
                Boolean add = zsetOps.add("topPlans", planString, score);
                System.out.println(add);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }





    //상위 5개 여행지 가져오고 레디스 서버에 업데이트
    //@Scheduled(cron = "0 0 0/1 * * * ")
    //@ResponseBody
    //@GetMapping("/updateHottestPlace")
    @Scheduled(cron = "0 * * * * *")
    public void updateHottestPlace()  {

        List<PlaceForModalDTO> TopPlaces = destinationService.getHottestPlace(0L); //상위 10개 순위 데이터
        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
       for(PlaceForModalDTO place : TopPlaces){
            System.out.println("insert"+place.getPlace().getPlaceName()+ " and score is "+ place.getPlace().getRatingScore() );
            try {
                String placeString = objectMapper.writeValueAsString(place.getPlace());
                Double score = place.getPlace().getRatingScore();
                Boolean add = zsetOps.add("topPlaces", placeString, score);
                System.out.println(add);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }


 */



}
