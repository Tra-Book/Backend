package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcTemplatePlanRepository implements PlanRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePlanRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Plan save(Plan plan, List<Schedule> scheduleList) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());

        String sql = "INSERT INTO Plan(userId,cityId,likes,scraps,dateCreated,title,content)" +
                "values(:userId,:cityId, :likes, :scraps, :dateCreated, :title, :content)";
        System.out.println(plan.toString());
        SqlParameterSource param = new BeanPropertySqlParameterSource(plan);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        //db에서 자동으로 1 증가한 값을 pk로 설정해주고 이값을 keyholder에 보관
        namedParameterJdbcTemplate.update(sql, param, keyHolder);
        long key = keyHolder.getKey().longValue();
        plan.setPlanId(key);

        return plan;
    }

    @Override
    public Optional<Plan> findById(long planId){
        String sql = "SELECT * FROM Plan WHERE planId = ?";
        List<Plan> result = jdbcTemplate.query(sql, planRowMapper(), planId);

        return result.stream().findAny();
    }

    @Override
    public Optional<Plan> findPlanByUserAndName(long userId, String planName) {

        List<Plan> result = jdbcTemplate.query("SELECT * FROM Plan WHERE userId = ? AND title = ?", planRowMapper(), userId, planName);
        return result.stream().findAny();
    }

    @Override
    public int deletePlan(long planId) {
        String sql = "DELETE FROM Plan WHERE planId = ? ";
        return jdbcTemplate.update(sql, planId);
    }

    @Override
    public int deleteLike(long userId, long planId) {
        String sql = "DELETE FROM LikedPlan WHERE userId = ? AND planId = ?";
        return jdbcTemplate.update(sql,userId,planId);
    }

    @Override
    public int deleteScrap(long userId, long planId) {
        String sql = "DELETE FROM ScrappedPlan WHERE userId = ? AND planId = ?";
        return jdbcTemplate.update(sql,userId,planId);
    }

    @Override
    public int deleteComment(long commentId) {
        String sql = "DELETE FROM PlanComment WHERE commentId = ?";
        return jdbcTemplate.update(sql,commentId);
    }

    /*
    @Override
    public List<Plan> findPlanList() {
        return super.findPlanList(); //몇개씩 가져와야 하는지 ?
    }
    */
    @Override
    public List<Plan> findUserPlanList(long userId) {
        String sql = "SELECT * " +
                "FROM Plan " +
                "WHERE userId = ?";
        return jdbcTemplate.query(sql,planRowMapper(),userId);
    }
    @Override
    public List<Plan> findUserLikePlanList(long userId) {
        String sql = "SELECT * " +
                "FROM Plan " +
                "JOIN LikedPlan on Plan.planId = LikedPlan.planId " +
                "WHERE LikedPlan.userId = ? ";
        return jdbcTemplate.query(sql,planRowMapper(),userId);
    }

    @Override
    public List<Plan> findUserScrapPlanList(long userId) {
        String sql = "SELECT * " +
                "FROM Plan " +
                "JOIN ScrappedPlan on Plan.planId = ScrappedPlan.planId " +
                "WHERE ScrappedPlan.userId = ? ";
        return jdbcTemplate.query(sql,planRowMapper(),userId);
    }

    @Override
    public List<Plan> findPlanListByCityId(long cityId) {
        return jdbcTemplate.query("SELECT * FROM Plans WHERE cityId =? ",planRowMapper(),cityId);
    }
/*
    @Override
    public List<Plan> planSearch(String keyword, PlanSearchDTO.Filters filters,String sorts) {
        String region = filters.getRegion();
        int memberCount = filters.getMemberCount();
        int duration = filters.getDuration();
        return jdbcTemplate.query("SELECT * FROM Plans WHERE planTitle like '%?% ",planRowMapper(),keyword);
    }
*/
    @Override
    public void likePlan(long userId,long planId) {
        String sql = "INSERT INTO LikedPlan(userId,planId) " +
                "values(?,?)";
        jdbcTemplate.update(sql,userId,planId);
        String sql2 = "UPDATE Plan SET likes = likes + 1 WHERE planId = ?";

        jdbcTemplate.update(sql2, planId);

    }

    @Override
    public void scrapPlan(long userId, long planId) {
        String sql1 = "INSERT INTO ScrappedPlan(userId,planId)" +
                "values(?, ?)";

        jdbcTemplate.update(sql1,userId,planId);

        String sql2 = "UPDATE Plan SET scraps = scraps + 1 WHERE planId = ?";

        jdbcTemplate.update(sql2,planId);

    }

    @Override
    public void addComment(Comment comment) {
        String sql = "INSERT INTO PlanComment( userId,planId,content,ref,refOrder,time)" +
                "values(?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                comment.getUserId(),
                comment.getPlanId(),
                comment.getContent(),
                comment.getRef(),
                comment.getRefOrder(),
                comment.getTime());
    }

    @Override
    public void clearStore() {
    }

    private RowMapper<Plan> planRowMapper() {
        return new RowMapper<Plan>() {
            @Override
            public Plan mapRow(ResultSet rs, int rowNum) throws SQLException {
                Plan plan = new Plan();
                plan.setTitle(rs.getString("title"));
                plan.setUserId(rs.getLong("userId"));
                plan.setPlanId(rs.getLong("planId"));
                plan.setCityId(rs.getLong("cityId"));
                plan.setLikes(rs.getInt("likes"));
                plan.setScraps(rs.getInt("scraps"));
                plan.setPublic(rs.getBoolean("isPublic"));
                plan.setDateCreated(rs.getString("dateCreated"));
                plan.setContent(rs.getString("content"));
                return plan;
            }


        };
    }

}
