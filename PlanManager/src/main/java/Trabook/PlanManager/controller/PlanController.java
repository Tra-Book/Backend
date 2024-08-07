package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.repository.PlanRepository;
import Trabook.PlanManager.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String savePlan(@RequestBody Plan plan) {
      log.info("planBody = {}",plan);
      return planService.createPlan(plan, scheduleList);
    }
    @ResponseBody
    @PostMapping("/delete")
    public String deletePlan(@RequestBody Plan plan) {
        log.info("delete plan = {}", plan);
        return planService.createPlan(plan);
    }

    @ResponseBody
    @PostMapping("/list")
    public List<Plan> getUserPlanList(@RequestBody String userId) {
        List<Plan> userPlanList = planService.getUserPlanList(userId);
        log.info("{}'s plans = {}",userId, userPlanList);
        return userPlanList;
    }

    @ResponseBody
    @GetMapping("/like")
    public  List<Plan> getUserLikePlanList(@RequestBody String userId) {
        return planService.getUserLikePlanList(userId);
    }

    @ResponseBody
    @PostMapping("/like")
    public String likePlan(@RequestBody String userId, String planId) {
        return planService.likePlan(userId,planId);
    }

    @ResponseBody
    @GetMapping("/scrap")
    public String scrapPlan(@RequestBody String userId, String planId) {
        return planService.get(userId,planId);
    }

    @ResponseBody
    @PostMapping("/scrap")
    public String scrapPlan(@RequestBody String userId) {
        return planService.getUserScrapPlanList(userId);
    }


}
