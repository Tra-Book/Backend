package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.DestinationReactionDto;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;
import Trabook.PlanManager.domain.destination.PlaceScrapRequestDTO;
import Trabook.PlanManager.response.ResponseMessage;
import Trabook.PlanManager.service.destination.DestinationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PlaceForModalDTO> getPlaceByPlaceId(@RequestParam("placeId") long placeId,@RequestHeader(value = "userId",required = false)Long userId ){
        PlaceForModalDTO result = destinationService.getPlaceModalByPlaceId(placeId);
        if(userId != null) {
            //System.out.println(destinationService.isScrapPlace(placeId,userId));
            //System.out.println("user Id = " + userId);
            result.getPlace().setScrapped(destinationService.isScrapPlace(placeId,userId));
        }
        return ResponseEntity.ok(result);
    }



    @ResponseBody
    @PostMapping("/scrap")
    public ResponseEntity<ResponseMessage> addPlaceScrap(@RequestBody PlaceScrapRequestDTO placeScrapRequestDTO, @RequestHeader("userId")long userId){
        String message = destinationService.addPlaceScrap(userId, placeScrapRequestDTO.getPlaceId());
        return ResponseEntity.ok(new ResponseMessage(message));
    }

    @ResponseBody
    @DeleteMapping("/scrap")
    public  ResponseEntity<ResponseMessage> deletePlaceScrap(@RequestBody PlaceScrapRequestDTO placeScrapRequestDTO,@RequestHeader("userId")long userId){
        String message = destinationService.deletePlaceScrap(userId, placeScrapRequestDTO.getPlaceId());
        return ResponseEntity.ok(new ResponseMessage(message));
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




}
