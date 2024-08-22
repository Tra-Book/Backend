package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH-mm:ss")
    private String dateCreated;
    private String title;
    private String content;


    public Plan() {

    }



    public Plan(long planId, long userId, long cityId, boolean isPublic, int likes, int scraps, String dateCreated, String title, String content) {
        this.planId = planId;
        this.userId = userId;
        this.cityId = cityId;
        this.isPublic = isPublic;
        this.likes = likes;
        this.scraps = scraps;
        this.dateCreated = dateCreated;
        this.title = title;
        this.content = content;

    }

    @Override
    public String toString() {
        return "Plan{" +
                "planId=" + planId +
                ", userId=" + userId +
                ", cityId=" + cityId +
                ", isPublic=" + isPublic +
                ", likes=" + likes +
                ", scraps=" + scraps +
                ", dateCreated=" + dateCreated +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
