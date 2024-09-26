package Trabook.PlanManager.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentUpdateResponseDTO {
    private String message;
    private long commentId;

    public CommentUpdateResponseDTO() {}
    public CommentUpdateResponseDTO(String message, long commentId) {
        this.message = message;
        this.commentId = commentId;
    }
}
