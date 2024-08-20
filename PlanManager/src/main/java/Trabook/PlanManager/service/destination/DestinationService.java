package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.DestinationReactionDto;
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

    public List<Place> getPlaceListByCity(long cityId) {
        return destinationRepository.findPlaceListByCity(cityId);
    }

    public String addPlaceReaction(DestinationReactionDto destinationReactionDto){
        String reactionType = destinationReactionDto.getReactionType();
        long userId = destinationReactionDto.getUserId();
        long placeId = destinationReactionDto.getPlaceId();

        if(reactionType.equals("LIKE")) {
            destinationRepository.addPlaceLike(userId, placeId);
            return "like complete";
        }
        else if(reactionType.equals("SCRAP")) {
            destinationRepository.addPlaceScrap(userId, placeId);
            return "scrap complete";
        }
        else
            return "type invalid";

    }

    public String deletePlaceReaction(DestinationReactionDto destinationReactionDto) {
        String reactionType = destinationReactionDto.getReactionType();
        long userId = destinationReactionDto.getUserId();
        long placeId = destinationReactionDto.getPlaceId();

        if(reactionType.equals("LIKE")) {
            destinationRepository.deletePlaceLike(userId,placeId);
            return "delete like complete";
        }
        else if(reactionType.equals("SCRAP")) {
            destinationRepository.deletePlaceScrap(userId,placeId);
            return "delete scrap complete";
        }
        else
            return "type invalid";
    }

    public Optional<Place> getPlaceByPlaceId(long placeId){
        return destinationRepository.findByPlaceId(placeId);
    }
    public Optional<Place> getPlace(long placeId) {
        return destinationRepository.findByPlaceId(placeId);
    }
}
