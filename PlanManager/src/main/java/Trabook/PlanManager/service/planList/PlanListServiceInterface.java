package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanListResponseDTO;
import Trabook.PlanManager.repository.plan.PlanRepository;

import java.util.List;

public interface PlanListServiceInterface {

    public List<PlanListResponseDTO> getPlanList(long userId);
    public boolean isAvailableService();

}
