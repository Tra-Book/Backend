package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.comment.CommentRequestDTO;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.domain.user.User;

import Trabook.PlanManager.response.*;

import Trabook.PlanManager.response.PlanIdResponseDTO;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.file.FileUploadService;
import Trabook.PlanManager.service.webclient.WebClientService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;


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
        PlanIdResponseDTO planIdResponseDTO = new PlanIdResponseDTO(planId,"create complete");
        return new ResponseEntity<>(planIdResponseDTO, HttpStatus.OK);

    }

    @ResponseBody
    @PatchMapping("/update")
    public ResponseEntity<PlanUpdateResponseDTO> updatePlan(@RequestPart("plan") Plan plan,
                                                            @RequestPart(value = "image",required = false) MultipartFile image) {

        long planId = planService.updatePlan(plan);
        if(planId == 0)
            return new ResponseEntity<>(new PlanUpdateResponseDTO(-1,"no plan exists",null), HttpStatus.NOT_FOUND);
        try {
            if(!image.isEmpty()) {
                //System.out.println("okok");
                fileUploadService.uploadPlanImage(image, planId);
            }
            else
                System.out.println("no image");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PlanUpdateResponseDTO planUpdateResponseDTO = new PlanUpdateResponseDTO(planId,"update complete",plan.getImgSrc());

        return new ResponseEntity<>(planUpdateResponseDTO,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("")
    public ResponseEntity<PlanResponseDTO> getPlanByPlanId(@RequestParam("planId")long planId, @RequestHeader(value = "userId", required = false) Long userId) {


        PlanResponseDTO result = planService.getPlan(planId, userId);

        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        long planOwnerId = result.getPlan().getUserId();
        result.setTags(planService.getTags(result.getPlan().getDayPlanList()));
        User userInfo = webClientService.getUserInfo(planOwnerId);
        result.setUser(userInfo);
        for(Comment comment : result.getComments() ) {
            long commentUserId = comment.getUser().getUserId();
            User commentUserInfo = webClientService.getUserInfo(commentUserId);
            comment.setUser(commentUserInfo);
        }

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
    public ResponseEntity<ResponseMessage> likePlan(@RequestBody PlanIdDTO planIdDTO, @RequestHeader("userId") long userId) {

        String message = planService.likePlan(planIdDTO.getPlanId(),userId);
        if (Objects.equals(message,"like already exists" )){
            return new ResponseEntity<>(new ResponseMessage("like already exists"), HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok(new ResponseMessage(message));

    }



    @ResponseBody
    @PostMapping("/scrap")
    public ResponseEntity<ResponseMessage> scrapPlan(@RequestBody PlanIdDTO planIdDTO, @RequestHeader(value = "userId") long userId) {

        String message = planService.scrapPlan(planIdDTO.getPlanId(),userId);
        if (Objects.equals(message, "no plan exists")){
            return new ResponseEntity<>(new ResponseMessage("no plan exists"), HttpStatus.NOT_FOUND);
        }else if(Objects.equals(message, "already scrap error")){
            return new ResponseEntity<>(new ResponseMessage("already scrap error"), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(new ResponseMessage(message));

    }

    @ResponseBody
    @PostMapping("/comment/add")
    public  ResponseEntity<CommentUpdateResponseDTO> addComment(@RequestBody CommentRequestDTO comment, @RequestHeader("userId") long userId) {
        comment.setUserId(userId);

        comment.setCommentId(0);


        CommentUpdateResponseDTO commentUpdateResponseDTO = planService.addComment(comment);

        if(Objects.equals(commentUpdateResponseDTO.getMessage(), "no plan exists"))
            return new ResponseEntity<>(commentUpdateResponseDTO,HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(commentUpdateResponseDTO);
    }

    @ResponseBody
    @DeleteMapping("")
    public ResponseEntity<ResponseMessage> deletePlan(@RequestParam("planId") long planId,@RequestHeader("userId") long userId) {
        PlanResponseDTO plan = planService.getPlan(planId, userId);
        System.out.println(plan.getPlan().getUserId());
        if(plan.getPlan().getUserId() != userId)
            return new ResponseEntity<>(new ResponseMessage("you have no access to this plan"),HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<ResponseMessage> deleteComment(@RequestParam("commentId") long commentId, @RequestHeader("userId") long userId) {
        Comment comment = planService.getComment(commentId);
        if(comment == null)
            return new ResponseEntity<>(new ResponseMessage("comment not found"),HttpStatus.NOT_FOUND);
        if(comment.getUser().getUserId() != userId)
            return new ResponseEntity<>(new ResponseMessage("you have no access to this comment"),HttpStatus.BAD_REQUEST);
        //유저아이디랑 댓글 아이디 일치여부 로직 추가
        String message = planService.deleteComment(commentId);
        return ResponseEntity.ok(new ResponseMessage(message));
    }
}
