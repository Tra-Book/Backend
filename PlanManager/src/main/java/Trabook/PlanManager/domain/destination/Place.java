package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

@Getter @Setter
public class Place {
    long placeId;
    long cityId;
    int likes;
    int numOfAdded;
    double latitude;
    double longitude;
    long star;
    long ratingScore;
    String address;
    String placeName;
    //String description;
    String imageSrc;
    String category;

    public Place() {

    }

    public Place(long placeId, long cityId, int likes, int numOfAdded, double latitude, double longitude, long star, long ratingScore, String address, String placeName, String imageSrc, String category) {
        this.placeId = placeId;
        this.cityId = cityId;
        this.likes = likes;
        this.numOfAdded = numOfAdded;
        this.latitude = latitude;
        this.longitude = longitude;
        this.star = star;
        this.ratingScore = ratingScore;
        this.address = address;
        this.placeName = placeName;
        this.imageSrc = imageSrc;
        this.category = category;
    }
}
