package Trabook.PlanManager.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlanUpdateResponseDTO {
    private long planId;
    private String message;
    //private String imgSrc;
    public PlanUpdateResponseDTO() {}

    public PlanUpdateResponseDTO(long planId, String message) {
        this.planId = planId;
        this.message = message;
        //this.imgSrc = imgSrc;
    }
}
