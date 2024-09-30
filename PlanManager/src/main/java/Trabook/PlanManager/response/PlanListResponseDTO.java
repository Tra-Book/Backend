package Trabook.PlanManager.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PlanListResponseDTO {

    public PlanListResponseDTO() {}

    private long planId;
    private String planTitle;
    private String description;
    private String state;
    //private 이미지
    private int likes;
    private int scraps;
    private int numOfComment;
    private String imgSrc;

    //여행 날짜
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isFinished;
    private Boolean isLiked;
    private Boolean isScrapped;
    private boolean isPublic;

    private Integer numOfPeople;

    private List<String> tags;
    public PlanListResponseDTO(long planId, String planTitle, String description, String state, int likes, int scraps, LocalDate startDate, LocalDate endDate, boolean isFinished, boolean isLiked, boolean isScrapped, boolean isPublic,List<String> tags) {
        this.planId = planId;
        this.planTitle = planTitle;
        this.description = description;
        this.state = state;
        this.likes = likes;
        this.scraps = scraps;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isFinished = isFinished;
        this.isLiked = isLiked;
        this.isScrapped = isScrapped;
        this.isPublic = isPublic;
        this.tags = tags;
    }
}
