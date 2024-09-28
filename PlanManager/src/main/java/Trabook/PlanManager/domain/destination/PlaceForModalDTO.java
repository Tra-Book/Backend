package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
public class PlaceForModalDTO {
    private Place place;
    private List<PlaceComment> comments;
    public PlaceForModalDTO() {}

    public PlaceForModalDTO(Place place, List<PlaceComment> comments) {
        this.place = place;
        this.comments = comments;
    }
}
