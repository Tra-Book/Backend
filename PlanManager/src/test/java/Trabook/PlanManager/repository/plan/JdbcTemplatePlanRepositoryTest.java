package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanCreateDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class JdbcTemplatePlanRepositoryTest {


    @Autowired
    private PlanRepository planRepository;

    @Test
    void create() {
        PlanCreateDTO planCreateDTO = new PlanCreateDTO(3,"경기도", LocalDate.parse("2024-09-01"),LocalDate.parse("2024-09-08"));
        long plan = planRepository.createPlan(planCreateDTO);
        Optional<Plan> result = planRepository.findById(plan);
        Plan resultPlan = result.get();

        Assertions.assertThat(resultPlan.getPlanId()).isEqualTo(plan);
    }

    @Test
    void findById() {
    }
}