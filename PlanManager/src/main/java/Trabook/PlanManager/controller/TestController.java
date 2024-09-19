package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.test.ImageTest;
import Trabook.PlanManager.service.file.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private final FileUploadService fileUploadService;

    public TestController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PatchMapping("/image")
    public ResponseEntity<Void> testImageUpload(ImageTest imageTest) {
        log.info("testImageUpload");
        try {
            fileUploadService.uploadPlanImage(imageTest.getImage(), imageTest.getPlanId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
