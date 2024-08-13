package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.domain.plan.Plan;

import java.util.List;

public interface PlanListServiceInterface {
    public List<Plan> getPlanList(long userId);
    public boolean isAvailableService();

}
