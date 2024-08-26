package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DestinationReactionDto {
    public DestinationReactionDto() {}

    private String reactionType;
    private long userId;
    private long placeId;

    public DestinationReactionDto(String reactionType, long userId, long placeId) {
        this.reactionType = reactionType;
        this.userId = userId;
        this.placeId = placeId;
    }
}
