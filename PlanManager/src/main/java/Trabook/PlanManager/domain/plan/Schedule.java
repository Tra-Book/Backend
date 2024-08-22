package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Getter @Setter
public class Schedule {
    long scheduleId;
    long planId;
    long placeId;
    String date;
    String startTime;
    String endTime;

    public Schedule() {}

    public Schedule(long scheduleId, long planId, long placeId, String date, String startTime, String endTime) {
        this.scheduleId = scheduleId;
        this.planId = planId;
        this.placeId = placeId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
