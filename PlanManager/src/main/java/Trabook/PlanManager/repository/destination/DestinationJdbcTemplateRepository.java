package Trabook.PlanManager.repository.destination;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class    DestinationJdbcTemplateRepository implements DestinationRepository {

    private final JdbcTemplate jdbcTemplate;

    public DestinationJdbcTemplateRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public String findTagByPlaceId(long placeId) {
        String sql = "SELECT subcategory FROM Place WHERE placeId = ?";
        return jdbcTemplate.queryForObject(sql, String.class,placeId);

    }

    @Override
    public void updateNumOfAdded(long placeId) {
        String sql = "UPDATE Place SET numOfAdded = numOfAdded + 1 WHERE placeId = ?";
         jdbcTemplate.update(sql, placeId);
    }

    @Override
    public List<PlaceComment> findCommentsByPlaceId(long placeId) {
        String sql = "SELECT * " +
                "FROM PlaceComment " +
                "WHERE placeId = ? " +
                "ORDER BY `date` " +
                "LIMIT 10;";
        return jdbcTemplate.query(sql,placeCommentRowMapper(),placeId);
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
                "LIMIT 10;";
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

    @Override
    public boolean isScrapped(long placeId, long userId) {
        String sql = "SELECT EXISTS ( " +
                "SELECT 1 " +
                "FROM ScrappedPlace " +
                "WHERE placeId = ? AND userId = ?)";
        return jdbcTemplate.queryForObject(sql, new Object[]{placeId, userId}, Boolean.class);

    }

    /*
### 중요 ###
review 추가되면 review 랑 조인 + ORDER BY review DESC 필요
 */

    //////////////// findCustomPlaceList 위치 내릴게 합칠때 위로 올리지마 ////////////////////////
    @Override
    public List<PlaceForModalDTO> findCustomPlaceList(String search, List<String> state, List<String> category,
                                                      String sorts, Integer userId,
                                                      Boolean userScrapOnly) {
        String likeSearch = "%" + search + "%"; // SQL injection 방지
        List<Object> params = new ArrayList<>();

        // 기본 쿼리
        String sql = "SELECT Place.*, ST_X(coordinate) AS x, ST_Y(coordinate) AS y, " +
                "CASE WHEN sp.placeId IS NOT NULL THEN TRUE ELSE FALSE END AS isScrapped," +
                "pc.commentId, pc.content, pc.date " +  // scrap 여부 확인
                "FROM Place " +
                "JOIN City ON Place.cityId = City.cityId " +
                "LEFT JOIN PlaceComment pc ON pc.placeId = Place.placeId " +
                "LEFT JOIN ScrappedPlace sp ON Place.placeId = sp.placeId AND sp.userId = ? " +  // ScrappedPlace를 LEFT JOIN
                "WHERE (Place.placeName LIKE ? OR Place.description LIKE ?) ";

        params.add(userId);
        params.add(likeSearch);  // placeName LIKE ?
        params.add(likeSearch);  // description LIKE ?

        if(userScrapOnly) {
            sql += "AND (sp.userId = ?) ";
            params.add(userId);
        }

        // state 리스트가 비어있지 않으면 IN 절 추가
        if (state != null && !state.isEmpty()) {
            if (!state.get(0).equals("전체")) {
                String statePlaceholders = String.join(", ", Collections.nCopies(state.size(), "?"));
                sql += ("AND City.stateName IN (" + statePlaceholders + ") ");
                params.addAll(state);
            }
        }

        // subcategory 리스트가 비어있지 않으면 IN 절 추가
        if (category != null && !category.isEmpty()) {
            if (!category.get(0).equals("전체")) {
                String subcategoryPlaceholders = String.join(", ", Collections.nCopies(category.size(), "?"));
                sql += ("AND Place.category IN (" + subcategoryPlaceholders + ") ");
                params.addAll(category);
            }
        }

        // 정렬 조건 처리

        sql += "ORDER BY " +
                "CASE WHEN ? = 'numOfAdded' THEN Place.numOfAdded " +
                "WHEN ? = 'star' THEN Place.star " +
                "ELSE Place.star END DESC ";
//                "date DESC ";
//
//
//        // 파라미터 추가
        params.add(sorts);  // 첫 번째 CASE 조건 (numOfAdded, star)
        params.add(sorts);  // 두 번째 CASE 조건 (numOfAdded, star)
        return jdbcTemplate.query(sql, generalPlaceRowMapper(), params.toArray());
    }

    private RowMapper<PlaceForModalDTO> generalPlaceRowMapper() {
        return new RowMapper<PlaceForModalDTO>() {
            @Override
            public PlaceForModalDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                Place place = new Place();
                place.setPlaceName(rs.getString("placeName"));
                place.setPlaceId(rs.getLong("placeId"));
                place.setNumOfAdded(rs.getInt("numOfAdded"));
                place.setAddress(rs.getString("address"));
                place.setLongitude(rs.getDouble("x"));
                place.setLatitude(rs.getDouble("y"));
                place.setCategory(rs.getString("category"));
                place.setSubcategory(rs.getString("subcategory"));
                place.setCityId(rs.getLong("cityId"));
                place.setImgSrc(rs.getString("imageSrc"));
                place.setStar(rs.getLong("star"));
                //place.setRatingScore(rs.getDouble("ratingScore"));
                place.setAddress(rs.getString("address"));
                place.setIsScrapped(rs.getBoolean("isScrapped"));
                place.setScraps(rs.getInt("scraps"));
                //place.setZipcode(rs.getString("zipcode"));
                place.setDescription(rs.getString("description"));
                place.setNumOfReview(rs.getInt("numOfReview"));

                List<PlaceComment> comments = new ArrayList<>();
                int commentCount = 0;

                do {
                    long commentId = rs.getLong("commentId");
                    if (commentId != 0) {
                        PlaceComment comment = new PlaceComment();
                        comment.setCommentId(commentId);
                        comment.setPlaceId(rs.getLong("placeId"));
                        comment.setContent(rs.getString("content"));
                        comment.setDate(rs.getString("date"));
                        comments.add(comment);
                        commentCount++;
                    }
                } while (rs.next() && rs.getLong("placeId") == place.getPlaceId() && commentCount < 10);

                // PlaceForModalDTO 객체 생성
                return new PlaceForModalDTO(place, comments);

            }
        };
    }

    private RowMapper<Place> placeRowMapper() {
        return new RowMapper<Place>() {
            @Override
            public Place mapRow(ResultSet rs, int rowNum) throws SQLException {
                Place place = new Place();
                place.setPlaceName(rs.getString("placeName"));
                place.setPlaceId(rs.getLong("placeId"));
                place.setNumOfAdded(rs.getInt("numOfAdded"));
                place.setAddress(rs.getString("address"));
                place.setLongitude(rs.getDouble("x"));
                place.setLatitude(rs.getDouble("y"));
                place.setCategory(rs.getString("category"));
                place.setSubcategory(rs.getString("subcategory"));
                place.setCityId(rs.getLong("cityId"));
                place.setImgSrc(rs.getString("imageSrc"));
                place.setStar(rs.getLong("star"));
                place.setRatingScore(rs.getDouble("ratingScore"));
                place.setAddress(rs.getString("address"));
                place.setScraps(rs.getInt("scraps"));
                place.setNumOfReview(rs.getInt("numOfReview"));
                return place;
            }
        };
    }

    private RowMapper<PlaceComment> placeCommentRowMapper() {
        return new RowMapper<PlaceComment>() {
            public PlaceComment mapRow(ResultSet rs, int rowNum) throws SQLException {
                PlaceComment placeComment = new PlaceComment();
                placeComment.setPlaceId(rs.getLong("placeId"));
                placeComment.setCommentId(rs.getLong("commentId"));
                placeComment.setContent(rs.getString("content"));
                placeComment.setDate(rs.getString("date"));
                return placeComment;
            }
        };
    }
/*
    private RowMapper<PlaceForModalDTO> placeForModalRowMapper() {
        return new RowMapper<PlaceForModalDTO>() {
            public PlaceForModalDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                PlaceForModalDTO placeForModalDTO = new PlaceForModalDTO();
                placeForModalDTO.setPlaceId(rs.getLong("placeId"));
                placeForModalDTO.setPlaceName(rs.getString("placeName"));
                placeForModalDTO.setPlaceId(rs.getLong("placeId"));
                placeForModalDTO.setNumOfAdded(rs.getInt("numOfAdded"));
                placeForModalDTO.setAddress(rs.getString("address"));
                placeForModalDTO.setLatitude(rs.getDouble("latitude"));
                placeForModalDTO.setLongitude(rs.getDouble("longitude"));
                placeForModalDTO.setCategory(rs.getString("category"));
                placeForModalDTO.setSubcategory(rs.getString("subcategory"));
                placeForModalDTO.setCityId(rs.getLong("cityId"));
                placeForModalDTO.setImageSrc(rs.getString("imageSrc"));
                placeForModalDTO.setStar(rs.getLong("star"));
                return placeForModalDTO;
            }
        };
    }

 */
}
