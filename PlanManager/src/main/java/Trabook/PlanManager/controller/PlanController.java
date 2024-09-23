package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.domain.user.User;

import Trabook.PlanManager.response.PlanIdResponseDTO;
import Trabook.PlanManager.response.PlanResponseDTO;
import Trabook.PlanManager.response.ResponseMessage;

import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.file.FileUploadService;
import Trabook.PlanManager.service.webclient.WebClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Plan API", description = "API test for CRUD Plan")
@Slf4j
@RestController
@RequestMapping("/plan")
public class PlanController {

    private final WebClientService webClientService;
    private final PlanService planService;
    private final FileUploadService fileUploadService;

    @Autowired
    public PlanController(WebClientService webClientService, PlanService planService, FileUploadService fileUploadService) {
        this.webClientService = webClientService;
        this.planService = planService;
        this.fileUploadService = fileUploadService;
    }

    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<PlanIdResponseDTO> createPlan(@RequestBody PlanCreateDTO planCreateDTO, @RequestHeader("userId") long userId) {
        //System.out.println(userId);
        planCreateDTO.setUserId(userId);
        long planId = planService.createPlan(planCreateDTO);
        PlanIdResponseDTO planIdResponseDTO = new PlanIdResponseDTO(planId);
        return new ResponseEntity<>(planIdResponseDTO, HttpStatus.OK);

    }

    @ResponseBody
    @PatchMapping("/update")
    public ResponseEntity<PlanIdResponseDTO> updatePlan(@RequestPart("plan") Plan plan,
                                                        @RequestPart("image") MultipartFile image){
        log.info("{}",plan.isFinished());
        long planId = planService.updatePlan(plan);

        try {
            fileUploadService.uploadPlanImage(image, planId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PlanIdResponseDTO planIdResponseDTO = new PlanIdResponseDTO(planId);

        return new ResponseEntity<>(planIdResponseDTO,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/")
    public ResponseEntity<PlanResponseDTO> getPlanByPlanId(@RequestParam("planId") long planId, @RequestHeader(value = "userId") long userId) {

        PlanResponseDTO result = planService.getPlan(planId, userId);
        long planOwnerId = result.getPlan().getUserId();
        result.setTags(planService.getTags(result));
        User userInfo = webClientService.getUserInfo(planOwnerId);
        result.setUser(userInfo);
        return ResponseEntity.ok(result);
    }

    @ResponseBody
    @PostMapping("/test")
    public ResponseEntity<ResponseMessage> scrap(@RequestParam("planId") long planId) {
        planService.deleteLike(3,planId);
        return ResponseEntity.ok(new ResponseMessage("OK"));
    }

    @ResponseBody
    @PostMapping("/like")
    public ResponseEntity<ResponseMessage> likePlan(@RequestBody PlanReactionDTO planReactionDTO,@RequestHeader("userId") long userId) {
        planReactionDTO.setUserId(userId);
        String message = planService.likePlan(planReactionDTO);
        return ResponseEntity.ok(new ResponseMessage(message));

    }



    @ResponseBody
    @PostMapping("/scrap")
    public ResponseEntity<ResponseMessage> scrapPlan(@RequestBody PlanReactionDTO planReactionDTO,@RequestHeader(value = "userId") long userId) {
        planReactionDTO.setUserId(userId);
        String message = planService.scrapPlan(planReactionDTO);
        return ResponseEntity.ok(new ResponseMessage(message));

    }

    @ResponseBody
    @PostMapping("/comment")
    public  ResponseEntity<ResponseMessage> addComment(@RequestBody Comment comment, @RequestHeader("userId") long userId) {
        comment.setUserId(userId);
        String message = planService.addComment(comment);
        return ResponseEntity.ok(new ResponseMessage(message));
    }

    @ResponseBody
    @DeleteMapping("/")
    public ResponseEntity<ResponseMessage> deletePlan(@RequestParam("planId") long planId,@RequestHeader("userId") long userId) {
        //계획과 유저 일치하는 로직 추가
        String message = planService.deletePlan(planId);
        return ResponseEntity.ok(new ResponseMessage(message));
    }

    @ResponseBody
    @DeleteMapping("/like")
    public ResponseEntity<ResponseMessage> deleteLike(@RequestHeader("userId") long userId, @RequestParam("planId") long planId){
        String message = planService.deleteLike(userId, planId);
        return ResponseEntity.ok(new ResponseMessage(message));

    }

    @ResponseBody
    @DeleteMapping("/scrap")
    public ResponseEntity<ResponseMessage> deleteScrap(@RequestHeader("userId") long userId, @RequestParam("planId") long planId) {
        String message = planService.deleteScrap(userId, planId);
        return ResponseEntity.ok(new ResponseMessage(message));
    }

    @ResponseBody
    @DeleteMapping("/comment")
    public ResponseEntity<ResponseMessage> deleteComment(@RequestParam("commentId") long commentId) {
        //유저아이디랑 댓글 아이디 일치여부 로직 추가
        String message = planService.deleteComment(commentId);
        return ResponseEntity.ok(new ResponseMessage(message));
    }
}
