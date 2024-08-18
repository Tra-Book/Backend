package Trabook.PlanManager.domain.plan;

import lombok.Getter;


@Getter
public class PlanListRequestDTO {
    private long userId;
    private String type;

    public PlanListRequestDTO() {}

}
