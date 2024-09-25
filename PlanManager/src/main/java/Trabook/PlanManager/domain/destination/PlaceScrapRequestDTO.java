package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlaceScrapRequestDTO {
    private long placeId;

    public PlaceScrapRequestDTO() {}
    public PlaceScrapRequestDTO(long placeId) {
        this.placeId = placeId;
    }
}
