package Trabook.PlanManager.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlanIdResponseDTO {
    private long planId;
    private String message;
    public  PlanIdResponseDTO() {}
    public PlanIdResponseDTO(long planId, String message) {
        this.planId = planId;
        this.message = message;
    }

}
