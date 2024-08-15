package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.DestinationReactionDto;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.service.destination.DestinationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/place")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @ResponseBody
    @GetMapping("/")
    public Optional<Place> getPlaceByPlaceId(@RequestParam("placeId") String placeId ){
        return destinationService.getPlaceByPlaceId(placeId);
    }

    @ResponseBody
    @PostMapping("/reaction")
    public String addPlaceReaction(@RequestBody DestinationReactionDto destinationReactionDto) {
        return destinationService.addPlaceReaction(destinationReactionDto);
    }
    @ResponseBody
    @DeleteMapping("/reaction")
    public String deletePlanReaction(@RequestBody DestinationReactionDto destinationReactionDto) {
        return destinationService.deletePlaceReaction(destinationReactionDto);
    }

    @ResponseBody
    @GetMapping("/")
    public List<Place> getPlaceByCity(@RequestBody String cityId) {
        return destinationService.getPlaceListByCity(cityId);
    }



    @ResponseBody
    @GetMapping("/places")
    public List<Place> getPlaces(@RequestBody ) {

    }

}
