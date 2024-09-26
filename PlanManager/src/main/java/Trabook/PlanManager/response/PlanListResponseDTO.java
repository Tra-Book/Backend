package Trabook.PlanManager.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlanListResponseDTO {

    public PlanListResponseDTO() {}

    private long planId;
    private String planTitle;
    private String description;
    private String placeRegion;
    //private 이미지
    private int likes;
    private int scraps;
    private int numOfComments;
    private String imgSrc;

    //여행 날짜
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isFinished;
    private Boolean isLiked;
    private Boolean isScrapped;
    private boolean isPublic;


    public PlanListResponseDTO(long planId, String planTitle, String description, String placeRegion, int likes, int scraps, LocalDate startDate, LocalDate endDate, boolean isFinished, boolean isLiked, boolean isScrapped, boolean isPublic) {
        this.planId = planId;
        this.planTitle = planTitle;
        this.description = description;
        this.placeRegion = placeRegion;
        this.likes = likes;
        this.scraps = scraps;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isFinished = isFinished;
        this.isLiked = isLiked;
        this.isScrapped = isScrapped;
        this.isPublic = isPublic;
    }
}
