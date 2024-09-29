package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.DestinationReactionDto;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
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
    public String addPlaceScrap(long userId, long placeId) {
        if(destinationRepository.findByPlaceId(placeId).isPresent()) {
            try {
                destinationRepository.addPlaceScrap(userId, placeId);
                return "scrap complete";
            }catch (DataAccessException e) {
                if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                    return "already scrap error";
                }
                return "error accessing db";
            }
        } else
            return "no place exists";
    }

    @Transactional
    public String deletePlaceScrap(long userId, long placeId) {
        if(destinationRepository.deletePlaceScrap(userId,placeId)==1) {
            destinationRepository.scrapDown(placeId);
            return "delete scrap complete";
        }
        else
            return "can't delete null";
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

    @Transactional
    public List<PlaceForModalDTO> getUserCustomPlaceList(String search,
                                              List<String> state,
                                              List<String> category,
                                              String sorts,
                                              Integer userId,
                                              Boolean userScrapOnly) {
        return destinationRepository.findCustomPlaceList(search, state, category, sorts, userId, userScrapOnly);
    }

    @Transactional
    public boolean isScrapPlace(long placeId,long userId) {
        //System.out.println("placeId = " + placeId + ", userId = " + userId);
        return destinationRepository.isScrapped(placeId,userId);
    }
    public List<PlaceForModalDTO> getHottestPlace(Long userId){
        List<Place> top10Places = destinationRepository.findHottestPlaceList();
        List<PlaceForModalDTO> result = new ArrayList<>();

        for(Place place : top10Places){
            if(userId == null) {
                place.setIsScrapped(false);
            }else {
                place.setIsScrapped(destinationRepository.isScrapped(place.getPlaceId(), userId));
            }

            List<PlaceComment> comments = destinationRepository.findCommentsByPlaceId(place.getPlaceId());

            result.add(new PlaceForModalDTO(place, comments));
        }
        return result;
    }
    /*
    public Optional<Place> getPlaceByPlaceId(long placeId){
        return destinationRepository.findByPlaceId(placeId);
    }


     */

    public Optional<Place> getPlaceByPlaceId(long placeId) {
        return destinationRepository.findByPlaceId(placeId);
    }
    public PlaceForModalDTO getPlaceModalByPlaceId(long placeId) {
        Optional<Place> place = destinationRepository.findByPlaceId(placeId);
        List<PlaceComment> comments = destinationRepository.findCommentsByPlaceId(placeId);
        return new PlaceForModalDTO(place.get(),comments);
    }
}
