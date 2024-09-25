package Trabook.PlanManager.domain.destination;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

@Getter @Setter
public class Place {
    private Long placeId;
    private Long cityId;
    private String address;
    private String placeName;
    private String description;
    private Double latitude;
    private Double Longitude;
    private Long star;
    private String category;
    private String imageSrc;
    private String subcategory;
    private String tel;
    private String zipcode;
    private Double ratingScore;
    private Integer scraps;
    private Integer numOfAdded;
    private Boolean isScraped;

    public Place(){};

    public Place(Long placeId, Long cityId, String address, String placeName, String description, Double latitude, Double longitude, Long star, String category, String imageSrc, String subcategory, String tel, String zipcode, Double ratingScore, Integer scraps, Integer numOfAdded, Integer likes) {
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
        this.numOfAdded = numOfAdded;
    }
}
