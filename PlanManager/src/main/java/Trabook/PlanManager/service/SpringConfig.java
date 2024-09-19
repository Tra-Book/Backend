package Trabook.PlanManager.service;

import Trabook.PlanManager.repository.destination.DestinationJdbcTemplateRepository;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import Trabook.PlanManager.repository.plan.JdbcTemplatePlanListRepository;
import Trabook.PlanManager.repository.plan.JdbcTemplatePlanRepository;

import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.service.destination.DestinationService;
import Trabook.PlanManager.service.destination.SearchDestinationService;
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
        return new PlanService(planRepository(),destinationRepository(), planListRepository());
    }

    @Bean
    public PlanRepository planRepository() {
        return new JdbcTemplatePlanRepository(dataSource);
    }
    @Bean
    public PlanListRepository planListRepository() {
        return new JdbcTemplatePlanListRepository(dataSource);
    }

    @Bean
    public GetUserLikePlanList getUserLikePlanList() {
        return new GetUserLikePlanList(planListRepository());
    }
    @Bean
    public DestinationRepository destinationRepository() {return new DestinationJdbcTemplateRepository(dataSource);
    }
    @Bean
    public DestinationService destinationService() {
        return new DestinationService(destinationRepository());
    }
    @Bean
    public SearchDestinationService searchDestinationService() {
        return new SearchDestinationService(destinationRepository());
    }



}
