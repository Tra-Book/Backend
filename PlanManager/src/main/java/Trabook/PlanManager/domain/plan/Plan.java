package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Plan {
    private String planId;
    private String userId;
    private String cityId;
    private String scheduleId;
    private boolean isPublic;
    private int likes;
    private int scraps;
    //private type date;
    private String planName;


    public Plan() {

    }

    public Plan(String planId, String userId, String cityId, String scheduleId, boolean isPublic, int likes, int scraps, String name) {
        this.planId = planId;
        this.userId = userId;
        this.cityId = cityId;
        this.scheduleId = scheduleId;
        this.isPublic = isPublic;
        this.likes = likes;
        this.scraps = scraps;
        this.planName = name;
    }
}
