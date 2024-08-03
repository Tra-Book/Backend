package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    @Autowired
    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public String createPlan(Plan newPlan) {
        if(validateDuplicatePlanName(newPlan)){
            return "planName already exists";
        } else {
            planRepository.save(newPlan);
            return "Plan saved!";
        }
    }

    public boolean validateDuplicatePlanName(Plan plan){

        return planRepository.findByUserAndName(plan.getUserId(),plan.getPlanName());

    }
}
