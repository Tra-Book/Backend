package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.plan.HottestPlanContentsInRedis;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.service.destination.PointDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PlanRedisService {

    private final PlanRepository planRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public PlanRedisService(RedisTemplate<String, String> redisTemplate, PlanRepository planRepository) {
        this.redisTemplate = redisTemplate;
        this.planRepository = planRepository;
    }

    private ObjectMapper objectMapper = new ObjectMapper();


    public List<PlanListResponseDTO> getHottestPlan(){
        objectMapper.registerModule(new SimpleModule().addDeserializer(Point.class, new PointDeserializer()));
        objectMapper.registerModule(new JavaTimeModule());
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        List<String> topPlans = hashOps.values("plans");

        List<PlanListResponseDTO> top10Plans = new ArrayList<>();

        try {
            for (String jsonPlan : topPlans) {
                 PlanListResponseDTO plan = objectMapper.readValue(jsonPlan, PlanListResponseDTO.class);
                top10Plans.add(plan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        for(PlanListResponseDTO plan : top10Plans){
            if(userId == null){
                plan.setIsScrapped(false);
                plan.setIsLiked(false);
            } else {
                plan.setIsLiked(planRepository.isLiked(plan.getPlanId(), userId));
                plan.setIsScrapped(planRepository.isScrapped(plan.getPlanId(), userId));
            }
        }


         */
        return top10Plans;
    }

    public void updatePlanInRanking(HottestPlanContentsInRedis plan) throws JsonProcessingException {

        HashOperations<String,String,String> hashops = redisTemplate.opsForHash();
        objectMapper.registerModule(new JavaTimeModule());
        String planString = objectMapper.writeValueAsString(plan);
        String planKey = "plan:content:" + plan.getPlanId();
        hashops.put("plans",planKey,planString);
    }

}
