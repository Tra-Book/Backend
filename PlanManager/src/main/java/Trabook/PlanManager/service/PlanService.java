package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;
import Trabook.PlanManager.repository.plan.PlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PlanService {

    private final PlanRepository planRepository;

    @Autowired
    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }
    @Transactional
    public long createPlan(Plan newPlan, List<Schedule> scheduleList) {

        if(validateDuplicatePlanName(newPlan).isPresent()){
            //return "planName already exists";
            return -1;
        } else {
            Plan savedPlan;

                if (scheduleList == null || scheduleList.isEmpty()) {
                    savedPlan = planRepository.savePlan(newPlan);
                } else {
                    savedPlan = planRepository.savePlan(newPlan);
                    planRepository.saveSchedule(scheduleList);
                }


            return savedPlan.getPlanId();
        }
    }

    @Transactional
    public String addComment(Comment comment) {
        if(planRepository.findById(comment.getPlanId()).isPresent()){
            planRepository.addComment(comment);
            return "added comment";
        } else
            return "no plan exists";
    }
    public Optional<Plan> getPlan(long planId) {
        return planRepository.findById(planId);
    }
    public String deletePlan(long planId) {
        if(planRepository.deletePlan(planId) == 1)
            return "delete complete";
        else {
            return "error";
        }
    }

    @Transactional()
    public String deleteLike(long userId,long planId){
        /*
        if(planRepository.deleteLike(userId,planId) == 1) {
            planRepository.downLike(planId);
            return "delete complete";
        } else
            return "error";
            */
        int updatedLikes = planRepository.downLike(planId);
        log.info("planId : {} like 수 감소[{} -> {}]",planId,updatedLikes-1,updatedLikes);
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

    public String deleteComment(long commentId) {
        if(planRepository.deleteComment(commentId) == 1)
            return "delete complete";
        else
            return "error";
    }

    @Transactional
    public String likePlan(long userId,long planId) {

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
    public String scrapPlan(long userId, long planId) {
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
