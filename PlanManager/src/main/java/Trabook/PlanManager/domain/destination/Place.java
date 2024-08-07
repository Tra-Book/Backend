package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Place {
    String placeId;
    String address;
    String placeName;
    String description;
    int likes;
    int scraps;
}
