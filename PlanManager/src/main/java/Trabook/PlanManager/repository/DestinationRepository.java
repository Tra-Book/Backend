package Trabook.PlanManager.repository;


import Trabook.PlanManager.domain.destination.City;
import Trabook.PlanManager.domain.destination.Place;

public interface DestinationRepository {
    Place findByPlaceName();
    City findByCityName();

}
