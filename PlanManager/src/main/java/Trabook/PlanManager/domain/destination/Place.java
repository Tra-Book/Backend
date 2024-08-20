package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

@Getter @Setter
public class Place {
    long placeId;
    long cityId;
    int likes;
    int scraps;
    //double xMap;
    Point geography;
    //double yMap;
    long rating;
    String address;
    String placeName;
    String description;
    String imageSrc;
    String category;

    public Place() {

    }


    public Place(long placeId, long cityId, int likes, int scraps, Point geography, long rating, String address, String placeName, String description, String imageSrc, String category) {
        this.placeId = placeId;
        this.cityId = cityId;
        this.likes = likes;
        this.scraps = scraps;
        this.geography = geography;
        this.rating = rating;
        this.address = address;
        this.placeName = placeName;
        this.description = description;
        this.imageSrc = imageSrc;
        this.category = category;
    }
}
