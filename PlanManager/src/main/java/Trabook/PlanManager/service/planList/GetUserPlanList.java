package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserPlanList implements PlanListServiceInterface{
    private final PlanListRepository planListRepository;
    private final PlanRepository planRepository;
    public GetUserPlanList(PlanListRepository planListRepository, PlanRepository planRepository) {
        this.planListRepository = planListRepository;
        this.planRepository = planRepository;
    }

    @Override
    public List<PlanListResponseDTO> getPlanList(long userId) {

        List<PlanListResponseDTO> userPlanList = planListRepository.findUserPlanList(userId);
        for(PlanListResponseDTO planListResponseDTO : userPlanList){
            if(planRepository.isScrapped(planListResponseDTO.getPlanId(),userId)){
                planListResponseDTO.setIsScrapped(true);
            } else {
                planListResponseDTO.setIsScrapped(false);
            }
            if(planRepository.isLiked(planListResponseDTO.getPlanId(),userId)){
                planListResponseDTO.setIsLiked(true);
            } else {
                planListResponseDTO.setIsLiked(false);
            }
        }
        return userPlanList;
    }

    @Override
    public boolean isAvailableService() {
        return false;
    }
}
