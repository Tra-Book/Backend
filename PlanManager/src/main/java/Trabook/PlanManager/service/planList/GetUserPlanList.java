package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserPlanList implements PlanListServiceInterface{
    private final PlanListRepository planListRepository;

    public GetUserPlanList(PlanListRepository planListRepository) {
        this.planListRepository = planListRepository;
    }

    @Override
    public List<PlanListResponseDTO> getPlanList(long userId) {
        return planListRepository.findUserPlanList(userId);
    }

    @Override
    public boolean isAvailableService() {
        return false;
    }
}
