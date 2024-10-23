package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.comment.CommentRequestDTO;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.CommentUpdateResponseDTO;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.response.PlanResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

@Slf4j
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final DestinationRepository destinationRepository;
    private final PlanListRepository planListRepository;

    @Autowired
    public PlanService(PlanRepository planRepository, DestinationRepository destinationRepository, PlanListRepository planListRepository) {
        this.planRepository = planRepository;
        this.destinationRepository = destinationRepository;
        this.planListRepository = planListRepository;
    }
    @Transactional
    public PlanResponseDTO testPlan(long planId) {
        PlanResponseDTO totalPlan = planRepository.findTotalPlan(planId);

        return totalPlan;

    }

    @Transactional
    public long createPlan(PlanCreateDTO planCreateDTO) {
        //user존재 로직 추가

        return planRepository.createPlan(planCreateDTO);
    }

    @Transactional
    public long updatePlan(Plan plan) {
        if(planRepository.findById(plan.getPlanId()).isEmpty()) {
            return 0;
        }
        plan.setImgSrc("https://storage.googleapis.com/trabook-20240822/planPhoto/" + Long.toString(plan.getPlanId()));

        List<DayPlan> dayPlanList = plan.getDayPlanList();
        long planId = planRepository.updatePlan(plan);

        List<DayPlan> oldDayPlanList = planRepository.findDayPlanListByPlanId(planId);
        int updatePlanDays = plan.getDayPlanList().size();
        int oldPlanDays = oldDayPlanList.size();

        if (updatePlanDays < oldPlanDays) {
            for(int i = updatePlanDays; i < oldPlanDays; i++) {
                long planIdOfOldPlan = oldDayPlanList.get(i).getPlanId();
                long dayOfOldPlan = oldDayPlanList.get(i).getDay();
                planRepository.deleteDayPlanById(planIdOfOldPlan, dayOfOldPlan);
                planRepository.deleteScheduleById(planIdOfOldPlan, dayOfOldPlan);
            }
        }


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
            long placeId = schedule.getPlaceId();

            schedule.setPlanId(planId);
            schedule.setDay(day);

            destinationRepository.updateNumOfAdded(placeId);

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
    public CommentUpdateResponseDTO addComment(CommentRequestDTO comment) {
        if(planRepository.findById(comment.getPlanId()).isPresent() ){
            //if(comment.getRefOrder()!=0)//대댓글
             //   if(planRepository.isCommentExists(comment.getParentId()))//본댓글 존재

               //     return new CommentUpdateResponseDTO("parent comment deleted",-1);
            long commentId = planRepository.addComment(comment);
            return new CommentUpdateResponseDTO("added comment",commentId);
        } else
            return new CommentUpdateResponseDTO("no plan exists", -1);

    }
    @Transactional
    public Comment getComment(long commentId) {
        return planRepository.findCommentById(commentId).get();
    }

    @Transactional
    public PlanResponseDTO getPlan(long planId, Long userId) {

        PlanResponseDTO result = planRepository.findTotalPlan(planId);
        Plan plan = result.getPlan();
        /*
        Optional<Plan> plan = planRepository.findById(planId);
        if(plan.isPresent()) {

            List<DayPlan> dayPlanList = planRepository.findDayPlanListByPlanId(planId);

            for (DayPlan dayPlan : dayPlanList) {
                List<DayPlan.Schedule> scheduleList = planRepository.findScheduleList(dayPlan.getPlanId(),dayPlan.getDay());

                for(DayPlan.Schedule schedule : scheduleList) {
                    Place place = destinationRepository.findByPlaceId(schedule.getPlaceId()).get();
                    schedule.setLatitude(place.getLatitude());
                    schedule.setLongitude(place.getLongitude());
                    schedule.setImageSrc(place.getImgSrc());
                    schedule.setPlaceName(place.getPlaceName());
                }
                dayPlan.setScheduleList(scheduleList);
            }

         */
        List<DayPlan> dayPlanList = plan.getDayPlanList();


        for(DayPlan dayPlan : dayPlanList) {
            for(DayPlan.Schedule schedule : dayPlan.getScheduleList()) {
                Place place = destinationRepository.findByPlaceId(schedule.getPlaceId()).get();
                schedule.setImageSrc(place.getImgSrc());
                schedule.setLongitude(place.getLongitude());
                schedule.setLatitude(place.getLatitude());
                schedule.setPlaceName(place.getPlaceName());
                schedule.setSubcategory(place.getSubcategory());
                schedule.setAddress(place.getAddress());
            }
        }


        boolean isLiked;
        boolean isScrapped;
        if(userId == null){
            isLiked = false;
            isScrapped = false;
        } else {
            isLiked = planRepository.isLiked(planId, userId);
            isScrapped = planRepository.isScrapped(planId, userId);
        }
        result.setLiked(isLiked);
        result.setScrapped(isScrapped);

            List<Comment> comments = planRepository.findCommentListByPlanId(planId);
            result.setComments(comments);
           // result = new PlanResponseDTO(plan.get(), dayPlanList,null,isLiked,isScrapped,null,comments);
            return result;


    }

    public List<String> getTags(List<DayPlan> dayPlanList) {
        //tagCount 변수로 3개 되면 리턴할지 아니면 리스트의 사이즈를 확인할지 고민해보기
       // List<String> tags = new ArrayList<>();
        Set<String> tags = new HashSet<>();
       // List<DayPlan> dayPlanList = plan.getDayPlanList();

        for(DayPlan dayPlan : dayPlanList) {
            List<DayPlan.Schedule> scheduleList = dayPlan.getScheduleList();
            for(DayPlan.Schedule schedule : scheduleList) {
                tags.add(destinationRepository.findTagByPlaceId(schedule.getPlaceId()));
                if(tags.size()==3)
                    return new ArrayList<>(tags);
            }
        }
        return new ArrayList<>(tags);
    }
    public List<String> getTagsTest(long planId) {
        return planRepository.findTagsByPlanId(planId);
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
        planRepository.deleteLike(userId,planId);
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
            planRepository.deleteCommentByRef(comment.getParentId(),commentId, comment.getPlanId());
            return "delete complete";
        } else {
            if(planRepository.deleteComment(commentId)==1)
                return "delete complete";
            else
                return "error comment delete";
        }

    }

    @Transactional
    public String likePlan(long planId, long userId) {


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

    }

    @Transactional
    public String scrapPlan(long planId, long userId) {

        try {
            if (planRepository.findById(planId).isPresent()) {
                if(planRepository.isScrapped(planId, userId)) {
                    return "already scrapped";
                }
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
    public List<PlanListResponseDTO> getHottestPlan(Long userId) {

        List<PlanListResponseDTO> top10Plans = planListRepository.findHottestPlan();
        for(PlanListResponseDTO plan : top10Plans){
            if(userId == null){
                plan.setIsScrapped(false);
                plan.setIsLiked(false);
            } else {
                plan.setIsLiked(planRepository.isLiked(plan.getPlanId(), userId));
                plan.setIsScrapped(planRepository.isScrapped(plan.getPlanId(), userId));
            }
        }
        return top10Plans;
    }

    @Transactional
    public List<PlanGeneralDTO> findCustomPlanList(String search,
                                                        List<String> state,
                                                        Integer numOfPeople,
                                                        Integer duration,
                                                        String sorts,
                                                        Integer userId,
                                                        Boolean userScrapOnly) {
        return planListRepository.findCustomPlanList(search, state, numOfPeople, duration, sorts, userId, userScrapOnly);
    }

    // null인지 아닌지 확인해서 boolean으로 반환하는 방식으로 바꾸기
    public Optional<Plan> validateDuplicatePlanName(Plan plan){

        return planRepository.findPlanByUserAndName(plan.getUserId(),plan.getTitle());

    }
}
