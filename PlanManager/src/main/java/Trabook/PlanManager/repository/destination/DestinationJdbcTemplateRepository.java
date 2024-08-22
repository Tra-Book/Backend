package Trabook.PlanManager.repository.destination;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.destination.City;
import Trabook.PlanManager.domain.destination.Place;
import org.springframework.data.geo.Point;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DestinationJdbcTemplateRepository implements DestinationRepository {

    private final JdbcTemplate jdbcTemplate;

    public DestinationJdbcTemplateRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //Optional 여부
    @Override
    public Optional<Place> findByPlaceId(long placeId) {
        List<Place> result = jdbcTemplate.query("SELECT * FROM Place WHERE placeId =?", placeRowMapper(), placeId);
        return result.stream().findAny();
    }


    @Override
    public List<Place> findPlaceListByCity(long cityId) {
        return jdbcTemplate.query("SELECT * FROM Place WHERE cityId = ?",placeRowMapper(),cityId);
    }

    @Override
    public List<Place> findPlaceListByUserScrap(long userId) {
        String sql = "SELECT * " +
                "FROM Place " +
                "JOIN ScrappedPlace ON Place.placeId = ScrappedPlace.placeId " +
                "WHERE ScrappedPlace.userId = ?";
        return jdbcTemplate.query(sql,placeRowMapper(),userId);
    }

    @Override
    public void addPlaceLike(long userId, long placeId) {
        String sql = "INSERT INTO LikedPlace(userId,placeId)" +
                "values(?, ?)";
        jdbcTemplate.update(sql,userId,placeId);
        String sql2 = "UPDATE Place SET likes = likes + 1 WHERE placeId = ?";

        jdbcTemplate.update(sql2, placeId);
    }

    @Override
    public void addPlaceScrap(long userId, long placeId) {
        String sql = "INSERT INTO ScrappedPlace(userId,placeId)" +
                "values(?, ?)";
        jdbcTemplate.update(sql,userId,placeId);
        String sql2 = "UPDATE Place SET scraps = scraps + 1 WHERE placeId = ?";

        jdbcTemplate.update(sql2, placeId);
    }

    @Override
    public void addPlaceComment(Comment comment) {

    }

    @Override
    public int deletePlaceLike(long userId, long placeId) {
        String sql = "DELETE FROM LikedPlace WHERE userId = ? AND placeId = ?";

        return jdbcTemplate.update(sql,userId,placeId);

    }
    @Override
    public int likeDown(long placeId) {
        String sql = "UPDATE Place SET likes = likes - 1 WHERE placeId = ?";
        return jdbcTemplate.update(sql,placeId);
    }

    @Override
    public int deletePlaceScrap(long userId, long placeId) {
        String sql = "DELETE FROM ScrappedPlace WHERE userId = ? AND placeId = ?"; //1번 쿼리 잘 안됐는데 2번쿼리 잘되는 거 고치기
        return jdbcTemplate.update(sql,userId,placeId);
    }
    @Override
    public int scrapDown(long placeId) {
        String sql = "UPDATE Place SET scraps = scraps - 1 WHERE placeId = ?";
        return jdbcTemplate.update(sql,placeId);

    }
    private RowMapper<Place> placeRowMapper() {
        return new RowMapper<Place>() {
            @Override
            public Place mapRow(ResultSet rs, int rowNum) throws SQLException {
                Place place = new Place();
                place.setPlaceName(rs.getString("placeName"));
                place.setPlaceId(rs.getLong("placeId"));
                place.setScraps(rs.getInt("scraps"));
                place.setLikes(rs.getInt("likes"));
                place.setAddress(rs.getString("address"));
               // place.setXMap(rs.getDouble("xMap"));
                //place.setYMap(rs.getDouble("yMap"));
                String pointString = rs.getString("location");
                if(pointString != null && !pointString.isEmpty()) {
                    String[] points = pointString.replace("POINT(", "").replace(")", "").split(" ");
                    double latitude = Double.parseDouble(points[0]);
                    double altitude = Double.parseDouble(points[1]);
                    place.setGeography(new Point(latitude,altitude));
                }
                place.setCategory(rs.getString("category"));
                place.setCityId(rs.getLong("cityId"));
                place.setImageSrc(rs.getString("imageSrc"));
                place.setRating(rs.getLong("rating"));
                return place;
            }
        };
    }


}
