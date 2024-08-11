package Trabook.PlanManager.domain.comment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter @Setter
public class Comment {
    private String commentId;
    private String userId;
    private String planId;
    private String content;
    private int group;
    private int orderInGroup;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date postDate;
}
