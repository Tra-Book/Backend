package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.response.PlanListResponseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JdbcTemplatePlanListRepository implements PlanListRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePlanListRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<PlanListResponseDTO> findHottestPlan() {
        String sql = "SELECT *  " +
                "FROM Plan " +
                "ORDER BY likes DESC " +
                "LIMIT 10;";
        List<PlanListResponseDTO> result = jdbcTemplate.query(sql, planListRowMapper());
        return result;
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

    @Override
    public List<PlanListResponseDTO> findCustomPlanList(String search,
                                                        List<String> region,
                                                        Integer memberCount,
                                                        Integer duration,
                                                        String sorts,
                                                        Integer userId,
                                                        Boolean userScrapOnly) {
        String likeSearch = "%" + search + "%"; // SQL injection 방지
        List<Object> params = new ArrayList<>();

        String sql = "SELECT p.*, " +
                "CASE WHEN lp.planId IS NOT NULL THEN TRUE ELSE FALSE END AS isLiked, " +  // LIKE 여부 확인
                "CASE WHEN sp.planId IS NOT NULL THEN TRUE ELSE FALSE END AS isScrapped " +  // SCRAP 여부 확인
                "FROM Plan p " +
                "LEFT JOIN LikedPlan lp ON p.planId = lp.planId AND lp.userId = ? " +  // LikedPlan 테이블을 LEFT JOIN
                "LEFT JOIN ScrappedPlan sp ON p.planId = sp.planId AND sp.userId = ? " +  // ScrappedPlan 테이블을 LEFT JOIN
                "WHERE (p.title LIKE ? OR p.description LIKE ?) ";

        params.add(userId);
        params.add(userId);
        params.add(likeSearch);
        params.add(likeSearch);

        if(userScrapOnly) {
            sql += "AND (sp.userId = ?) ";
            params.add(userId);
        }

        if (region != null && !region.isEmpty()) {
            String regionPlaceholders = String.join(", ", Collections.nCopies(region.size(), "?"));
            sql += "AND p.region IN (" + regionPlaceholders + ") ";
            params.addAll(region);
        }

        sql += "AND (p.numOfPeople = ? OR ? IS NULL) " +
                "AND (DATEDIFF(p.endDate, p.startDate) + 1 = ? OR ? IS NULL) ";

        params.add(memberCount);
        params.add(memberCount);
        params.add(duration);
        params.add(duration);

        sql += "ORDER BY " +
                "CASE WHEN ? = 'likes' THEN p.likes " +
                "WHEN ? = 'numOfPeople' THEN p.numOfPeople " +
                "ELSE p.likes END DESC ";  // 기본값은 likes 로 정렬

        params.add(sorts);
        params.add(sorts);

        return jdbcTemplate.query(sql, generalPlanListRowMapper(), params.toArray());
    }

    private RowMapper<PlanListResponseDTO> generalPlanListRowMapper() {
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
                plan.setPlaceRegion(rs.getString("region"));
                plan.setNumOfComments(rs.getInt("numOfComments"));
                plan.setIsScrapped(rs.getBoolean("isScrapped"));
                plan.setIsLiked(rs.getBoolean("isLiked"));

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
