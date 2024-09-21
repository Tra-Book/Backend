package Trabook.PlanManager.domain.destination;

import lombok.Data;

import java.util.List;

@Data
public class CustomPlaceListDTO {
    private List<Place> places;
    private Integer totalPages;

    public CustomPlaceListDTO(List<Place> places, Integer totalPages) {
        this.places = places;
        this.totalPages = totalPages;
    }
}
