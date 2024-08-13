package Trabook.PlanManager.repository.destination;

import Trabook.PlanManager.domain.destination.City;
import Trabook.PlanManager.domain.destination.Place;
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
    public Optional<Place> findByPlaceId(String placeId) {
        List<Place> result = jdbcTemplate.query("SELECT * FROM Place WHERE placeId =?", memberRowMapper(), placeId);
        return result.stream().findAny();
    }


    @Override
    public List<Place> findPlaceListByCity(String cityId) {
        return jdbcTemplate.query("SELECT * FROM Place WHERE cityId = ?",memberRowMapper(),cityId);
    }

    private RowMapper<Place> memberRowMapper() {
        return new RowMapper<Place>() {
            @Override
            public Place mapRow(ResultSet rs, int rowNum) throws SQLException {
                Place place = new Place();
                place.setPlaceName(rs.getString("placeName"));
                place.setPlaceId(rs.getString("placeId"));
                place.setScraps(rs.getInt("scraps"));
                place.setLikes(rs.getInt("likes"));
                place.setAddress(rs.getString("address"));
                return place;
            }
        };
    }


}
