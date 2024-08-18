package Trabook.PlanManager.service;

import Trabook.PlanManager.repository.destination.DestinationJdbcTemplateRepository;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import Trabook.PlanManager.repository.plan.JdbcTemplatePlanRepository;

import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.service.planList.GetUserLikePlanList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    private final DataSource dataSource;

    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Bean
    public PlanService planService() {
        return new PlanService(planRepository());
    }

    @Bean
    public PlanRepository planRepository() {
        return new JdbcTemplatePlanRepository(dataSource);
    }

    @Bean
    public GetUserLikePlanList getUserLikePlanList() {
        return new GetUserLikePlanList(planRepository());
    }
    @Bean
    public DestinationRepository destinationRepository() {return new DestinationJdbcTemplateRepository(dataSource);
    }
}
