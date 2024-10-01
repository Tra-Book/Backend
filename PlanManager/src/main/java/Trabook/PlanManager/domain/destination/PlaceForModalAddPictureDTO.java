package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//@RequiredArgsConstructor
@Getter @Setter
public class PlaceForModalAddPictureDTO extends PlaceForModalDTO {

    private List<String> photos;

    public PlaceForModalAddPictureDTO(Place place, List<PlaceComment> comments, List<String> photos) {
        super(place, comments);
        this.photos = photos;
    }
}
