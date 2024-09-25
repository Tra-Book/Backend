package Trabook.PlanManager.repository.destination;


import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.destination.Place;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository {
    Optional<Place> findByPlaceId(long placeId);

    List<Place> findPlaceListByCity(long cityId);
    List<Place> findPlaceListByUserScrap(long userId);
    List<Place> findHottestPlaceList();
    List<Place> findCustomPlaceList(String search,
                                    List<String> state,
                                    List<String> subcategory,
                                    String sorts,
                                    Integer userId);


    void addPlaceLike(long userId, long placeId);
    void addPlaceScrap(long userId, long scrapId);
    void addPlaceComment(Comment comment);
    int deletePlaceLike(long userId, long placeId);
    int deletePlaceScrap(long userId, long placeId);
    int likeDown(long placeId);
    int scrapDown(long placeId);
    int scoreUp(long placeId);

    String findTagByPlaceId(long placeId);
}

