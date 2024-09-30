package Trabook.PlanManager.domain.plan;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import Trabook.PlanManager.response.PlanListResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PlanGeneralDTO {
    private PlanListResponseDTO plan;
    private List<PlanComment> comments;
    public PlanGeneralDTO() {}

    public PlanGeneralDTO(PlanListResponseDTO plan, List<PlanComment> comments) {
        this.plan = plan;
        this.comments = comments;
    }
}
