package Trabook.PlanManager.configuration;

import Trabook.PlanManager.repository.destination.DestinationJdbcTemplateRepository;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import Trabook.PlanManager.repository.plan.JdbcTemplatePlanListRepository;
import Trabook.PlanManager.repository.plan.JdbcTemplatePlanRepository;

import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.service.PlanRedisService;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.destination.DestinationService;
import Trabook.PlanManager.service.destination.SearchDestinationService;
import Trabook.PlanManager.service.planList.GetUserLikePlanList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
public class SpringConfig {

    private final DataSource dataSource;
    private final RedisTemplate<String, String> redisTemplate;

    public SpringConfig(DataSource dataSource,RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.dataSource = dataSource;
    }

   @Bean
   public WebClient webClient() {
        return WebClient.create("http://35.216.124.162:4060");
   }

    @Bean
    public PlanService planService() {
        return new PlanService(planRepository(),destinationRepository(), planListRepository(),redisTemplate);
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
