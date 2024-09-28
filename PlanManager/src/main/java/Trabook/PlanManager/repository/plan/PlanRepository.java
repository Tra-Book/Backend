package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.comment.CommentRequestDTO;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.response.PlanListResponseDTO;

import java.util.List;
import java.util.Optional;


public interface PlanRepository {

    long createPlan(PlanCreateDTO planCreateDTO);

    long updatePlan(Plan plan);
    long saveDayPlan(DayPlan dayPlan);
    long updateDayPlan(DayPlan dayPlan);
    void saveSchedule( DayPlan.Schedule schedule);
    long updateSchedule(DayPlan.Schedule schedule);
    Optional<Plan> findById(long planId);
    Optional<Plan> findPlanByUserAndName(long userId, String planName);
    Optional <DayPlan> findDayPlan(long planId,int day);
    Optional<Comment> findCommentById(long commentId);
    Optional <DayPlan.Schedule> findSchedule(long planId,int day,int order);

    List<PlanListResponseDTO> findUserLikePlanList(long userId);
    List<DayPlan> findDayPlanListByPlanId(long planId);
    List<DayPlan.Schedule> findScheduleList(long planId,int day);
    List<Comment> findCommentListByPlanId(long planId);
    void likePlan(long userId, long planId);
    void scrapPlan(long userId,long planId);
    long addComment(CommentRequestDTO comment);

    int deletePlan(long planId);
    int deleteCommentByRef(long ref,long commentId,long planId);
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
