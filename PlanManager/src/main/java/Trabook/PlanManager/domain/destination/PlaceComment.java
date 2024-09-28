package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PlaceComment {
    long commentId;
    long placeId;
    String content;
    String date;

    public PlaceComment() {}

    public PlaceComment(long commentId, long placeId, String content, String date) {
        this.commentId = commentId;
        this.placeId = placeId;
        this.content = content;
        this.date = date;
    }
}
