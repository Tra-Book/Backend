package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserScrapPlanList implements PlanListServiceInterface{

    private final PlanListRepository planListRepository;
    private final PlanRepository planRepository;
    public GetUserScrapPlanList(PlanListRepository planListRepository, PlanRepository planRepository) {
        this.planListRepository = planListRepository;
        this.planRepository = planRepository;
    }
    @Override
    public List<PlanListResponseDTO> getPlanList(long userId) {
        List<PlanListResponseDTO> userScrappedPlanList = planListRepository.findUserScrappedPlanList(userId);

        for(PlanListResponseDTO planListResponseDTO : userScrappedPlanList){

            planListResponseDTO.setIsScrapped(true);

            if(planRepository.isLiked(planListResponseDTO.getPlanId(), userId)){
                planListResponseDTO.setIsLiked(true);
            } else{
                planListResponseDTO.setIsLiked(false);
            }
        }
        return userScrappedPlanList;
    }

    @Override
    public boolean isAvailableService() {
        return false;
    }
}
