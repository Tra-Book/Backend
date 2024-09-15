package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanSearchFilterDTO {
    private String search;
    private int cityId;
    private int sortCode;
}
