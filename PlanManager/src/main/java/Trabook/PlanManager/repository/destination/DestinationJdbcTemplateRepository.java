package Trabook.PlanManager.repository.destination;

import Trabook.PlanManager.domain.comment.Comment;
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

    @Override
    public String findTagByPlaceId(long placeId) {
        String sql = "SELECT subcategory FROM Place WHERE placeId = ?";
        return jdbcTemplate.queryForObject(sql, String.class,placeId);

    }

    //Optional 여부
    @Override
    public Optional<Place> findByPlaceId(long placeId) {
        List<Place> result = jdbcTemplate.query("SELECT *,ST_X(coordinate) AS x, ST_Y(coordinate) AS y" +
                " FROM Place WHERE placeId =?", placeRowMapper(), placeId);
        return result.stream().findAny();
    }


    @Override
    public List<Place> findPlaceListByCity(long cityId) {
        return jdbcTemplate.query("SELECT *,ST_X(coordinate) AS x, ST_Y(coordinate) AS y" +
                " FROM Place WHERE cityId = ?",placeRowMapper(),cityId);
    }

    @Override
    public List<Place> findPlaceListByUserScrap(long userId) {
        String sql = "SELECT Place.placeId as placeId,scraps,cityId,address,placeName,star,category,imageSrc,ratingScore, ST_X(coordinate) AS x, ST_Y(coordinate) AS y " +
                "FROM Place " +
                "JOIN ScrappedPlace ON Place.placeId = ScrappedPlace.placeId " +
                "WHERE ScrappedPlace.userId = ?";
        return jdbcTemplate.query(sql,placeRowMapper(),userId);
    }

    @Override
    public List<Place> findHottestPlaceList() {
        String sql = "SELECT * , ST_X(coordinate) AS x, ST_Y(coordinate) AS y " +
                "FROM Place " +
                "ORDER BY ratingScore DESC " +
                "LIMIT 5;";
        List<Place> result = jdbcTemplate.query(sql, placeRowMapper());
        return result;
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
    public int scoreUp(long placeId){
        String sql = "UPDATE Place SET ratingScore = ratingScore + 1 WHERE placeId = ?";
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
                place.setNumOfAdded(rs.getInt("scraps"));
                place.setAddress(rs.getString("address"));
                place.setLongitude(rs.getDouble("x"));
                place.setLatitude(rs.getDouble("y"));
                place.setCategory(rs.getString("category"));
                place.setCityId(rs.getLong("cityId"));
                place.setImageSrc(rs.getString("imageSrc"));
                place.setStar(rs.getLong("star"));
                place.setRatingScore(rs.getLong("ratingScore"));
                return place;
            }
        };
    }


}
