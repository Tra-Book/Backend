package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Plan {
    private long planId;
    private long userId;
    private long cityId;
    //private String scheduleId;
    private boolean isPublic;
    private int likes;
    private int scraps;
    //private type date;
    private String planTitle;


    public Plan() {

    }

    public Plan(long planId, long userId, long cityId, boolean isPublic, int likes, int scraps, String planTitle) {
        this.planId = planId;
        this.userId = userId;
        this.cityId = cityId;
        //this.scheduleId = scheduleId;
        this.isPublic = isPublic;
        this.likes = likes;
        this.scraps = scraps;
        this.planTitle = planTitle;
    }
}
