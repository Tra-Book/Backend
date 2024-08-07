package Trabook.PlanManager.repository;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JdbcTemplatePlanRepository implements PlanRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePlanRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Plan save(Plan plan, List<Schedule> scheduleList) {
        jdbcTemplate.query("INSERT INTO Schedule(scheduleId, date, startTime, endTime, planId) values(?,?,?,?,?)");
        return jdbcTemplate.query("INSERT INTO Plan(planId,userId,cityId,isPublic,likes,scraps,name) values(?,?,?,?,?,?,?");
    }

    @Override
    public boolean findByUserAndName(String userId, String planName) {
        return super.findByUserAndName(userId, planName);
    }

    @Override
    public boolean deletePlan(Plan plan) {
        return jdbcTemplate.query("delete from `test_db`.`Plan` where (`planId`=?) values(?)")
    }

    @Override
    public List<Plan> findPlanList() {
        return super.findPlanList(); //몇개씩 가져와야 하는지 ?
    }

    @Override
    public List<Plan> findUserPlanList(String userId) {
        return jdbcTemplate.query("(select * from Plan where userId = ?) values(?)");
    }
    @Override
    public List<Plan> findUserLikePlanList(String userId) {
        return jdbcTemplate.query("(SELECT * FROM Likes where userId = ?) values(?)");
    }

    @Override
    public List<Plan> findUserScrapPlanList(String userId) {
        return jdbcTemplate.query("SELECT * FROM Scraps WHERE userId = ?) values(?) ");
    }


    @Override
    public int likePlan(String userId,String planId) {
        jdbcTemplate.query("INSERT INTO Likes(userId,planId) values (?,?)");
        return jdbcTemplate.query("UPDATE Plan SET likes = likes + 1 WHERE planId = ?");
    }

    @Override
    public int scrapPlan(String userId, String planId) {
        jdbcTemplate.query("insert into Scraps(userId,planId) values (?,?");
        return jdbcTemplate.query("UPDATE Plan SET scraps = scaprs + 1 WHERE planId = ?");
    }
    @Override
    public void clearStore() {
        super.clearStore();
    }
}
