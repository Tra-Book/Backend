package Trabook.PlanManager.domain.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class CommentRequestDTO {
    private long commentId;
    private long userId;
    private long planId;
    private String content;
    private long parentId;
    private int refOrder;

    private String time;

    public CommentRequestDTO() {}
    public CommentRequestDTO(long commentId, long planId, String content, long parentId, int refOrder, String time) {
        this.commentId = commentId;
        this.planId = planId;
        this.content = content;
        this.parentId = parentId;
        this.refOrder = refOrder;
        this.time = time;
    }
}
