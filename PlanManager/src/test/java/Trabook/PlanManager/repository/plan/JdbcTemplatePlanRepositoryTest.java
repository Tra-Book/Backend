package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.Plan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcTemplatePlanRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcTemplatePlanRepository jdbcTemplatePlanRepository;

    @Test
    void save() {
        Plan plan = new Plan(-1,3,1,true,0,0,"2024-08-20 10:00:00","test","test");

        doNothing().when(jdbcTemplate).update(anyString(), any(Object[].class));

        jdbcTemplatePlanRepository.save(plan,null);

       /*
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(plan);

        Optional<Plan> result = jdbcTemplatePlanRepository.findById(plan.getPlanId());
        */

        // JdbcTemplate의 update 메서드가 정확히 호출되었는지 검증
        verify(jdbcTemplate, times(1)).update(
                eq("INSERT INTO plans ( cityId,likes,scraps,title,content,dateCreated,userId description) VALUES (?,?,?,?,?,?, ?, ?)"),
                 eq(plan.getCityId()), eq(plan.getLikes()), eq(plan.getScraps()), eq(plan.getTitle()),eq(plan.getContent()),eq(plan.getDateCreated()),(eq(plan.getUserId()))
        );

    }

    @Test
    void findById() {
    }
}