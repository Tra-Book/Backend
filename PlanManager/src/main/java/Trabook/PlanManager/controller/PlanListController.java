package Trabook.PlanManager.controller;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.destination.DestinationRedisService;
import Trabook.PlanManager.service.planList.GetUserLikePlanList;
import Trabook.PlanManager.service.planList.GetUserPlanList;
import Trabook.PlanManager.service.planList.GetUserScrapPlanList;
import Trabook.PlanManager.service.planList.PlanListServiceInterface;
import Trabook.PlanManager.service.webclient.WebClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "PlanList API", description = "API test for CRUD Plans")
@Slf4j
@RestController
@RequestMapping("/plans")
public class PlanListController {

    private final PlanService planService;
    private final Map<String, PlanListServiceInterface> planListServiceInterfaceMap;
    private final WebClientService webClientService;
    private final DestinationRedisService destinationRedisService;

    //PlanListServiceInterface 인터페이스를 구현한 모든 서비스들이 자동으로 주입됨. 스프링이 자동으로 이 인터페이스를 구현한
    //모든 빈을 찾아서 리스트로 제공한다 ㄷㄷ..
    @Autowired
    public PlanListController(List<PlanListServiceInterface> planListService,
                              PlanService planService,
                              WebClientService webClientService,
                              DestinationRedisService destinationRedisService) {
        this.webClientService= webClientService;
        this.destinationRedisService = destinationRedisService;
        this.planService = planService;
        this.planListServiceInterfaceMap = planListService.stream().collect(Collectors.toMap(
                service -> {
                    if(service instanceof GetUserLikePlanList) return "likes";
                    if(service instanceof GetUserPlanList) return "user";
                    if(service instanceof GetUserScrapPlanList) return "scrap";
                    return "default";
                },
                service -> service //?? 이문구 문법적으로 알아보기
                //stream.collect.Coolectore.toMap 이것도 문법적으로 닷 ㅣ알아보기
        ));
    }


    @ResponseBody
    @GetMapping("")
    public List<PlanListResponseDTO> getPlanList(@RequestParam(name="type") String type, @RequestHeader(name="userId") long userId) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(type);
        List<PlanListResponseDTO> planList = planListService.getPlanList(userId);
        log.info("{}'s plans = {}", userId, planList);
        return planList;
    }

    @ResponseBody
    @GetMapping("/general")
    public List<PlanListResponseDTO> getCustomPlans(
            @RequestParam String search,
            @RequestParam(required = false) List<String> region,
            @RequestParam(required = false) Integer memberCount,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false, defaultValue = "likes") String sorts) {
        log.info("/plans/general");

        return planService.findCustomPlanList(search, region, memberCount, duration, sorts);
    }


}
