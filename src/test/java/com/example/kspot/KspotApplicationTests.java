package com.example.kspot;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class KspotApplicationTests {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void checkContentsTable(){
    String sql = "SELECT content_id, category, title, poster_image_url, release_date FROM contents";
    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

    assertThat(results).isNotEmpty();

    boolean contents = results.stream()
        .anyMatch(row -> row.get("content_id").toString().equals("7")
        && "기생충".equals(row.get("title"))
        && "2019-05-30".equals(row.get("release_date").toString()));
    assertThat(contents).isTrue();
  }

  @Test
  void checkArtistsTable(){
    String sql = "SELECT artist_id, name, profile_image_url FROM artists";
    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

    assertThat(results).isNotEmpty();

    boolean contents = results.stream()
        .anyMatch(row -> row.get("artist_id").toString().equals("1")
            && "방탄소년단".equals(row.get("name")));
    assertThat(contents).isTrue();
  }

  @Test
  void checkLocationsTable(){
    String sql = "SELECT location_id, name, address, latitude, longitude FROM locations";
    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

    assertThat(results).isNotEmpty();

    boolean contents = results.stream()
        .anyMatch(row -> row.get("location_id").toString().equals("3")
            && "경복궁".equals(row.get("name"))
            && "서울특별시 종로구 사직로 161".equals(row.get("address"))
            && new BigDecimal("37.57961700").compareTo((BigDecimal) row.get("latitude")) == 0
            && new BigDecimal("126.97704100").compareTo((BigDecimal) row.get("longitude")) == 0); // 숫자 비교를 위해 BigDecimal 사용
    assertThat(contents).isTrue();
  }
}
