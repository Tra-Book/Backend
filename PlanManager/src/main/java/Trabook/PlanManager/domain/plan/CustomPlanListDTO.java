package Trabook.PlanManager.domain.plan;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.response.PlanListResponseDTO;

import java.util.List;

public class CustomPlanListDTO {
    public List<PlanListResponseDTO> plans;
    public Integer totalPages;

    public CustomPlanListDTO(List<PlanListResponseDTO> plans, Integer totalPages) {
        this.plans = plans;
        this.totalPages = totalPages;
    }
}