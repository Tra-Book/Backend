package Trabook.PlanManager.response;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.DayPlan;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class PlanResponseDTO {
    private Plan plan;
    private User user;
    private boolean isLiked;
    private boolean isScrapped;
    private List<String> tags;
    private List<Comment> comments;
    public PlanResponseDTO() {}

    public PlanResponseDTO(Plan plan, List<DayPlan> dayPlanList, User user, boolean isLiked, boolean isScrapped, List<String> tags,List<Comment> comments) {
        this.plan = plan;
        //this.dayPlanList = dayPlanList;
        this.user = user;
        this.isLiked = isLiked;
        this.isScrapped = isScrapped;
        this.tags = tags;
        this.comments = comments;
    }
}
