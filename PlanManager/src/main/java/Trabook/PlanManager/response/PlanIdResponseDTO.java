package Trabook.PlanManager.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlanIdResponseDTO {
    private long planId;
    public  PlanIdResponseDTO() {}
    public PlanIdResponseDTO(long planId) {
        this.planId = planId;
    }
}
