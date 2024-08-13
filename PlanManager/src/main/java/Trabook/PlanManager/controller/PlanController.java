package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanList;
import Trabook.PlanManager.domain.plan.Schedule;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.planList.GetUserLikePlanList;
import Trabook.PlanManager.service.planList.GetUserPlanList;
import Trabook.PlanManager.service.planList.GetUserScrapPlanList;
import Trabook.PlanManager.service.planList.PlanListServiceInterface;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Plan API", description = "API test for CRUD Plan")
@Slf4j
@RestController
@RequestMapping("/plan")
public class PlanController {

    private final PlanService planService;
    private final Map<String, PlanListServiceInterface> planListServiceInterfaceMap;


    //PlanListServiceInterface 인터페이스를 구현한 모든 서비스들이 자동으로 주입됨. 스프링이 자동으로 이 인터페이스를 구현한
    //모든 빈을 찾아서 리스트로 제공한다 ㄷㄷ..
    @Autowired
    public PlanController(List <PlanListServiceInterface> planListService, PlanService planService) {
        this.planService = planService;
        this.planListServiceInterfaceMap = planListService.stream().collect(Collectors.toMap(
                service -> {
                    if(service instanceof GetUserLikePlanList) return "likes";
                    if(service instanceof GetUserPlanList) return "user";
                    if(service instanceof GetUserScrapPlanList) return "scrap";
                    return "default";
                },
                service -> service //?? 이문구 문법적으로 알아보기
                //stream.collect.Coolectore.toMap 이것도 문법적으로 닷 ㅣ알아보기
        ));

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
    public List<Plan> getUserPlanList(@RequestBody PlanList planList) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(planList.getType());
        List<Plan> userPlanList = planListService.getPlanList(planList.getUserId());
        log.info("{}'s plans = {}",planList.getUserId(), userPlanList);

        return userPlanList;
    }

    @ResponseBody
    @GetMapping("/like")
    public  List<Plan> getUserLikePlanList(@RequestBody PlanList planList) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(planList.getType());
        return planListService.getPlanList(planList.getUserId());
        //return planService.getUserLikePlanList(userId);
    }

    @ResponseBody
    @GetMapping("/scrap")
    public List<Plan> getUserScrapPlanList(@RequestBody PlanList planList) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(planList.getType());
        return planListService.getPlanList(planList.getUserId());
    }

    @ResponseBody
    @PostMapping("/like")
    public String likePlan(@RequestBody long userId, long planId) {
        return planService.likePlan(userId,planId);
    }



    @ResponseBody
    @PostMapping("/scrap")
    public String scrapPlan(@RequestBody long userId, long planId) {
        return planService.scrapPlan(userId,planId);
    }

    @ResponseBody
    @GetMapping("/???")
    public List<Plan> getPlanListByCityId(@RequestBody long planId) {
        return planService.getPlanListByCityId(planId);
    }

}
