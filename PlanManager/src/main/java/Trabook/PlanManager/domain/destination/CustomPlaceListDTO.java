package Trabook.PlanManager.domain.destination;

import lombok.Data;

import java.util.List;

@Data
public class CustomPlaceListDTO {
    private List<PlaceForModalDTO> places;
    private Integer totalPages;

    public CustomPlaceListDTO(List<PlaceForModalDTO> places, Integer totalPages) {
        this.places = places;
        this.totalPages = totalPages;
    }
}
