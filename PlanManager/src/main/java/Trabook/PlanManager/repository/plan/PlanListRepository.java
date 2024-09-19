package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.response.PlanListResponseDTO;

import java.util.List;

public interface PlanListRepository {

    List<PlanListResponseDTO> findUserPlanList(long userId);
    List<PlanListResponseDTO> findUserScrappedPlanList(long userId);
}
