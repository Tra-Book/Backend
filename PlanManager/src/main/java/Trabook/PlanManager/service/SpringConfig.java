package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.repository.PlanMemoryRepository;
import Trabook.PlanManager.repository.PlanRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public PlanService planService() {
        return new PlanService(planRepository());
    }

    @Bean
    public PlanRepository planRepository() {
        return new PlanMemoryRepository();
    }
}
