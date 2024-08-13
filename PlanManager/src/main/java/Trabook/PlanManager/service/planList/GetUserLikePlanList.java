package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.domain.plan.Plan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GetUserLikePlanList implements PlanListServiceInterface{
    @Override
    public List<Plan> getPlanList(long userId) {

        log.info("get user like plan list = {}",userId);
        return List.of();
    }

    @Override
    public boolean isAvailableService() {
        return false;
    }
}
