package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DestinationRedisService {


    private final DestinationRepository destinationRepository;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public DestinationRedisService(@Qualifier("topRedisTemplate")RedisTemplate<String, String> redisTemplate, DestinationRepository destinationRepository) {
        this.redisTemplate = redisTemplate;
        this.destinationRepository = destinationRepository;
    }

    private ObjectMapper objectMapper = new ObjectMapper();


    public List<PlaceForModalDTO> getHottestPlace(Long userId){
        objectMapper.registerModule(new SimpleModule().addDeserializer(Point.class, new PointDeserializer()));

        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
        Set<String> topPlaces = zsetOps.reverseRange("topPlaces", 0, 9);

        List<Place> top10Places = new ArrayList<>();
        List<PlaceForModalDTO> result = new ArrayList<>();
        try {
            for (String jsonPlace : topPlaces) {
                //System.out.println(jsonPlace);
                Place place = objectMapper.readValue(jsonPlace, Place.class);
                top10Places.add(place);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
