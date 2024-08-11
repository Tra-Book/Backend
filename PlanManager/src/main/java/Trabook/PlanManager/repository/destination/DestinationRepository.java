package Trabook.PlanManager.repository.destination;


import Trabook.PlanManager.domain.destination.City;
import Trabook.PlanManager.domain.destination.Place;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository {
    Optional<Place> findByPlaceId(String placeId);

    List<Place> findPlaceListByCity(String cityId);
}
