package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.comment.CommentRequestDTO;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.domain.user.User;

import Trabook.PlanManager.domain.webclient.userInfoDTO;
import Trabook.PlanManager.response.*;

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
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public ResponseEntity<PlanCreateResponseDTO> createPlan(@RequestBody PlanCreateDTO planCreateDTO, @RequestHeader("userId") long userId) throws FileNotFoundException {
        //System.out.println(userId);
        planCreateDTO.setUserId(userId);
        Long planId = planService.createPlan(planCreateDTO);
        String fileName = fileUploadService.uploadDefaultImage(planId);
        PlanCreateResponseDTO planCreateResponseDTO = new PlanCreateResponseDTO(planId,"create complete","https://storage.googleapis.com/trabook-20240822/"+fileName);
        return new ResponseEntity<>(planCreateResponseDTO, HttpStatus.OK);

    }

    @ResponseBody
    @PatchMapping("/update")
    public ResponseEntity<PlanUpdateResponseDTO> updatePlan(@RequestPart("plan") Plan plan,
                                                            @RequestPart(value = "image",required = false) MultipartFile image) {

        long planId = planService.updatePlan(plan);
        if(planId == 0)
            return new ResponseEntity<>(new PlanUpdateResponseDTO(-1,"no plan exists"), HttpStatus.NOT_FOUND);
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

        PlanUpdateResponseDTO planUpdateResponseDTO = new PlanUpdateResponseDTO(planId,"update complete");

        return new ResponseEntity<>(planUpdateResponseDTO,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("")
    public PlanResponseDTO getPlanByPlanId(@RequestParam("planId")long planId, @RequestHeader(value = "userId", required = false) Long userId) {
        PlanResponseDTO result = planService.getPlan(planId,userId);

        long planOwnerId = result.getPlan().getUserId();
        User planOwner = webClientService.getUserInfoBlocking(planOwnerId);
        result.setUser(planOwner);

        List<Long> commentUserIds = new ArrayList<>();
        List<Comment> commentList = result.getComments();
        for(Comment comment : commentList ) {
            long commentUserId = comment.getUser().getUserId();
            commentUserIds.add(commentUserId);

        }
        List<User> users = webClientService.getUserInfoListBlocking(commentUserIds);
        for (int indexOfUserList = 0; indexOfUserList < users.size(); indexOfUserList++) {
            result.getComments().get(indexOfUserList).setUser(users.get(indexOfUserList));
        }

        return result;
    }

/*
    @ResponseBody
    @GetMapping("")
    public Mono<ResponseEntity<PlanResponseDTO>> getPlanByPlanId(@RequestParam("planId")long planId, @RequestHeader(value = "userId", required = false) Long userId) {

        // part 1 : get plan and plan owner Info
        PlanResponseDTO result = planService.getPlan(planId, userId);
        if(result == null){
            return Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        long planOwnerId = result.getPlan().getUserId();
        Mono<userInfoDTO> userInfo = webClientService.getUserInfo(planOwnerId);

        // part 2 : get comments and comments' user Info
        List<Long> commentUserIds = new ArrayList<>();
        List<Comment> commentList = result.getComments();
        for(Comment comment : commentList ) {
            long commentUserId = comment.getUser().getUserId();
            commentUserIds.add(commentUserId);

        }
        // 이 부분을 굳이 non-blocking으로 처리할 필요?
        Mono<List<User>> commentUserListInfo = commentUserIds.isEmpty() ? Mono.just(Collections.emptyList()) : webClientService.getUserListInfo(commentUserIds);

        return Mono.zip(userInfo,commentUserListInfo)
                .map(tuple -> {
                    result.setUser(tuple.getT1().getUser());
                    List<User> users= tuple.getT2();

                    for (int indexOfUserList = 0; indexOfUserList < users.size(); indexOfUserList++) {
                        result.getComments().get(indexOfUserList).setUser(users.get(indexOfUserList));
                    }

                   return new ResponseEntity<>(result, HttpStatus.OK);
                });

    }


 */
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
