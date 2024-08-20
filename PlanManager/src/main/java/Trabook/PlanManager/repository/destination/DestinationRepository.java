package Trabook.PlanManager.repository.destination;


import Trabook.PlanManager.domain.destination.City;
import Trabook.PlanManager.domain.destination.Place;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository {
    Optional<Place> findByPlaceId(long placeId);

    List<Place> findPlaceListByCity(long cityId);

    void addPlaceLike(long userId, long placeId);
    void addPlaceScrap(long userId, long scrapId);

    void deletePlaceLike(long userId, long placeId);
    void deletePlaceScrap(long userId, long placeId);
}
