package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

@Getter @Setter
public class Place {
    Long placeId;
    Long cityId;
    String address;
    String placeName;
    String description;
    Double latitude;
    Double Longitude;
    Long star;
    String category;
    String imageSrc;
    String subcategory;
    String tel;
    String zipcode;
    Double ratingScore;
    Integer scraps;
    Integer likes;
    Integer numOfAdded;
    Integer numOfReview;

    public Place(){};

    public Place(Long placeId, Long cityId, String address, String placeName, String description, Double latitude, Double longitude, Long star, String category, String imageSrc, String subcategory, String tel, String zipcode, Double ratingScore, Integer scraps, Integer likes) {
        this.placeId = placeId;
        this.cityId = cityId;
        this.address = address;
        this.placeName = placeName;
        this.description = description;
        this.latitude = latitude;
        Longitude = longitude;
        this.star = star;
        this.category = category;
        this.imageSrc = imageSrc;
        this.subcategory = subcategory;
        this.tel = tel;
        this.zipcode = zipcode;
        this.ratingScore = ratingScore;
        this.scraps = scraps;

        this.likes = likes;
    }
}
