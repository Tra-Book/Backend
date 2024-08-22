package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.DestinationReactionDto;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
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

    public List<Place> getPlaceListByUserScrap(long userId) {
        return destinationRepository.findPlaceListByUserScrap(userId);
    }
    @Transactional
    public String addPlaceReaction(DestinationReactionDto destinationReactionDto) {

        String reactionType = destinationReactionDto.getReactionType();
        long userId = destinationReactionDto.getUserId();
        long placeId = destinationReactionDto.getPlaceId();

        if(reactionType.equals("LIKE")) {
            if(destinationRepository.findByPlaceId(placeId).isPresent()) {
                try {
                    destinationRepository.addPlaceLike(userId, placeId);
                    return "like complete";
                } catch(DataAccessException e) {
                    if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                        return "already liked";
                    }
                    return "error accessing db";
                }
            }
            return "no place";
        }
        else if(reactionType.equals("SCRAP")) {
            if(destinationRepository.findByPlaceId(placeId).isPresent()){
                try {
                    destinationRepository.addPlaceScrap(userId, placeId);
                    return "scrap complete";
                } catch(DataAccessException e) {
                    if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                        return "already scrapped";
                    }
                    return "error accessing db";
                }
            }
            return "no place";
        }
        else
            return "type invalid";

    }
    @Transactional
    public String deletePlaceReaction(DestinationReactionDto destinationReactionDto) {
        String reactionType = destinationReactionDto.getReactionType();
        long userId = destinationReactionDto.getUserId();
        long placeId = destinationReactionDto.getPlaceId();

        if(reactionType.equals("LIKE")) {
            if(destinationRepository.deletePlaceLike(userId,placeId)==1) {
                destinationRepository.likeDown(placeId);
                return "delete like complete";
            }
            else
                return "can't delete null";
        }
        else if(reactionType.equals("SCRAP")) {
            if(destinationRepository.deletePlaceScrap(userId,placeId)==1) {
                destinationRepository.scrapDown(placeId);
                return "delete scrap complete";
            }
            else
                return "can't delete null";
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
