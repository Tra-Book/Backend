package Trabook.PlanManager.controller;

import Trabook.PlanManager.response.PlanResponseDTO;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.file.FileUploadService;
import Trabook.PlanManager.service.webclient.WebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/test")

public class TestController {
    private final WebClientService webClientService;
    private final PlanService planService;
    private final FileUploadService fileUploadService;

    @Autowired
    public TestController(WebClientService webClientService, PlanService planService, FileUploadService fileUploadService) {
        this.webClientService = webClientService;
        this.planService = planService;
        this.fileUploadService = fileUploadService;
    }

    @ResponseBody
    @GetMapping("")
    public PlanResponseDTO getPlanByPlanId(@RequestParam("planId")long planId, @RequestHeader(value = "userId", required = false) Long userId) {
        PlanResponseDTO result = planService.getPlan(planId, userId);
        return result;
    }


}
