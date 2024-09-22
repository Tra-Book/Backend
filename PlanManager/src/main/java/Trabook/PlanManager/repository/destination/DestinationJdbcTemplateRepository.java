package Trabook.PlanManager.repository.destination;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.destination.Place;
import org.springframework.data.geo.Point;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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

    /*
    ### 중요 ###
    review 추가되면 review 랑 조인 + ORDER BY review DESC 필요
     */
    @Override
    public List<Place> findCustomPlaceList(String search, List<String> state, List<String> subcategory, String sorts) {
        String likeSearch = "%" + search + "%"; // SQL injection 방지
        List<Object> params = new ArrayList<>();
        // 기본 쿼리
        String sql = "SELECT * , ST_X(coordinate) AS x, ST_Y(coordinate) AS y " +
                "FROM Place "+
                "JOIN City ON Place.cityId = City.cityId " +
                "WHERE (Place.placeName LIKE ? OR Place.description LIKE ?) ";
        params.add(likeSearch); // placeName LIKE ?
        params.add(likeSearch); // description LIKE ?

        // state 리스트가 비어있지 않으면 IN 절 추가
        if (state != null && !state.isEmpty()) {
            if(!state.get(0).equals("전체")) {
                String statePlaceholders = String.join(", ", Collections.nCopies(state.size(), "?"));
                sql += ("AND City.stateName IN (" + statePlaceholders + ") ");
                params.addAll(state);
            }
        }

        // subcategory 리스트가 비어있지 않으면 IN 절 추가
        if (subcategory != null && !subcategory.isEmpty()) {
            if(!subcategory.get(0).equals("전체")) {
                String subcategoryPlaceholders = String.join(", ", Collections.nCopies(subcategory.size(), "?"));
                sql += ("AND Place.subcategory IN (" + subcategoryPlaceholders + ") ");
                params.addAll(subcategory);
            }
        }
        // sort 생략
        if(sorts.equals("numOfAdded")) sorts = "" + "scraps";
        sql += "ORDER BY " + sorts + " DESC ";
        return jdbcTemplate.query(sql, placeRowMapper(), params.toArray());
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
                place.setRatingScore(rs.getDouble("ratingScore"));
                place.setAddress(rs.getString("address"));
                return place;
            }
        };
    }


}
