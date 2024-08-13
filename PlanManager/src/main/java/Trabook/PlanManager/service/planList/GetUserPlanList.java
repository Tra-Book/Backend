package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.domain.plan.Plan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserPlanList implements PlanListServiceInterface{
    @Override
    public List<Plan> getPlanList(long userId) {
        return List.of();
    }

    @Override
    public boolean isAvailableService() {
        return false;
    }
}
