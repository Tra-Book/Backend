package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
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
        String sql = "INSERT INTO Plan(planId,userId,cityId,isPublic,likes,scraps,name)" +
                "values(:planId,:userId,:cityId, :isPublic, :likes, :scraps, :name)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(plan);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        //db에서 자동으로 1 증가한 값을 pk로 설정해주고 이값을 keyholder에 보관
        jdbcTemplate.update(sql, param, keyHolder);
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

        List<Plan> result = jdbcTemplate.query("SELECT * FROM Plan WHERE userId = ? AND planName = ?", planRowMapper(), userId, planName);
        return result.stream().findAny();
    }

    @Override
    public Optional<Plan> deletePlan(long planId) {
        List<Plan> result = jdbcTemplate.query("delete from Plan where planId=?", planRowMapper(), planId);
        return result.stream().findAny();
    }
    /*
    @Override
    public List<Plan> findPlanList() {
        return super.findPlanList(); //몇개씩 가져와야 하는지 ?
    }
    */
    @Override
    public List<Plan> findUserPlanList(long userId) {
        return jdbcTemplate.query("(select * from Plan where userId = ?",planRowMapper(),userId);
    }
    @Override
    public List<Plan> findUserLikePlanList(long userId) {
        return jdbcTemplate.query("(SELECT * FROM Likes where userId = ?",planRowMapper(),userId);
    }

    @Override
    public List<Plan> findUserScrapPlanList(long userId) {
        return jdbcTemplate.query("SELECT * FROM Scraps WHERE userId = ? ",planRowMapper(),userId);
    }

    @Override
    public List<Plan> findPlanListByCityId(long cityId) {
        return jdbcTemplate.query("SELECT * FROM Plans WHERE cityId =? ",planRowMapper(),cityId);
    }

    @Override
    public void likePlan(long userId,long planId) {
        String sql = "INSERT INTO Likes(userId,planId)" +
                "values(:userId, :planId)";
        jdbcTemplate.update(sql,userId,planId);
        String sql2 = "UPDATE Plan SET likes = likes + 1 WHERE planId = ?";

        jdbcTemplate.update(sql2, planId);

    }

    @Override
    public void scrapPlan(long userId, long planId) {
        String sql1 = "INSERT INTO Scraps(userId,planId)" +
                "values(:userId, planId)";

        jdbcTemplate.update(sql1,userId,planId);

        String sql2 = "UPDATE Plan SET scraps = scraps + 1 WHERE planId = ?";

        jdbcTemplate.update(sql2,planId);

    }

    @Override
    public void clearStore() {
    }

    private RowMapper<Plan> planRowMapper() {
        return new RowMapper<Plan>() {
            @Override
            public Plan mapRow(ResultSet rs, int rowNum) throws SQLException {
                Plan plan = new Plan();
                plan.setPlanName(rs.getString("planName"));
                plan.setUserId(rs.getLong("userId"));
                plan.setPlanId(rs.getLong("planId"));
                plan.setCityId(rs.getLong("cityId"));
                plan.setLikes(rs.getInt("likes"));
                plan.setScraps(rs.getInt("scraps"));
                plan.setPublic(rs.getBoolean("isPublic"));
                return plan;
            }


        };
    }

}
