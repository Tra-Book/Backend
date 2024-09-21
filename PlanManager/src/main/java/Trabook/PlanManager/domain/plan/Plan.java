package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class Plan {
    private long planId;
    private long userId;
    private String state; //도 단위 &
    private boolean isPublic;
    private boolean isFinished;
    private int likes;
    private int scraps;
    private int numOfPeople; //바꾸기
    private int budget;
    //private MultipartFile image;
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String description;
    private String img;
    private List<DayPlan> dayPlanList;
    public Plan() {

    }
    //시작 날짜 종료 날짜

    public Plan(long planId, long userId, String state, boolean isPublic, boolean isFinished, int likes, int scraps, int numOfPeople, int budget, LocalDate startDate, LocalDate endDate, String title, String description, String img, List<DayPlan> dayPlanList) {
        this.planId = planId;
        this.userId = userId;
        this.state = state;
        this.isPublic = isPublic;
        this.isFinished = isFinished;
        this.likes = likes;
        this.scraps = scraps;
        this.numOfPeople = numOfPeople;
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = description;
        this.img = img;
        this.dayPlanList = dayPlanList;
    }
}
