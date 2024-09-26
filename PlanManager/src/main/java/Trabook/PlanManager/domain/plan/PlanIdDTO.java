package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanIdDTO {

    private long planId;

    public PlanIdDTO() {}

    public PlanIdDTO(long planId) {

        this.planId = planId;
    }
}
