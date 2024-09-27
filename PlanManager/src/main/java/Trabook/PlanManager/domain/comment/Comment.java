package Trabook.PlanManager.domain.comment;

import Trabook.PlanManager.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class Comment {
    private User user;
    private long commentId;

    private long planId;
    private String content;
    private long parentId;
    private int refOrder;
    private String time;
    public Comment() {}
    public Comment(User user, long commentId, long planId, String content, long parentId, int refOrder, String time) {
        this.user = user;
        this.commentId = commentId;
        this.planId = planId;
        this.content = content;
        this.parentId = parentId;
        this.refOrder = refOrder;
        this.time = time;
    }
}
