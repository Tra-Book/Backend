package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HottestPlanAdditionalFieldInRedis {
    private long planId;
    private int likes;
    private int scraps;
    private int numOfComment;

    public HottestPlanAdditionalFieldInRedis(long planId,int likes, int scraps, int numOfComment) {
        this.planId = planId;
        this.likes = likes;
        this.scraps = scraps;
        this.numOfComment = numOfComment;
    }
}
