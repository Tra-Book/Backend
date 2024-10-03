package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter @Setter
public class PlanCreateResponseDTO {
    private long planId;
    private String message;
    private String imgSrc;

    public PlanCreateResponseDTO(Long planId, String meesage, String fileName) {
        this.planId = planId;
        this.message = meesage;
        this.imgSrc = fileName;
    }
}
