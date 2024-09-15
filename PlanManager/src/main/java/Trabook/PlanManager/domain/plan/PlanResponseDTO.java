package Trabook.PlanManager.domain.plan;

import Trabook.PlanManager.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class PlanResponseDTO {
    private Plan plan;
    private List<DayPlan> dayPlanList;
    private User user;
    private boolean isLiked;
    private boolean isScrapped;

    public PlanResponseDTO() {}

    public PlanResponseDTO(Plan plan, List<DayPlan> dayPlanList, boolean isLiked, boolean isScrapped,User user) {
        this.plan = plan;
        this.dayPlanList = dayPlanList;
        this.isLiked = isLiked;
        this.isScrapped = isScrapped;
        this.user = user;
    }
}
