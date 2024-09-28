package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Place {
    Long placeId;
    Long cityId;
    String address;
    String placeName;
    String description;
    Double latitude;
    Double longitude;
    Long star;
    String category;
    String imgSrc;
    String subcategory;
    Double ratingScore;
    Integer scraps;
    Integer numOfAdded;
    Integer numOfReview;
    Boolean isScrapped;
    public Place(){};

    public Place(Long placeId, Long cityId, String address, String placeName, String description, Double latitude, Double longitude, Long star, String category, String imgSrc, String subcategory, Double ratingScore, Integer scraps, Integer numOfAdded, Integer numOfReview, Boolean isScrapped) {
        this.placeId = placeId;
        this.cityId = cityId;
        this.address = address;
        this.placeName = placeName;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.star = star;
        this.category = category;
        this.imgSrc = imgSrc;
        this.subcategory = subcategory;

        this.ratingScore = ratingScore;
        this.scraps = scraps;
        this.numOfAdded = numOfAdded;
        this.numOfReview = numOfReview;
        this.isScrapped = isScrapped;
    }
}
