package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.service.DestinationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/destination")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @ResponseBody
    @GetMapping("/")
    public List<Place> getPlaceByCity(@RequestBody String cityId) {
        return destinationService.getPlaceListByCity(cityId);
    }
}
