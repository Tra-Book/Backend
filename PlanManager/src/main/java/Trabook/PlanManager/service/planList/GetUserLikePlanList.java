package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GetUserLikePlanList implements PlanListServiceInterface{
    private final PlanListRepository planListRepository;

    public GetUserLikePlanList(PlanListRepository planListRepository) {
        this.planListRepository = planListRepository;
    }


    @Override
    public List<PlanListResponseDTO> getPlanList(long userId) {

        log.info("get user like plan list = {}",userId);
        return planListRepository.findUserPlanList(userId);
    }

    @Override
    public boolean isAvailableService() {
        return false;
    }
}
