package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;

import java.util.List;
import java.util.Optional;


public interface PlanRepository {
    Plan save(Plan plan, List<Schedule>scheduleList);
    Optional<Plan> findById(long planId);



    Optional<Plan> findPlanByUserAndName(long userId, String planName);

    Optional<Plan> deletePlan(long planId);

    //List<Plan> findPlanList();
    List<Plan> findUserPlanList(long userId);
    List<Plan> findUserLikePlanList(long userId);
    List<Plan> findUserScrapPlanList(long userId);
    List<Plan> findPlanListByCityId(long cityId);
    void likePlan(long userId,long planId);
    void scrapPlan(long userId,long planId);

    void clearStore();
}
