package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;
import Trabook.PlanManager.domain.plan.PlanSearchDTO;
import Trabook.PlanManager.repository.plan.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    @Autowired
    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public String createPlan(Plan newPlan, List<Schedule> scheduleList) {
        if(validateDuplicatePlanName(newPlan).isPresent()){
            return "planName already exists";
        } else {
            planRepository.save(newPlan,scheduleList);
            return "Plan saved!";
        }
    }

    public String deletePlan(long planId) {
        if(planRepository.deletePlan(planId) == 1)
            return "delete complete";
        else {
            return "error";
        }
    }

    public String deleteLike(long userId,long planId){
        if(planRepository.deleteLike(userId,planId) == 1)
            return "delete complete";
        else
            return "error";
    }
    public String deleteScrap(long userId, long planId) {
        if(planRepository.deleteScrap(userId,planId) == 1)
            return "delete complete";
        else
            return "error";
    }

    public String deleteComment(long commentId) {
        if(planRepository.deleteComment(commentId) == 1)
            return "delete complete";
        else
            return "error";
    }

    public String likePlan(long userId,long planId) {
        if(planRepository.findById(planId).isPresent()) {
            planRepository.likePlan(userId,planId);
            return "like complete";
        }
        else
            return "no plan exists";

    }

    public String scrapPlan(long userId, long planId) {
        if(planRepository.findById(planId).isPresent()) {
            planRepository.scrapPlan(userId,planId);
            return "scrap complete";
        } else
            return "no plan exists";
    }

    public List<Plan> getUserPlanList(long userId) {
        return planRepository.findUserPlanList(userId);
    }

    public List<Plan> getUserLikePlanList(long userId) {
        return planRepository.findUserLikePlanList(userId);
    }

    public List<Plan> getUserScrapPlanList(long userId){
            return planRepository.findUserScrapPlanList(userId);
    }

    public List<Plan> getPlanListByCityId(long cityId) {
        return planRepository.findPlanListByCityId(cityId);
    }
/*
    public List<Plan> getPlanSearch(PlanSearchDTO planSearchDTO){
        return planRepository.planSearch(planSearchDTO.getKeyword(),planSearchDTO.getFilters(),planSearchDTO.getSorts());
    }
*/

    // null인지 아닌지 확인해서 boolean으로 반환하는 방식으로 바꾸기
    public Optional<Plan> validateDuplicatePlanName(Plan plan){

        return planRepository.findPlanByUserAndName(plan.getUserId(),plan.getPlanTitle());

    }


}
