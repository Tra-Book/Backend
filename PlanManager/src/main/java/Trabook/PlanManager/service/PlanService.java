package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.PlanResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final DestinationRepository destinationRepository;

    @Autowired
    public PlanService(PlanRepository planRepository,DestinationRepository destinationRepository) {
        this.planRepository = planRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional
    public long createPlan(PlanCreateDTO planCreateDTO) {
        //user존재 로직 추가
        return planRepository.createPlan(planCreateDTO);
    }

    @Transactional
    public long updatePlan(Plan plan) {
/*
        if(validateDuplicatePlanName(newPlan).isPresent()){
            //return "planName already exists";
            return -2;
        } else {
        }
 */
        List<DayPlan> dayPlanList = plan.getDayPlanList();
        long savedPlanId = planRepository.updatePlan(plan);

        if(dayPlanList != null) {
            for (DayPlan dayPlan : dayPlanList) {
                long dayPlanId = updateOrSaveDayPlan(dayPlan);
                updateOrSaveSchedule(plan, dayPlan, dayPlanId);
            }
        }

        return savedPlanId;
    }

    private void updateOrSaveSchedule(Plan plan, DayPlan dayPlan, long dayPlanId) {
        for (DayPlan.Schedule schedule : dayPlan.getScheduleList()) {
            if(schedule.getScheduleId() == 0) // 새로 생성한 스케쥴
                planRepository.saveSchedule(dayPlanId, schedule);
            else // 기존에 있는 스케쥴 업데이트
                planRepository.updateSchedule(dayPlanId,schedule);

            placeRatingScoreUp(plan, schedule);
        }
    }

    private void placeRatingScoreUp(Plan plan, DayPlan.Schedule schedule) {
        if (plan.isFinished()) // 점수 추가
            destinationRepository.scoreUp(schedule.getPlaceId());
        // 레디스에 있는 목록인지 확인 로직 추가
    }

    private long updateOrSaveDayPlan(DayPlan dayPlan) {
        long dayPlanId;
        if(dayPlan.getDayPlanId() == 0) { // 새로 생성한 dayplan
            dayPlanId = planRepository.saveDayPlan(dayPlan);
        } else { // 기존에 있는 dayplan
            dayPlanId = planRepository.updateDayPlan(dayPlan);
        }
        return dayPlanId;
    }


    @Transactional
    public String addComment(Comment comment) {
        if(planRepository.findById(comment.getPlanId()).isPresent() ){
            if(comment.getRefOrder()!=0)
                if(planRepository.isCommentExists(comment.getRef()))
                    return "parent comment deleted";
            planRepository.addComment(comment);
            return "added comment";
        } else
            return "no plan exists";
    }

    @Transactional
    public PlanResponseDTO getPlan(long planId, long userId) {
        PlanResponseDTO result;

        Optional<Plan> planResult = planRepository.findById(planId);
        if(planResult.isPresent()) {
            List<DayPlan> dayPlanList = planRepository.findDayPlanListByPlanId(planId);

            for (DayPlan dayPlan : dayPlanList) {
                List<DayPlan.Schedule> scheduleList = planRepository.findScheduleListByDayPlanList(dayPlan.getDayPlanId());
                for(DayPlan.Schedule schedule : scheduleList) {
                    Place place = destinationRepository.findByPlaceId(schedule.getPlaceId()).get();
                    schedule.setLatitude(place.getLatitude());
                    schedule.setLongtitude(place.getLongitude());
                    schedule.setImageSrc(place.getImageSrc());
                    schedule.setPlaceName(place.getPlaceName());
                }
                dayPlan.setScheduleList(scheduleList);
            }
            boolean isLiked = planRepository.isLiked(planId, userId);
            boolean isScrapped = planRepository.isScrapped(planId, userId);

            System.out.println(isLiked);
            result = new PlanResponseDTO(planResult.get(), dayPlanList,isLiked,isScrapped,null);
            return result;
        }
        else
            return null;
    }

    @Transactional
    public String deletePlan(long planId) {
        if(planRepository.deletePlan(planId) == 1)
            return "delete complete";
        else {
            return "error";
        }
    }

    @Transactional
    public String deleteLike(long userId,long planId){

        int updatedLikes = planRepository.downLike(planId);
       // log.info("planId : {} like 수 감소[{} -> {}]",planId,updatedLikes-1,updatedLikes);
        return "delete complete";
    }

    @Transactional
    public String deleteScrap(long userId, long planId) {
        if(planRepository.deleteScrap(userId,planId) == 1) {
            int updatedScraps = planRepository.downScrap(planId);
            log.info("planId : {} scrap 수 감소[{} -> {}]",planId,updatedScraps-1,updatedScraps);
            return "delete complete";
        }else
            return "error";
    }

    @Transactional
    public String deleteComment(long commentId) {

        Comment comment = planRepository.findCommentById(commentId).get();
        if(comment == null)
            return "comment already deleted";
        if(comment.getRefOrder()==0)//comment ref oreder가 0인지 확인 로직
        {
            planRepository.deleteCommentByRef(comment.getRef());
            return "delete complete";
        } else {
            if(planRepository.deleteComment(commentId)==1)
                return "delete complete";
            else
                return "error comment delete";
        }

    }

    @Transactional
    public String likePlan(PlanReactionDTO planReactionDTO) {

        long userId = planReactionDTO.getUserId();
        long planId = planReactionDTO.getPlanId();

        try {
            if (planRepository.findById(planId).isPresent()) {
                planRepository.likePlan(userId, planId);
                planRepository.upLike(planId);
                return "like complete";
            } else
                return "no plan exists";
        } catch(DataAccessException e){
            if( e.getCause() instanceof SQLIntegrityConstraintViolationException)
                return "like already exists";
            return "error accessing db";
        }

        /*
        if (planRepository.findById(planId).isPresent()) {
            planRepository.upLike(planId);
            return "like complete";
        } else
            return "no plan exists";

         */
    }

    @Transactional
    public String scrapPlan(PlanReactionDTO planReactionDTO) {
        long userId = planReactionDTO.getUserId();
        long planId = planReactionDTO.getPlanId();
        try {
            if (planRepository.findById(planId).isPresent()) {
                planRepository.scrapPlan(userId, planId);
                planRepository.upScrap(planId);
                return "scrap complete";
            } else
                return "no plan exists";
        } catch(DataAccessException e) {
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                return "already scrap error";
            }
            return "error accessing db";
        }
    }


    // null인지 아닌지 확인해서 boolean으로 반환하는 방식으로 바꾸기
    public Optional<Plan> validateDuplicatePlanName(Plan plan){

        return planRepository.findPlanByUserAndName(plan.getUserId(),plan.getTitle());

    }


}
