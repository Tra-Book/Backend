package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.destination.PlaceComment;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanComment;
import Trabook.PlanManager.domain.plan.PlanGeneralDTO;
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
                //"ORDER BY likes DESC " + 이미 인덱싱 되어서 파일 sort 굳이 할 필요 없음
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
                "INNER JOIN ScrappedPlan on Plan.planId = ScrappedPlan.planId " +
                "WHERE ScrappedPlan.userId = ? ";
        return jdbcTemplate.query(sql,planListRowMapper(),userId);
    }

    @Override
    public List<PlanGeneralDTO> findCustomPlanList(String search,
                                                   List<String> state,
                                                   Integer numOfPeople,
                                                   Integer duration,
                                                   String sorts,
                                                   Integer userId,
                                                   Boolean userScrapOnly) {
        String likeSearch = "%" + search + "%"; // SQL injection 방지
        List<Object> params = new ArrayList<>();

        String sql = "SELECT p.*, pc.*, " +
                "CASE WHEN lp.planId IS NOT NULL THEN TRUE ELSE FALSE END AS isLiked, " +  // LIKE 여부 확인
                "CASE WHEN sp.planId IS NOT NULL THEN TRUE ELSE FALSE END AS isScrapped " +  // SCRAP 여부 확인
                "FROM Plan p " +
                "LEFT JOIN PlanComment pc ON pc.planId = p.planId " +
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

        if (state != null && !state.isEmpty()) {
            String regionPlaceholders = String.join(", ", Collections.nCopies(state.size(), "?"));
            sql += "AND p.state IN (" + regionPlaceholders + ") ";
            params.addAll(state);
        }

        sql += "AND (p.numOfPeople = ? OR ? IS NULL) " +
                "AND (DATEDIFF(p.endDate, p.startDate) + 1 = ? OR ? IS NULL) ";

        params.add(numOfPeople);
        params.add(numOfPeople);
        params.add(duration);
        params.add(duration);

        if ("likes".equals(sorts)) {
            sql += "ORDER BY p.likes DESC";
        } else if ("numOfPeople".equals(sorts)) {
            sql += "ORDER BY p.numOfPeople DESC";
        } else if ("startDate".equals(sorts)) {
            sql += "ORDER BY p.startDate DESC";
        } else if ("numOfComment".equals(sorts)) {
            sql += "ORDER BY p.numOfComment DESC";
        } else {
            sql += "ORDER BY p.likes DESC";  // 기본값은 likes로 정렬
        }





        return jdbcTemplate.query(sql, generalPlanListRowMapper(), params.toArray());
    }


    private RowMapper<PlanGeneralDTO> generalPlanListRowMapper() {
        return new RowMapper<PlanGeneralDTO>() {
            @Override
            public PlanGeneralDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                PlanListResponseDTO plan = new PlanListResponseDTO();
                plan.setPlanTitle(rs.getString("title"));
                plan.setPlanId(rs.getLong("planId"));
                plan.setLikes(rs.getInt("likes"));
                plan.setScraps(rs.getInt("scraps"));
                plan.setPublic(rs.getBoolean("isPublic"));
                plan.setFinished(rs.getBoolean("isFinished"));
                //plan.setDateCreated(rs.getString("dateCreated"));
                plan.setDescription(rs.getString("description"));
                plan.setState(rs.getString("region"));
                plan.setNumOfComment(rs.getInt("numOfComment"));
                plan.setIsScrapped(rs.getBoolean("isScrapped"));
                plan.setIsLiked(rs.getBoolean("isLiked"));
                plan.setImgSrc(rs.getString("imgSrc"));
                plan.setNumOfPeople(rs.getInt("numOfPeople"));
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

                List<PlanComment> comments = new ArrayList<>();
                int commentCount = 0;

                do {
                    long commentId = rs.getLong("commentId");
                    if(commentCount >= 10) continue;
                    if (commentId != 0) {
                        PlanComment comment = new PlanComment();
                        comment.setCommentId(commentId);
                        comment.setUserId(rs.getLong("userId"));
                        comment.setPlanId(rs.getLong("planId"));
                        comment.setContent(rs.getString("content"));
                        comment.setRefOrder(rs.getInt("refOrder"));
                        comment.setTime(rs.getString("time"));
                        commentCount++;
                        comments.add(comment);
                    }
                } while (rs.next() && rs.getLong("planId") == plan.getPlanId());

                return new PlanGeneralDTO(plan, comments);
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
                plan.setState(rs.getString("state"));
                plan.setPublic(rs.getBoolean("isPublic"));
                plan.setFinished(rs.getBoolean("isFinished"));
                plan.setImgSrc(rs.getString("imgSrc"));
                //plan.setDateCreated(rs.getString("dateCreated"));
                plan.setDescription(rs.getString("description"));
                plan.setNumOfPeople(rs.getInt("numOfPeople"));
                plan.setNumOfComment(rs.getInt("numOfComment"));
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
