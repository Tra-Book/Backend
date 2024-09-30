package Trabook.PlanManager.domain.plan;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.response.PlanListResponseDTO;

import java.util.List;

public class CustomPlanListDTO {
    public List<PlanGeneralDTO> plans;
    public Integer totalPages;

    public CustomPlanListDTO(List<PlanGeneralDTO> plans, Integer totalPages) {
        this.plans = plans;
        this.totalPages = totalPages;
    }
}