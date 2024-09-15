package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanReactionDTO {
    private long userId;
    private long planId;

    public PlanReactionDTO() {}

    public PlanReactionDTO(long userId, long planId) {
        this.userId = userId;
        this.planId = planId;
    }
}
