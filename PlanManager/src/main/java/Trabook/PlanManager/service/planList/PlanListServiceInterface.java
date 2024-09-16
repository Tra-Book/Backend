package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.response.PlanListResponseDTO;

import java.util.List;

public interface PlanListServiceInterface {

    public List<PlanListResponseDTO> getPlanList(long userId);
    public boolean isAvailableService();

}
