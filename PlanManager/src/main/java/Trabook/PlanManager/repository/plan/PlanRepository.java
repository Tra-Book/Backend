package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.*;

import java.util.List;
import java.util.Optional;


public interface PlanRepository {
    //Plan save(Plan plan, List<DayPlan> dayPlanList);
    //Plan savePlan(Plan plan);

    long createPlan(PlanCreateDTO planCreateDTO);

    long updatePlan(Plan plan);
    void saveDayPlan(DayPlan dayPlan);
    void saveSchedule(long DayPlanId, DayPlan.Schedule schedule);

    Optional<Plan> findById(long planId);
    Optional<Plan> findPlanByUserAndName(long userId, String planName);

    Optional<Comment> findCommentById(long commentId);
    //List<Plan> findUserPlanList(long userId);
    List<PlanListResponseDTO> findUserLikePlanList(long userId);
    //List<Plan> findUserScrapPlanList(long userId);
    //List<Plan> findPlanListByCityId(long cityId);
    List<DayPlan> findDayPlanListByPlanId(long planId);
    List<DayPlan.Schedule> findScheduleListByDayPlanList(long dayPlanId);
    //List<Plan> planSearch(String keyword, PlanSearchDTO.Filters filter, String sort);

    void likePlan(long userId, long planId);
    void scrapPlan(long userId,long planId);
    void addComment(Comment comment);

    int deletePlan(long planId);
    int deleteCommentByRef(long ref);
    int deleteLike(long userId, long planId);
    int deleteScrap(long userId, long planId);
    int deleteComment(long commentId);

    int upLike(long planId);
    int downLike(long planId);
    int upScrap(long planId);
    int downScrap(long planId);

    boolean isLiked(long planId,long userId);
    boolean isScrapped(long planId, long userId);
    boolean isCommentExists(long commentId);
    void clearStore();
}
