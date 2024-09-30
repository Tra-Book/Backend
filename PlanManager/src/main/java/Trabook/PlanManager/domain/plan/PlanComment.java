package Trabook.PlanManager.domain.plan;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PlanComment {
    private Long commentId;
    private Long userId;
    private Long planId;
    private String content;
    private Integer refOrder;
    private String time;
}
