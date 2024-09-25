package Trabook.PlanManager.response;

import Trabook.PlanManager.domain.plan.DayPlan;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class PlanResponseDTO {
    private Plan plan;
    //private List<DayPlan> dayPlanList;
    private User user;
    private boolean isLiked;
    private boolean isScrapped;
    private List<String> tags;
    public PlanResponseDTO() {}

    public PlanResponseDTO(Plan plan, List<DayPlan> dayPlanList, User user, boolean isLiked, boolean isScrapped, List<String> tags) {
        this.plan = plan;
        //this.dayPlanList = dayPlanList;
        this.user = user;
        this.isLiked = isLiked;
        this.isScrapped = isScrapped;
        this.tags = tags;
    }
}
