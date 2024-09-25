package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.response.PlanResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final DestinationRepository destinationRepository;
    private  final PlanListRepository planListRepository;

    @Autowired
    public PlanService(PlanRepository planRepository, DestinationRepository destinationRepository, PlanListRepository planListRepository) {
        this.planRepository = planRepository;
        this.destinationRepository = destinationRepository;
        this.planListRepository = planListRepository;
    }

    @Transactional
    public long createPlan(PlanCreateDTO planCreateDTO) {
        //user존재 로직 추가
        return planRepository.createPlan(planCreateDTO);
    }

    @Transactional
    public long updatePlan(Plan plan) {

        plan.setImgSrc("https://storage.googleapis.com/trabook-20240822/planPhoto/" + Long.toString(plan.getPlanId()));

        List<DayPlan> dayPlanList = plan.getDayPlanList();
        long planId = planRepository.updatePlan(plan);

        if(dayPlanList != null) {
            for (DayPlan dayPlan : dayPlanList) {
                dayPlan.setPlanId(planId);
                updateOrSaveDayPlan(dayPlan);
                updateOrSaveSchedule(plan, dayPlan);
            }
        }
        return planId;
    }

    private void updateOrSaveSchedule(Plan plan, DayPlan dayPlan) {
        int day = dayPlan.getDay();
        long planId = plan.getPlanId();
        for (DayPlan.Schedule schedule : dayPlan.getScheduleList()) {
            int order = schedule.getOrder();
            schedule.setPlanId(planId);
            schedule.setDay(day);

            if(planRepository.findSchedule(planId,day,order).isPresent()) // 새로 생성한 스케쥴
                planRepository.updateSchedule(schedule);
            else // 기존에 있는 스케쥴 업데이트
                planRepository.saveSchedule( schedule);

            placeRatingScoreUp(plan, schedule);
        }
    }

    private void placeRatingScoreUp(Plan plan, DayPlan.Schedule schedule) {
        if (plan.isFinished()) // 점수 추가
            destinationRepository.scoreUp(schedule.getPlaceId());
        // 레디스에 있는 목록인지 확인 로직 추가
    }

    private void updateOrSaveDayPlan(DayPlan dayPlan) {
        long planId = dayPlan.getPlanId();
        int day = dayPlan.getDay();
        if(planRepository.findDayPlan(planId,day).isPresent()) { // 새로 생성한 dayplan
            planRepository.updateDayPlan(dayPlan);
        } else { // 기존에 있는 dayplan
            planRepository.saveDayPlan(dayPlan);
        }
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
                List<DayPlan.Schedule> scheduleList = planRepository.findScheduleList(dayPlan.getPlanId(),dayPlan.getDay());
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
            result = new PlanResponseDTO(planResult.get(), dayPlanList,null,isLiked,isScrapped,null);
            return result;
        }
        else
            return null;
    }

    public List<String> getTags(PlanResponseDTO planResponseDTO) {
        //tagCount 변수로 3개 되면 리턴할지 아니면 리스트의 사이즈를 확인할지 고민해보기
        List<String> tags = new ArrayList<>();
        List<DayPlan> dayPlanList = planResponseDTO.getDayPlanList();

        for(DayPlan dayPlan : dayPlanList) {
            List<DayPlan.Schedule> scheduleList = dayPlan.getScheduleList();
            for(DayPlan.Schedule schedule : scheduleList) {
                tags.add(destinationRepository.findTagByPlaceId(schedule.getPlaceId()));
                if(tags.size()==3)
                    return tags;
            }
        }
        return tags;
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

    @Transactional
    public List<PlanListResponseDTO> findCustomPlanList(String search,
                                                        List<String> region,
                                                        Integer memberCount,
                                                        Integer duration,
                                                        String sorts,
                                                        Integer userId) {
        return planListRepository.findCustomPlanList(search, region, memberCount, duration, sorts, userId);
    }

    // null인지 아닌지 확인해서 boolean으로 반환하는 방식으로 바꾸기
    public Optional<Plan> validateDuplicatePlanName(Plan plan){

        return planRepository.findPlanByUserAndName(plan.getUserId(),plan.getTitle());

    }
}
