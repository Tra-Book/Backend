package Trabook.PlanManager.domain.comment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Getter @Setter
public class Comment {
    private long commentId;
    private long userId;
    private long planId;
    private String content;
    private long ref;
    private int refOrder;
    private LocalDateTime time;
}
