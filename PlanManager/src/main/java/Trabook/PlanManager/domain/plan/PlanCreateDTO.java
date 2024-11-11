package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter @Getter
public class PlanCreateDTO {
    private long userId;
    private String state;
    private LocalDate startDate;
    private LocalDate endDate;



    public PlanCreateDTO(long userId, String state, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
