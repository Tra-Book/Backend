package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.comment.CommentRequestDTO;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanCreateDTO;
import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.repository.customRowMapper.NestedRowMapper;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.domain.plan.DayPlan;
import Trabook.PlanManager.response.PlanResponseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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
    public Optional<DayPlan.Schedule> findSchedule(long planId, int day, int order) {
        String sql = "SELECT * FROM Schedule WHERE planId = ? AND `day` = ? AND `order` = ?";
        List<DayPlan.Schedule> result = jdbcTemplate.query(sql, scheduleRowMapper(), planId, day, order);
        return result.stream().findAny();

    }

    @Override
    public Optional<DayPlan> findDayPlan(long planId, int day) {
        String sql = "SELECT * FROM DayPlan WHERE planId = ? AND `day` = ?";
        List<DayPlan> result = jdbcTemplate.query(sql, dayPlanRowMapper(), planId, day);
        return result.stream().findAny();
    }

    @Override
    public long updatePlan(Plan plan) {
        String sql = "UPDATE Plan SET userId = ?,likes = ?, scraps = ?, title=?, description = ?," +
                " isPublic = ?,isFinished=?, numOfPeople = ?, budget = ?, planId=?,state=?,imgSrc=?,startDate=?, endDate=? " +
                "WHERE planId= ?";
        int update = jdbcTemplate.update(sql,
                plan.getUserId(),
                plan.getLikes(),
                plan.getScraps(),
                plan.getTitle(),
                plan.getDescription(),
                plan.isPublic(),
                plan.isFinished(),
                plan.getNumOfPeople(),
                plan.getBudget(),
                plan.getPlanId(),
                plan.getState(),
                plan.getImgSrc(),

                plan.getStartDate(),
                plan.getEndDate(),
                plan.getPlanId());

        System.out.println(plan.getPlanId());
        return plan.getPlanId();

    }

    @Override
    public long updateSchedule(DayPlan.Schedule schedule) {

        String sql = "UPDATE Schedule SET  `order` = ?, `time` = ?, placeId = ?" +
                " WHERE  planId = ? AND `day` = ? AND `order` = ?";
        int update = jdbcTemplate.update(sql,
                schedule.getOrder(),
                schedule.getTime(),
                schedule.getPlaceId(),
                schedule.getPlanId(),
                schedule.getDay(),
                schedule.getOrder());
        return update;
    }

    @Override
    public long updateDayPlan(DayPlan dayPlan) {
        String sql = "UPDATE DayPlan SET planId = ?,day = ?,startTime = ?,endTime = ? " +
                "WHERE planId = ? AND `day` = ?";
        int update = jdbcTemplate.update(sql,
                dayPlan.getPlanId(),
                dayPlan.getDay(),
                dayPlan.getStartTime(),
                dayPlan.getEndTime(), dayPlan.getPlanId(),dayPlan.getDay());
        return update;
    }

    @Override
    public long saveDayPlan(DayPlan dayPlan) {
        String sql = "INSERT INTO DayPlan(planId, day, startTime, endTime)" +
                "values(?,?,?,?)";
         return  jdbcTemplate.update(sql,
                dayPlan.getPlanId(),
                dayPlan.getDay(),
                dayPlan.getStartTime(),
                dayPlan.getEndTime()
        );


    }


    @Override
    public void saveSchedule(DayPlan.Schedule schedule) {
        String sql = "INSERT INTO Schedule(planId,`day`,`order`,`time`,placeId) "+
                "VALUES(?,?,?,?,?)";
        jdbcTemplate.update(sql,
                schedule.getPlanId(),
                schedule.getDay(),
                schedule.getOrder(),
                schedule.getTime(),
                schedule.getPlaceId());

    }

    @Override
    public Optional<Plan> findById(long planId){
        String sql = "SELECT * FROM Plan WHERE planId = ?";
        List<Plan> result = jdbcTemplate.query(sql, planRowMapper(), planId);
        return result.stream().findAny();
    }

    @Override
    public List<String> findTagsByPlanId(long planId) {
        String sql = "SELECT place.`subcategory` " +
                "FROM Plan p " +
                "INNER JOIN DayPlan dp on p.planId = dp.planId " +
                "INNER JOIN `Schedule` s on dp.planId = s.planId AND dp.day = s.day " +
                "INNER JOIN Place place on s.placeId = place.placeId " +
                "WHERE p.planId = ?";
        return jdbcTemplate.query(sql, new Object[]{planId}, (rs, rowNum) -> rs.getString("subcategory"));
    }

    @Override
    public PlanResponseDTO findTotalPlan(long planId) {
        String sql = "select p.*, dp.*, s.* " +
                "from Plan p " +
                "left join DayPlan dp on p.planId = dp.planId " +
                "left join `Schedule` s on dp.planId = s.planId and dp.day = s.day " +
                "where p.planId = ? ";

        Plan result = jdbcTemplate.query(sql, new Object[]{planId}, rs -> {
            int rowNum = 0;
            Plan plan = new Plan();
            while (rs.next()) {
                if (rowNum == 0) {
                    plan = planRowMapper().mapRow(rs, rs.getRow());
                    plan.setDayPlanList(new ArrayList<>());
                    rowNum++;
                } else {
                    DayPlan dayPlan = dayPlanRowMapper().mapRow(rs, rs.getRow());
                    dayPlan.setScheduleList(new ArrayList<>());
                    plan.getDayPlanList().add(dayPlan);
                    //DayPlan.Schedule schedule = scheduleRowMapperForTotalPlan().mapRow(rs, rs.getRow());
                    DayPlan.Schedule schedule = scheduleRowMapper().mapRow(rs,rs.getRow());
                    dayPlan.getScheduleList().add(schedule);

                }

            }
            return plan;
        });

        PlanResponseDTO planResponseDTO = new PlanResponseDTO();
        planResponseDTO.setPlan(result);
        return planResponseDTO;
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

        String sql = "UPDATE Plan" +
                "SET numOfComment = numOfComment - 1 " +
                "WHERE planId = (" +
                    "SELECT planId " +
                    "FROM Comment " +
                    "WHERE commentId = ?" +
                ");";

        String sql2 = "DELETE FROM PlanComment " +
                "WHERE commentId = ?;";

        jdbcTemplate.update(sql,commentId);
        return jdbcTemplate.update(sql2, commentId);
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
    public int deleteCommentByRef(long ref,long commentId,long planId) {
        String sql = "DELETE FROM PlanComment " +
                "WHERE parentId = ? ";
        String sql2 = "UPDATE Plan " +
                "SET numOfComment = numOfComment - ? " +
                "WHERE planId = ? ";
        int deletedCount = jdbcTemplate.update(sql, ref);
        System.out.println(deletedCount);
        return jdbcTemplate.update(sql2,deletedCount ,planId);

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
                "WHERE planId = ? " +
                "ORDER BY day ASC";

        return jdbcTemplate.query(sql,dayPlanRowMapper(),planId);

    }

    @Override
    public List<DayPlan.Schedule> findScheduleList(long planId,int day) {
        String sql = "SELECT * "+
                "FROM Schedule " +
                "WHERE planId = ? AND day = ?";
        return jdbcTemplate.query(sql,scheduleRowMapper(),planId,day);
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
    public long addComment(CommentRequestDTO comment) {
        String sql = "INSERT INTO PlanComment( userId,planId,content,parentId,refOrder,time)" +
                "values(?,?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,new String[]{"commentId"});
            ps.setLong(1,comment.getUserId());
            ps.setLong(2,comment.getPlanId());
            ps.setString(3,comment.getContent());
            ps.setLong(4,comment.getParentId());
            ps.setLong(5,comment.getRefOrder());
            ps.setString(6,comment.getTime());

            return ps;
        },keyHolder);
        long commentId = keyHolder.getKey().longValue();
        if(comment.getParentId()==0) { //본 댓글이면
            jdbcTemplate.update("UPDATE PlanComment SET parentId = ? WHERE commentId = ?",commentId,commentId);
        }
        String sql2 = "UPDATE Plan SET numOfComment = numOfComment + 1 WHERE planId = ?";
        jdbcTemplate.update(sql2,comment.getPlanId());
        return commentId;
    }

    @Override
    public void deleteDayPlanById(long planId, long day) {
        String sql = "DELETE FROM DayPlan WHERE planId = ? AND day = ?";
        jdbcTemplate.update(sql, planId, day);
    }

    @Override
    public void deleteScheduleById(long planId, long day) {
        String sql = "DELETE FROM Schedule WHERE planId = ? AND day = ?";
        jdbcTemplate.update(sql, planId, day);
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
                plan.setImgSrc(rs.getString("imgSrc"));
                plan.setBudget(rs.getInt("budget"));
                plan.setNumOfPeople(rs.getInt("numOfPeople"));
                plan.setDescription(rs.getString("description"));
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
                dayPlan.setStartTime(rs.getTime("startTime").toLocalTime());
                dayPlan.setEndTime(rs.getTime("endTime").toLocalTime());
                return dayPlan;
            }
        };
    }
/*
    private RowMapper<DayPlan.Schedule> scheduleRowMapperForTotalPlan() {
        return new RowMapper<DayPlan.Schedule>() {
            @Override
            public DayPlan.Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                DayPlan.Schedule schedule = new DayPlan.Schedule();
                schedule.setLongitude(rs.getDouble("x"));
                schedule.setLatitude(rs.getDouble("y"));
                schedule.setImageSrc(rs.getString("imageSrc"));
                schedule.setSubcategory(rs.getString("subcategory"));
                schedule.setPlaceName(rs.getString("placeName"));
                schedule.setAddress(rs.getString("address"));
                return schedule;
            }
        };
    }

 */
    private  RowMapper<DayPlan.Schedule> scheduleRowMapper() {
        return new RowMapper<DayPlan.Schedule>() {
            @Override
            public DayPlan.Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                DayPlan.Schedule schedule = new DayPlan.Schedule();
                schedule.setPlanId(rs.getLong("planId"));
                schedule.setDay(rs.getInt("day"));
                schedule.setTime(rs.getInt("time"));
                schedule.setPlaceId(rs.getLong("placeId"));
                schedule.setOrder(rs.getInt("order"));

                return schedule;
            }
        };
    }

    @Override
    public List<Comment> findCommentListByPlanId(long planId) {
        String sql = "SELECT * FROM PlanComment WHERE planId = ?";
        return jdbcTemplate.query(sql,commentRowMapper(),planId);
    }

    private RowMapper<Comment> commentRowMapper() {
        return new RowMapper<Comment>() {
            @Override
            public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
                Comment comment = new Comment();
                comment.setUser(new User());
                comment.setCommentId(rs.getLong("commentId"));
                comment.setParentId(rs.getLong("parentId"));
                comment.setContent(rs.getString("content"));
                comment.setPlanId(rs.getLong("planId"));
                comment.setTime(rs.getString("time"));
                comment.setRefOrder(rs.getInt("refOrder"));
                comment.getUser().setUserId(rs.getLong("userId"));
                return comment;
            }
        };
    }
/*
    private RowMapper<PlanResponseDTO> planTestRowMapper() {
        return new RowMapper<PlanResponseDTO>() {
            @Override
            public PlanResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                PlanResponseDTO planResponseDTO = new PlanResponseDTO();
                Plan plan = new Plan();
                plan.setTitle(rs.getString("title"));
                plan.setUserId(rs.getLong("userId"));
                plan.setPlanId(rs.getLong("planId"));
                plan.setLikes(rs.getInt("likes"));
                plan.setScraps(rs.getInt("scraps"));
                plan.setPublic(rs.getBoolean("isPublic"));
                plan.setState(rs.getString("state"));
                plan.setImgSrc(rs.getString("imgSrc"));
                plan.setBudget(rs.getInt("budget"));
                plan.setNumOfPeople(rs.getInt("numOfPeople"));
                plan.setDescription(rs.getString("description"));
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

                plan.setDayPlanList(new ArrayList<>());
                DayPlan dayPlan = new DayPlan();
                dayPlan.setDay(rs.getInt("day"));
                dayPlan.setPlanId(rs.getLong("planId"));
                dayPlan.setStartTime(rs.getTime("startTime").toLocalTime());
                dayPlan.setEndTime(rs.getTime("endTime").toLocalTime());

                plan.get


            }
        };
    }


 */
}
