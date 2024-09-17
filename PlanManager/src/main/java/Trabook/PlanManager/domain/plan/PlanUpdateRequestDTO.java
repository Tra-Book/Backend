package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
public class PlanUpdateRequestDTO {
    Plan plan;
    MultipartFile image;

    public PlanUpdateRequestDTO(Plan plan, MultipartFile image) {
        this.plan = plan;
        this.image = image;
    }

    @Override
    public String toString() {
        return "PlanUpdateRequestDTO{" +
                "plan=" + plan +
                ", image=" + image +
                '}';
    }
}
