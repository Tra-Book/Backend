package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.domain.webclient.userInfoDTO;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.planList.GetUserLikePlanList;
import Trabook.PlanManager.service.planList.GetUserPlanList;
import Trabook.PlanManager.service.planList.GetUserScrapPlanList;
import Trabook.PlanManager.service.planList.PlanListServiceInterface;
import Trabook.PlanManager.service.webclient.WebClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Plan API", description = "API test for CRUD Plan")
@Slf4j
@RestController
@RequestMapping("/plan")
public class PlanController {

    private final WebClientService webClientService;
    private final PlanService planService;

    @Autowired
    public PlanController(WebClientService webClientService, PlanService planService) {
        this.webClientService = webClientService;
        this.planService = planService;
    }

/*
    private final Map<String, PlanListServiceInterface> planListServiceInterfaceMap;


    //PlanListServiceInterface 인터페이스를 구현한 모든 서비스들이 자동으로 주입됨. 스프링이 자동으로 이 인터페이스를 구현한
    //모든 빈을 찾아서 리스트로 제공한다 ㄷㄷ..
    @Autowired
    public PlanController(List <PlanListServiceInterface> planListService, PlanService planService,WebClientService webClientService) {
        this.webClientService= webClientService;
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



 */
    @ResponseBody
    @PostMapping("/create")
    public long createPlan(@RequestBody PlanCreateDTO planCreateDTO) {
        return planService.createPlan(planCreateDTO);
    }

    @ResponseBody
    @PostMapping("/update")
    public long updatePlan(@RequestBody Plan plan){
        System.out.println(plan);
        return planService.updatePlan(plan);
    }

    @ResponseBody
    @GetMapping("/")
    public PlanResponseDTO getPlanByPlanId(@RequestParam("planId") long planId, @RequestParam("userId") long userId) {

        PlanResponseDTO result = planService.getPlan(planId, userId);

        long planOwnerId = result.getPlan().getUserId();
        User userInfo = webClientService.getUserInfo(planOwnerId);
        result.setUser(userInfo);
        return result;

    }

    @ResponseBody
    @PostMapping("/test")
    public void scrap(@RequestParam("planId") long planId) {
        planService.deleteLike(3,planId);
    }

/*
    @ResponseBody
    @GetMapping("/plans")
    public List<PlanListResponseDTO> getPlanList(@RequestBody PlanListRequestDTO planListRequestDTO) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(planListRequestDTO.getType());
        List<PlanListResponseDTO> planList = planListService.getPlanList(planListRequestDTO.getUserId());
        log.info("{}'s plans = {}", planListRequestDTO.getUserId(), planList);
        return planList;
    }


 */
    @ResponseBody
    @PostMapping("/like")
    public String likePlan(@RequestBody PlanReactionDTO planReactionDTO) {
        return planService.likePlan(planReactionDTO);
    }



    @ResponseBody
    @PostMapping("/scrap")
    public String scrapPlan(@RequestBody PlanReactionDTO planReactionDTO) {
        return planService.scrapPlan(planReactionDTO);
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
