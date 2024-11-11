package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.PlanGeneralDTO;
import Trabook.PlanManager.response.PlanListResponseDTO;

import java.util.List;

public interface PlanListRepository {

    List<PlanListResponseDTO> findUserPlanList(long userId);
    List<PlanListResponseDTO> findUserScrappedPlanList(long userId);
    List<PlanGeneralDTO> findCustomPlanList(String search,
                                            List<String> state,
                                            Integer numOfPeople,
                                            Integer duration,
                                            String sorts,
                                            Integer userId,
                                            Boolean userScrapOnly);
    List<PlanListResponseDTO> findHottestPlan();


}
