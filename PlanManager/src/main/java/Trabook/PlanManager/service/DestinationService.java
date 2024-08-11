package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;

    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public List<Place> getPlaceListByCity(String cityId) {
        return destinationRepository.findPlaceListByCity(cityId);
    }

    public Optional<Place> getPlace(String placeId) {
        return destinationRepository.findByPlaceId(placeId);
    }
}
