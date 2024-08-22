package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanListRequestDTO;
import Trabook.PlanManager.domain.plan.PlanRequestDTO;
import Trabook.PlanManager.domain.plan.Schedule;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.planList.GetUserLikePlanList;
import Trabook.PlanManager.service.planList.GetUserPlanList;
import Trabook.PlanManager.service.planList.GetUserScrapPlanList;
import Trabook.PlanManager.service.planList.PlanListServiceInterface;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public long createPlan(@RequestBody PlanRequestDTO planRequestDTO) {
       // System.out.println("ok");
        //log.info("{}",planRequestDTO);
      return planService.createPlan(planRequestDTO.getPlan(), planRequestDTO.getScheduleList());
    }

    @ResponseBody
    @GetMapping("/")
    public Plan getPlanByPlanId(@RequestParam("planId") long planId) {
        return planService.getPlan(planId)
                .orElseThrow(()-> new EntityNotFoundException("Plan not found"));
    }
    @ResponseBody
    @PostMapping("/test")
    public void scrap(@RequestParam("planId") long planId) {
        planService.deleteLike(3,planId);
    }


    @ResponseBody
    @GetMapping("/plans")
    public List<Plan> getUserPlanList(@RequestBody PlanListRequestDTO planListRequestDTO) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(planListRequestDTO.getType());
        List<Plan> userPlanList = planListService.getPlanList(planListRequestDTO.getUserId());
        log.info("{}'s plans = {}", planListRequestDTO.getUserId(), userPlanList);

        return userPlanList;
    }


    @ResponseBody
    @GetMapping("/like")
    public  List<Plan> getUserLikePlanList(@RequestBody PlanListRequestDTO planListRequestDTO) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(planListRequestDTO.getType());
        //System.out.println(planListService);
        return planListService.getPlanList(planListRequestDTO.getUserId());
        //return planService.getUserLikePlanList(userId);
    }

    @ResponseBody
    @GetMapping("/scrap")
    public List<Plan> getUserScrapPlanList(@RequestBody PlanListRequestDTO planListRequestDTO) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(planListRequestDTO.getType());
        return planListService.getPlanList(planListRequestDTO.getUserId());
    }

    @ResponseBody
    @PostMapping("/like")
    public String likePlan(@RequestParam("userId") long userId,@RequestParam("planId") long planId) {

        return planService.likePlan(userId,planId);
    }



    @ResponseBody
    @PostMapping("/scrap")
    public String scrapPlan(@RequestParam("userId") long userId,@RequestParam("planId") long planId) {
        return planService.scrapPlan(userId,planId);
    }

    @ResponseBody
    @PostMapping("/comment")
    public String addComment(@RequestBody Comment comment) {
        return planService.addComment(comment);
    }

    @ResponseBody
    @DeleteMapping("/")
    public String deletePlan(@RequestParam("planId") long planId) {
        return planService.deletePlan(planId);
    }

    @ResponseBody
    @DeleteMapping("/like")
    public String deleteLike(@RequestParam("userId") long userId, @RequestParam("planId") long planId){
        return planService.deleteLike(userId,planId);
    }

    @ResponseBody
    @DeleteMapping("/scrap")
    public String deleteScrap(@RequestParam("userId") long userId, @RequestParam("planId") long planId) {
        return planService.deleteScrap(userId,planId);

    }

    @ResponseBody
    @DeleteMapping("/comment")
    public String deleteComment(@RequestParam("commentId") long commentId) {
        return planService.deleteComment(commentId);
    }
}
