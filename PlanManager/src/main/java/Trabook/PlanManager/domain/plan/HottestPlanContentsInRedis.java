package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class HottestPlanContentsInRedis {
    private long planId;
    private String planTitle;
    private String description;
    private String state;
    private String imgSrc;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isFinished;
    private boolean isPublic;
    private Integer numOfPeople;


    //프론트랑 상의해서 없애야 함
    private Boolean isLiked;
    private Boolean isScrapped;

    public HottestPlanContentsInRedis(long planId, String planTitle, String description, String state, String imgSrc, LocalDate startDate, LocalDate endDate, boolean isFinished, boolean isPublic, Integer numOfPeople, Boolean isLiked, Boolean isScrapped) {
        this.planId = planId;
        this.planTitle = planTitle;
        this.description = description;
        this.state = state;
        this.imgSrc = imgSrc;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isFinished = isFinished;
        this.isPublic = isPublic;
        this.numOfPeople = numOfPeople;

        this.isLiked = isLiked;
        this.isScrapped = isScrapped;
    }
}
