package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.PlanListResponseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTemplatePlanListRepository implements PlanListRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePlanListRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<PlanListResponseDTO> findUserPlanList(long userId) {
        String sql = "SELECT * "+
                "FROM Plan "+
                "WHERE userId = ?";
        return jdbcTemplate.query(sql,planListRowMapper(),userId);
    }

    @Override
    public List<PlanListResponseDTO> findUserScrappedPlanList(long userId) {
        String sql = "SELECT * " +
                "FROM Plan " +
                "JOIN ScrappedPlan on Plan.planId = ScrappedPlan.planId " +
                "WHERE ScrappedPlan.userId = ? ";
        return jdbcTemplate.query(sql,planListRowMapper(),userId);
    }



    private RowMapper<PlanListResponseDTO> planListRowMapper() {
        return new RowMapper<PlanListResponseDTO>() {
            @Override
            public PlanListResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                PlanListResponseDTO plan = new PlanListResponseDTO();
                plan.setPlanTitle(rs.getString("title"));
                plan.setPlanId(rs.getLong("planId"));
                plan.setLikes(rs.getInt("likes"));
                plan.setScraps(rs.getInt("scraps"));
                plan.setPublic(rs.getBoolean("isPublic"));
                plan.setFinished(rs.getBoolean("isFinished"));
                //plan.setDateCreated(rs.getString("dateCreated"));
                plan.setDescription(rs.getString("description"));
                if (rs.getDate("startDate") != null) {
                    plan.setStartDate(rs.getDate("startDate").toLocalDate());
                } else {
                    plan.setStartDate(null); // null로 설정하거나 기본 값을 설정할 수 있음
                }

                if (rs.getDate("endDate") != null) {
                    plan.setEndDate(rs.getDate("endDate").toLocalDate());
                } else {
                    plan.setEndDate(null); // null로 설정하거나 기본 값을 설정할 수 있음
                }

                return plan;
            }


        };
    }

}
