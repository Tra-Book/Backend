package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.Place;
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


    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public DestinationRedisService( @Qualifier("topPlaceRedisTemplate")RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private ObjectMapper objectMapper = new ObjectMapper();


    public List<Place> getHottestPlace(){
        objectMapper.registerModule(new SimpleModule().addDeserializer(Point.class, new PointDeserializer()));

        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
        Set<String> topPlaces = zsetOps.reverseRange("topPlaces", 0, 4);

        List<Place> top5Places = new ArrayList<>();

        try {
            for (String jsonPlace : topPlaces) {
                System.out.println(jsonPlace);
                Place place = objectMapper.readValue(jsonPlace, Place.class);
                top5Places.add(place);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return top5Places;
    }
}
