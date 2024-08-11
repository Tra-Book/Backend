package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;
import Trabook.PlanManager.service.PlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Plan API", description = "API test for CRUD Plan")
@Slf4j
@RestController
@RequestMapping("/plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }


    @ResponseBody
    @PostMapping("/create")
    public String createPlan(@RequestBody Plan plan , List<Schedule> scheduleList) {
      log.info("planBody = {}",plan);
      return planService.createPlan(plan, scheduleList);
    }
    @ResponseBody
    @PostMapping("/delete")
    public String deletePlan(@RequestBody Plan planId) {
        log.info("delete plan = {}", planId);
        return planService.deletePlan(planId);
    }

    @ResponseBody
    @PostMapping("/list")
    public List<Plan> getUserPlanList(@RequestBody long userId) {
        List<Plan> userPlanList = planService.getUserPlanList(userId);
        log.info("{}'s plans = {}",userId, userPlanList);
        return userPlanList;
    }

    @ResponseBody
    @GetMapping("/like")
    public  List<Plan> getUserLikePlanList(@RequestBody long userId) {
        return planService.getUserLikePlanList(userId);
    }

    @ResponseBody
    @PostMapping("/like")
    public String likePlan(@RequestBody long userId, long planId) {
        return planService.likePlan(userId,planId);
    }

    @ResponseBody
    @GetMapping("/scrap")
    public List<Plan> scrapPlan(@RequestBody long userId, long planId) {
        return planService.getUserScrapPlanList(userId);
    }

    @ResponseBody
    @PostMapping("/scrap")
    public List<Plan> scrapPlan(@RequestBody long userId) {
        return planService.getUserScrapPlanList(userId);
    }

    @ResponseBody
    @GetMapping("/???")
    public List<Plan> getPlanListByCityId(@RequestBody long planId) {
        return planService.getPlanListByCityId(planId);
    }

}
