package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;
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

    public String deletePlan(Plan plan) {
        return "delete complete";
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
    // null인지 아닌지 확인해서 boolean으로 반환하는 방식으로 바꾸기
    public Optional<Plan> validateDuplicatePlanName(Plan plan){

        return planRepository.findPlanByUserAndName(plan.getUserId(),plan.getPlanName());

    }
}