package Trabook.PlanManager.domain.test;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
public class ImageTest {
    private MultipartFile image;
    private long planId;

    public  ImageTest() {}

    public ImageTest(MultipartFile image, long planId) {
        this.image = image;
        this.planId = planId;
    }
}
