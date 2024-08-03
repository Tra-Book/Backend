package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.repository.PlanRepository;
import Trabook.PlanManager.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
      return planService.createPlan(plan);
    }
}
