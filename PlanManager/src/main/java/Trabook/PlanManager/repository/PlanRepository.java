package Trabook.PlanManager.repository;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;

import java.util.List;


public interface PlanRepository {
    Plan save(Plan plan, List<Schedule>scheduleList);
    Plan findById(String planId);

    boolean findByUserAndName(String userName, String planName);
    boolean deletePlan(Plan plan);

    List<Plan> findPlanList();
    List<Plan> findUserPlanList(String userId);
    List<Plan> findUserLikePlanList(String userId);
    List<Plan> findUserScrapPlanList(String userId);
    int likePlan(String userId,String planId);
    int scrapPlan(String userId,String planId);

    void clearStore();
}
