package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.service.destination.PointDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PlanRedisService {
    private final PlanRepository planRepository;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public PlanRedisService(@Qualifier("topRedisTemplate")RedisTemplate<String, String> redisTemplate, PlanRepository planRepository) {
        this.redisTemplate = redisTemplate;
        this.planRepository = planRepository;
    }

    private ObjectMapper objectMapper = new ObjectMapper();


    public List<PlanListResponseDTO> getHottestPlan(Long userId){
        objectMapper.registerModule(new SimpleModule().addDeserializer(Point.class, new PointDeserializer()));
        objectMapper.registerModule(new JavaTimeModule());
        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
        Set<String> topPlans = zsetOps.reverseRange("topPlans", 0, 9);

        List<PlanListResponseDTO> top10Plans = new ArrayList<>();

        try {
            for (String jsonPlan : topPlans) {

                //System.out.println(jsonPlan);

                 PlanListResponseDTO plan = objectMapper.readValue(jsonPlan, PlanListResponseDTO.class);
                top10Plans.add(plan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(PlanListResponseDTO plan : top10Plans){
            if(userId == null){
                plan.setIsScrapped(false);
                plan.setIsLiked(false);
            } else {
                plan.setIsLiked(planRepository.isLiked(plan.getPlanId(), userId));
                plan.setIsScrapped(planRepository.isScrapped(plan.getPlanId(), userId));
            }
        }
        return top10Plans;
    }
}
