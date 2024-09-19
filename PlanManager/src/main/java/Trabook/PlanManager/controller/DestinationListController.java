package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.service.destination.DestinationRedisService;
import Trabook.PlanManager.service.destination.DestinationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/places")
public class DestinationListController {
    private final DestinationService destinationService;
    private final DestinationRedisService destinationRedisService;

    @Autowired
    public DestinationListController(DestinationService destinationService,DestinationRedisService destinationRedisService) {
        this.destinationService = destinationService;
        this.destinationRedisService = destinationRedisService;
    }

    @ResponseBody
    @GetMapping("/popular")
    public List<Place> getHottestPlace() {
        return destinationService.getHottestPlace();
    }

    @ResponseBody
    @GetMapping("/popular-redis")
    public List<Place> getHottestPlaceRedis(){
        return destinationRedisService.getHottestPlace();

    }

    @ResponseBody
    @GetMapping("/scrap")
    public List<Place> getUserScrapPlace(@RequestParam(name="userId") long userId) {
        return destinationService.getPlaceListByUserScrap(userId);
    }
}
