package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.SearchDestinationFilterDTO;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchDestinationService {
    private final DestinationRepository destinationRepository;

    private final static String SEARCH_KEY="";

    public SearchDestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public List<Place> getSearchResult(String search, String city, SearchDestinationFilterDTO.DestinationFilters destinationFilters){
        if(search == null) {
            search = "";
        }
        List<Place> result= null;

        //검색 로직 추가 해야 함.

        for(Place place : result)
            destinationRepository.scoreUp(place.getPlaceId());
        return result;
    }

}
