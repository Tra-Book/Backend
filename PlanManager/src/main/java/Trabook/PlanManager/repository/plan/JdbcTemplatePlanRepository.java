package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanCreateDTO;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.domain.plan.DayPlan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class JdbcTemplatePlanRepository implements PlanRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePlanRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public long createPlan(PlanCreateDTO planCreateDTO) {
        String sql = "INSERT INTO Plan(userId,state,startDate,endDate)" +
                "VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        long planId;
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,new String[]{"planId"});
            ps.setLong(1,planCreateDTO.getUserId());
            ps.setString(2,planCreateDTO.getState());
            ps.setDate(3, Date.valueOf(planCreateDTO.getStartDate()));
            ps.setDate(4, Date.valueOf(planCreateDTO.getEndDate()));
            return ps;
        },keyHolder);
        planId = keyHolder.getKey().longValue();
        return planId;

    }

    @Override
    public long updatePlan(Plan plan) {
        String sql = "UPDATE Plan SET userId = ?,likes = ?, scraps = ?, title=?, description = ?," +
                " isPublic = ?, numOfPeople = ?, budget = ?, planId=?,state=? "
                +"WHERE planId= ?";
        int update = jdbcTemplate.update(sql,
                plan.getUserId(),
                plan.getLikes(),
                plan.getScraps(),
                plan.getTitle(),
                plan.getDescription(),
                plan.isPublic(),
                plan.getNumOfPeople(),
                plan.getBudget(),
                plan.getPlanId(),
                plan.getState(),plan.getPlanId());
        System.out.println(plan.getPlanId());
        return plan.getPlanId();

    }

    @Override
    public long updateSchedule(long DayPlanId, DayPlan.Schedule schedule) {
        String sql = "UPDATE Schedule SET dayPlanId = ?, scheduleId = ?,order = ?,time = ?,placeId = ?" +
                " WHERE dayPlanId = ?";
        int update = jdbcTemplate.update(sql,
                schedule.getDayPlanId(),
                schedule.getScheduleId(),
                schedule.getOrder(),
                schedule.getTime(),
                schedule.getPlaceId());
        return schedule.getScheduleId();
    }

    @Override
    public long updateDayPlan(DayPlan dayPlan) {
        String sql = "UPDATE DayPlan SET dayPlanId = ?,planId = ?,day = ?,startTime = ?,endTime = ? " +
                "WHERE dayPlanId = ?";
        int update = jdbcTemplate.update(sql,
                dayPlan.getDayPlanId(),
                dayPlan.getPlanId(),
                dayPlan.getDay(),
                dayPlan.getStartTime(),
                dayPlan.getEndTime(), dayPlan.getDayPlanId());
        return dayPlan.getDayPlanId();
    }

    @Override
    public long saveDayPlan(DayPlan dayPlan) {
        String sql = "INSERT INTO DayPlan(planId, dayPlanId, day, startTime, endTime)" +
                "values(?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"scheduleId"});
            ps.setLong(1, dayPlan.getPlanId());
            ps.setLong(2, dayPlan.getDayPlanId());
            ps.setInt(3, dayPlan.getDay());
            ps.setTime(4, Time.valueOf(dayPlan.getStartTime()));
            ps.setTime(5, Time.valueOf(dayPlan.getEndTime()));
            return ps;
        }, keyHolder);
        dayPlan.setDayPlanId(keyHolder.getKey().longValue());
        return dayPlan.getDayPlanId();
    }

    @Override
    public void saveSchedule(long dayPlanId, DayPlan.Schedule schedule) {
        String sql = "INSERT INTO Schedule(DayPlanId,`order`,`time`,placeId) "+
                "VALUES(?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"scheduleId"});
            ps.setLong(1,dayPlanId);
            ps.setInt(2,schedule.getOrder());
            ps.setInt(3,schedule.getTime());
            ps.setLong(4,schedule.getPlaceId());
            return ps;
        });
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
        //String sql2 = "UPDATE Plan SET scraps = scraps + 1 WHERE planId = ?";
        int result = jdbcTemplate.update(sql, userId, planId);
        return result;
    }

    @Override
    public int deleteScrap(long userId, long planId) {
        String sql = "DELETE FROM ScrappedPlan WHERE userId = ? AND planId = ?";
       // String sql2 = "UPDATE Plan SET scraps = scraps - WHERE planId = ?";
        int result = jdbcTemplate.update(sql, userId, planId);
        return result;
    }

    @Override
    public int deleteComment(long commentId) {
        String sql = "DELETE FROM PlanComment WHERE commentId = ?";
        return jdbcTemplate.update(sql,commentId);
    }

    @Override
    public int upLike(long planId) {
        String sql = "UPDATE Plan SET likes = likes + 1 WHERE planId = ?";
        int result = jdbcTemplate.update(sql, planId);
        return result;
    }

    @Override
    public int downLike(long planId) {
        String sql = "UPDATE Plan SET likes = likes - 1 WHERE planId = ?";
        int result = jdbcTemplate.update(sql, planId);
        String sql2 = "SELECT likes FROM Plan WHERE planId=?";
        Integer updatedLikes = jdbcTemplate.queryForObject(sql2, new Object[]{planId}, Integer.class);

        return updatedLikes;
    }

    @Override
    public int upScrap(long planId) {
        String sql = "UPDATE Plan SET scraps = scraps + 1 WHERE planId = ?";
        int result = jdbcTemplate.update(sql, planId);

        return result;
    }

    @Override
    public int downScrap(long planId) {
        String sql = "UPDATE Plan SET scraps = scraps - 1 WHERE planId = ?";
        jdbcTemplate.update(sql,planId);
        String sql2 = "SELECT scraps FROM Plan WHERE planId=?";
        Integer updatedScraps = jdbcTemplate.queryForObject(sql2, new Object[]{planId}, Integer.class);
        return updatedScraps;
    }

    @Override
    public boolean isLiked(long planId, long userId) {
        String sql = "SELECT EXISTS ( " +
                "SELECT 1 " +
                "FROM LikedPlan " +
                "WHERE planId = ? AND userId = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class,planId,userId);

    }

    @Override
    public boolean isScrapped(long planId, long userId) {
        String sql = "SELECT EXISTS ( " +
                "SELECT 1 " +
                "FROM ScrappedPlan " +
                "WHERE planId = ? AND userId = ?)";
        return jdbcTemplate.queryForObject(sql, new Object[]{planId, userId}, Boolean.class);

    }

    @Override
    public boolean isCommentExists(long commentId) {
        String sql = "SELECT EXISTS ( SELECT 1 " +
                "FROM PlanComment " +
                "WHERE commentId = ?)";
        return jdbcTemplate.queryForObject(sql,new Object[]{commentId}, Boolean.class);
    }


    @Override
    public int deleteCommentByRef(long ref) {
        String sql = "DELETE FROM PlanComment " +
                "WHERE ref = ? ";
        return jdbcTemplate.update(sql, ref);

    }

    @Override
    public List<PlanListResponseDTO> findUserLikePlanList(long userId) {
        return List.of();
    }

    @Override
    public Optional<Comment> findCommentById(long commentId) {
        String sql = "SELECT * "+
                "FROM PlanComment "+
                "WHERE commentId = ? ";
        List<Comment> result = jdbcTemplate.query(sql, commentRowMapper(), commentId);
        return result.stream().findAny();
    }

    @Override
    public List<DayPlan> findDayPlanListByPlanId(long planId) {
        String sql = "SELECT * " +
                "FROM DayPlan "+
                "WHERE planId = ?";
        return jdbcTemplate.query(sql,dayPlanRowMapper(),planId);

    }

    @Override
    public List<DayPlan.Schedule> findScheduleListByDayPlanList(long dayPlanId) {
        String sql = "SELECT * "+
                "FROM Schedule " +
                "WHERE dayPlanId = ?";
        return jdbcTemplate.query(sql,scheduleRowMapper(),dayPlanId);
    }

    @Override
    public void likePlan(long userId,long planId) {
        String sql = "INSERT INTO LikedPlan(userId,planId) " +
                "values(?,?)";
        jdbcTemplate.update(sql,userId,planId);

    }

    @Override
    public void scrapPlan(long userId, long planId) {
        String sql1 = "INSERT INTO ScrappedPlan(userId,planId)" +
                "values(?, ?)";
        jdbcTemplate.update(sql1,userId,planId);

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
                plan.setLikes(rs.getInt("likes"));
                plan.setScraps(rs.getInt("scraps"));
                plan.setPublic(rs.getBoolean("isPublic"));
                plan.setState(rs.getString("state"));
                Date startDate = rs.getDate("startDate");
                if (startDate != null) {
                    plan.setStartDate(startDate.toLocalDate());
                } else {
                    plan.setStartDate(null); // 또는 기본값 설정
                }

                Date endDate = rs.getDate("endDate");
                if (endDate != null) {
                    plan.setEndDate(endDate.toLocalDate());
                } else {
                    plan.setEndDate(null); // 또는 기본
                }
                return plan;
            }
        };
    }
    private RowMapper<DayPlan> dayPlanRowMapper() {
        return new RowMapper<DayPlan>() {
            @Override
            public DayPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
                DayPlan dayPlan = new DayPlan();
                dayPlan.setDay(rs.getInt("day"));
                dayPlan.setPlanId(rs.getLong("planId"));
                dayPlan.setDayPlanId(rs.getLong("dayPlanId"));
                dayPlan.setStartTime(rs.getTime("startTime").toLocalTime());
                dayPlan.setEndTime(rs.getTime("endTime").toLocalTime());
                return dayPlan;
            }
        };
    }

    private  RowMapper<DayPlan.Schedule> scheduleRowMapper() {
        return new RowMapper<DayPlan.Schedule>() {
            @Override
            public DayPlan.Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                DayPlan.Schedule schedule = new DayPlan.Schedule();
                schedule.setTime(rs.getInt("time"));
                schedule.setPlaceId(rs.getLong("placeId"));
                schedule.setScheduleId(rs.getLong("scheduleId"));
                schedule.setOrder(rs.getInt("order"));
                return schedule;
            }
        };
    }

    private RowMapper<Comment> commentRowMapper() {
        return new RowMapper<Comment>() {
            @Override
            public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
                Comment comment = new Comment();
                comment.setCommentId(rs.getLong("commentId"));
                comment.setRef(rs.getLong("ref"));
                comment.setContent(rs.getString("content"));
                comment.setPlanId(rs.getLong("planId"));
                comment.setTime(rs.getTimestamp("time").toLocalDateTime());
                comment.setRefOrder(rs.getInt("refOrder"));
                comment.setUserId(rs.getLong("userId"));
                return comment;
            }
        };
    }
}
