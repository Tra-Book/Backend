package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanSearchDTO;
import Trabook.PlanManager.domain.plan.Schedule;

import java.util.List;
import java.util.Optional;


public interface PlanRepository {
    Plan save(Plan plan, List<Schedule>scheduleList);
    Plan savePlan(Plan plan);
    void saveSchedule(List<Schedule> scheduleList);
    Optional<Plan> findById(long planId);

    Optional<Plan> findPlanByUserAndName(long userId, String planName);



    List<Plan> findUserPlanList(long userId);
    List<Plan> findUserLikePlanList(long userId);
    List<Plan> findUserScrapPlanList(long userId);
    List<Plan> findPlanListByCityId(long cityId);
    //List<Plan> planSearch(String keyword, PlanSearchDTO.Filters filter, String sort);

    void likePlan(long userId, long planId);
    void scrapPlan(long userId,long planId);
    void addComment(Comment comment);

    int deletePlan(long planId);
    int deleteLike(long userId, long planId);
    int deleteScrap(long userId, long planId);
    int deleteComment(long commentId);

    int upLike(long planId);
    int downLike(long planId);
    int upScrap(long planId);
    int downScrap(long planId);
    void clearStore();
}
