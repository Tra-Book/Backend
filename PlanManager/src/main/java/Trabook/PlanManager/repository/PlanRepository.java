package Trabook.PlanManager.repository;

import Trabook.PlanManager.domain.plan.Plan;

import java.util.List;

public interface PlanRepository {
    Plan save(Plan plan);
    boolean findByUserAndName(String userName, String planName);
    List<Plan> findPlanList();
    List<Plan> findUserPlanList();

}
