package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.CustomPlaceListDTO;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.service.destination.DestinationRedisService;
import Trabook.PlanManager.service.destination.DestinationService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

    @ResponseBody
    @GetMapping("/general")
    public CustomPlaceListDTO getUserCustomPlaceList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> state,
            @RequestParam(required = false) List<String> subcategory,
            @RequestParam(defaultValue = "stars") String sorts,
            @RequestParam Integer pageSize,
            @RequestParam Integer pageNum) {
        log.info("/places/general");

        List<Place> customPlaceList = destinationService.getUserCustomPlaceList(search, state, subcategory, sorts);

        // 전체 페이지 수 계산
        Integer totalPages = (customPlaceList.size() + pageSize - 1) / pageSize; // 올림 처리

        // 페이지 번호가 유효한지 확인 (잘못된 pageNum이면 빈 리스트와 totalPages 반환)
        if (pageNum < 0 || pageNum >= totalPages) {
            return new CustomPlaceListDTO(Collections.emptyList(), totalPages);
        }

        // 해당 페이지에 맞는 시작과 끝 인덱스 계산
        int startIndex = pageNum * pageSize;
        int endIndex = Math.min(startIndex + pageSize, customPlaceList.size());

        // 서브리스트 반환 (페이지의 일부 요소와 전체 페이지 수)
        return new CustomPlaceListDTO(customPlaceList.subList(startIndex, endIndex), totalPages);
    }

}
