package Trabook.PlanManager.response;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentResponseDTO {
    private Comment comment;
    private User user;

    public CommentResponseDTO(Comment comment) {}

    public CommentResponseDTO(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
    }
}
