package Trabook.PlanManager.repository.destination;


import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository {
    Optional<Place> findByPlaceId(long placeId);

    List<Place> findPlaceListByCity(long cityId);
    List<Place> findPlaceListByUserScrap(long userId);
    List<Place> findHottestPlaceList();
    List<PlaceForModalDTO> findCustomPlaceList(String search,
                                    List<String> state,
                                    List<String> category,
                                    String sorts,
                                    Integer userId,
                                    Boolean userScrapOnly);
    List<PlaceComment> findCommentsByPlaceId(long placeId);
    void addPlaceLike(long userId, long placeId);
    void addPlaceScrap(long userId, long scrapId);
    void addPlaceComment(Comment comment);
    int deletePlaceLike(long userId, long placeId);
    int deletePlaceScrap(long userId, long placeId);
    int likeDown(long placeId);
    int scrapDown(long placeId);
    int scoreUp(long placeId);
    boolean isScrapped(long placeId, long userId);
    String findTagByPlaceId(long placeId);
}

