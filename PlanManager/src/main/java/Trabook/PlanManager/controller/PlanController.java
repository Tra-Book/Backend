package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.webclient.WebClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

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

    @ResponseBody
    @PostMapping("/create")
    public PlanIdResponseDTO createPlan(@RequestBody PlanCreateDTO planCreateDTO,@RequestHeader("userId") long userId) {
        System.out.println(userId);
        planCreateDTO.setUserId(userId);
        long planId = planService.createPlan(planCreateDTO);
        PlanIdResponseDTO planIdResponseDTO = new PlanIdResponseDTO(planId);
        return planIdResponseDTO;
        //return 0;
    }

    @ResponseBody
    @PostMapping("/update")
    public PlanIdResponseDTO updatePlan(@RequestBody Plan plan){
        System.out.println(plan);
        long planId = planService.updatePlan(plan);
        return new PlanIdResponseDTO(planId);
    }

    @ResponseBody
    @GetMapping("/")
    public PlanResponseDTO getPlanByPlanId(@RequestParam("planId") long planId, @RequestHeader("userId") long userId) {

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

    @ResponseBody
    @PostMapping("/like")
    public String likePlan(@RequestBody PlanReactionDTO planReactionDTO,@RequestHeader("userId") long userId) {
        planReactionDTO.setUserId(userId);
        return planService.likePlan(planReactionDTO);
    }



    @ResponseBody
    @PostMapping("/scrap")
    public String scrapPlan(@RequestBody PlanReactionDTO planReactionDTO,@RequestHeader("userId") long userId) {
        planReactionDTO.setUserId(userId);
        return planService.scrapPlan(planReactionDTO);
    }

    @ResponseBody
    @PostMapping("/comment")
    public String addComment(@RequestBody Comment comment, @RequestHeader("userId") long userId) {
        comment.setUserId(userId);
        return planService.addComment(comment);
    }

    @ResponseBody
    @DeleteMapping("/")
    public String deletePlan(@RequestParam("planId") long planId,@RequestHeader("userId") long userId) {
        //계획과 유저 일치하는 로직 추가
        return planService.deletePlan(planId);
    }

    @ResponseBody
    @DeleteMapping("/like")
    public String deleteLike(@RequestHeader("userId") long userId, @RequestParam("planId") long planId){
        return planService.deleteLike(userId,planId);
    }

    @ResponseBody
    @DeleteMapping("/scrap")
    public String deleteScrap(@RequestHeader("userId") long userId, @RequestParam("planId") long planId) {
        return planService.deleteScrap(userId,planId);

    }

    @ResponseBody
    @DeleteMapping("/comment")
    public String deleteComment(@RequestParam("commentId") long commentId) {
        //유저아이디랑 댓글 아이디 일치여부 로직 추가
        return planService.deleteComment(commentId);
    }
}
