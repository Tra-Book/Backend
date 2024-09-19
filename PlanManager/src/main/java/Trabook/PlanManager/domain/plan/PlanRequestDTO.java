package Trabook.PlanManager.domain.plan;

import lombok.Getter;

import java.util.List;

@Getter
public class PlanRequestDTO {
    private Plan plan;
    private List<DayPlan> dayPlanList;

    public PlanRequestDTO() {}
}
