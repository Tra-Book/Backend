package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.PlanListResponseDTO;

import java.util.List;

public interface PlanListRepository {

    List<PlanListResponseDTO> findUserPlanList(long userId);
    List<PlanListResponseDTO> findUserScrappedPlanList(long userId);
}
