package Trabook.PlanManager.domain.plan;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class PlanRequestDTO {
    private Plan plan;
    private List<Schedule> scheduleList;

    public PlanRequestDTO() {}
}
