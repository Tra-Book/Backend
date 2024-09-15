package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.plan.Plan;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PlanServiceTest {

    @Autowired PlanService planService;
/*
    @Test
    void createPlan() {
        //given
        Plan plan = new Plan(-1, 3, 1, true, 0, 0, "2024-08-20 10:00:00", "test", "test");

        //when
        long savedPlanId = planService.createPlan(plan, null);//plan with no schedule
        Plan getPlan = planService.getPlan(savedPlanId).orElseThrow(() -> new AssertionError("Plan not found"));
        //then
        Assertions.assertThat(getPlan).usingRecursiveComparison().isEqualTo(plan);
    }

    @Test
    void getPlan() {
        //given
        long noPlanId = 0;
        Plan plan = new Plan(-1, 3, 1, true, 0, 0, "2024-08-20 10:00:00", "test", "test");
        long existingPlanId =planService.createPlan(plan,null);
        //when
        Optional<Plan> planFound = planService.getPlan(existingPlanId);
        Optional<Plan> noPlan = planService.getPlan(noPlanId);
        //then
        Assertions.assertThat(noPlan).isEmpty();
        Assertions.assertThat(planFound.get().getPlanId()).isEqualTo(existingPlanId);
    }

    @Test
    void deletePlan() {
        //given
        Plan plan = new Plan(-1, 3, 1, true, 0, 0, "2024-08-20 10:00:00", "test", "test");
        long planId = planService.createPlan(plan, null);
        //when
        planService.deletePlan(planId);
        Optional<Plan> result = planService.getPlan(planId);
        //then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void likeTest() {
        //given
        long like = 0;
        Plan plan = new Plan(-1, 3, 1, true, 0, 0, "2024-08-20 10:00:00", "test", "test");
        long planId = planService.createPlan(plan, null);
        long userId = 3;

        //when
        String result1 = planService.likePlan(userId, planId);
        String result2 = planService.likePlan(userId, planId);
        Plan updatedPlan = planService.getPlan(planId).get();
        long updatedLike = updatedPlan.getLikes();
        //then
        //좋아요 성공 여부 ,좋아요 중복 여부, 좋아요 + 1 되었는지 확인
        Assertions.assertThat(result1).isEqualTo("like complete");
        Assertions.assertThat(result1).isNotEqualTo(result2);
        Assertions.assertThat(like + 1).isEqualTo(updatedLike);
    }
    @Test
    void scrapTest() {
        //given
        long scrap = 0;
        Plan plan = new Plan(-1, 3, 1, true, 0, 0, "2024-08-20 10:00:00", "test", "test");
        long planId = planService.createPlan(plan, null);
        long userId = 3;
        long userId2 = 4;
        //when1
        String result1 = planService.scrapPlan(userId, planId);
        String result2 = planService.scrapPlan(userId, planId);
        String result3 = planService.scrapPlan(userId,-1);
        Plan updatedPlan = planService.getPlan(planId).get();
        long updatedScraps = updatedPlan.getScraps();

        //then1
        Assertions.assertThat(result1).isEqualTo("scrap complete");
        Assertions.assertThat(result1).isNotEqualTo(result2);
        Assertions.assertThat(updatedScraps).isEqualTo(scrap+1);
        Assertions.assertThat(result3).isEqualTo("no plan exists");

        //when2
        String deleteResult1 = planService.deleteScrap(userId, planId);
        String deleteResult2 = planService.deleteScrap(userId, planId);

        //then2
        Assertions.assertThat(deleteResult1).isEqualTo("delete complete");
        Assertions.assertThat(deleteResult2).isEqualTo("error");

        //when3
        String ConcurrentResult1 = planService.scrapPlan(userId,planId);
        int a = planService.getPlan(planId).get().getScraps();
        String ConcurrentResult2 = planService.scrapPlan(userId2,planId);
        int b = planService.getPlan(planId).get().getScraps();
        //then3

        Assertions.assertThat(a).isEqualTo(b-1);
    }



 */

}