package Trabook.PlanManager.domain.plan;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Schedule {
    int scheduleId;
    //String date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
