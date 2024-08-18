package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.repository.plan.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserScrapPlanList implements PlanListServiceInterface{

    private final PlanRepository planRepository;

    public GetUserScrapPlanList(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public List<Plan> getPlanList(long userId) {
        return planRepository.findUserScrapPlanList(userId);
    }

    @Override
    public boolean isAvailableService() {
        return false;
    }
}
