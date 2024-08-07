package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;
import Trabook.PlanManager.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    @Autowired
    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public String createPlan(Plan newPlan, List<Schedule> scheduleList) {
        if(validateDuplicatePlanName(newPlan)){
            return "planName already exists";
        } else {
            planRepository.save(newPlan,scheduleList);
            return "Plan saved!";
        }
    }

    public String deletePlan(Plan plan) {
        return "delete complete";
    }

    public List<Plan> getUserPlanList(String userId) {
        return planRepository.findUserPlanList(userId);
    }

    public List<Plan> getUserLikePlanList(String userId) {
        return planRepository.findUserLikePlanList(userId);
    }
    public String likePlan(String userId,String planId) {
        if(planRepository.findById(planId) != null) {
            int result = planRepository.likePlan(userId,planId);
            return "like complete";
        }
        else
            return "no plan exists";

    }

    public String scrapPlan(String userId, String planId) {
        if(planRepository.findById(planId) != null) {
            int result = planRepository.scrapPlan(userId,planId);
            return "scrap complete";
        } else
            return "no plan exists";
    }

    public List<Plan> getUserScrapPlanList(String userId){
            return planRepository.findUserScrapPlanList(userId);
    }
    public boolean validateDuplicatePlanName(Plan plan){

        return planRepository.findByUserAndName(plan.getUserId(),plan.getPlanName());

    }
}
