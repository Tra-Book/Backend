package Trabook.PlanManager.domain.comment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter @Setter
public class Comment {
    private long commentId;
    private long userId;
    private long planId;
    private String content;
    private int ref;
    private int refOrder;
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private String time;
}
