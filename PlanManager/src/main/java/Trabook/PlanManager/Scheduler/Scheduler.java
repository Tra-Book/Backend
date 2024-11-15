package Trabook.PlanManager.Scheduler;

import Trabook.PlanManager.repository.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final PlanRepository planRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 180000)
    public void writeBackLike() {
        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> topPlans = zsetOps.rangeWithScores("topPlans", 0, -1);

        if(topPlans !=null) {
            for(ZSetOperations.TypedTuple<String> plan : topPlans) {
                String planKey = plan.getValue();
                int likes  = plan.getScore().intValue();

                long planId = Long.parseLong(planKey.split(":")[2]);
                log.info("update plan {} likes + {}",planId,likes);
                planRepository.updateLikes(planId,likes);
            }
        }
    }

}
