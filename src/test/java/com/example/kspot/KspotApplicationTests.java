package com.example.kspot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class KspotApplicationTests {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private Integer tableExists(String table){
    // 테이블 존재 여부 확인
    return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_name = ?",
            Integer.class, table);
  }

  private List<String> columns(String table){
    // 필수 컬럼 존재 여부 확인
    return jdbcTemplate.queryForList(
            "SELECT column_name FROM information_schema.columns " +
                    "WHERE table_name = ?",
            String.class, table);
  }
  @Test
  void checkContentsStructure() {
    assertThat(tableExists("contents")).isEqualTo(1);

    List<String> cols = columns("contents");
    assertThat(cols).contains(
            "content_id", "category", "title",
            "poster_image_url", "release_date",
            "created_at", "updated_at"
    );
  }

  @Test
  void checkArtistsStructure() {
    assertThat(tableExists("artists")).isEqualTo(1);
    List<String> cols = columns("artists");
    assertThat(cols).contains(
            "artist_id", "name", "profile_image_url",
            "created_at", "updated_at"
    );
  }

  @Test
  void checkLocationsStructure() {
    assertThat(tableExists("locations")).isEqualTo(1);
    List<String> cols = columns("locations");
    assertThat(cols).contains(
            "location_id", "name", "address",
            "latitude", "longitude", "description",
            "created_at", "updated_at"
    );
  }

  @Test
  void insertAndSelectContents_shouldReturnExpectedRow() {
    // Given
    jdbcTemplate.update("""
    INSERT INTO contents(category,title,poster_image_url,release_date)
    VALUES (?,?,?,?)
  """, "drama", "케이팝데몬헌터스", "http://x/poster.jpg", LocalDate.parse("2025-05-30"));

    Long id = jdbcTemplate.queryForObject("""
    SELECT content_id FROM contents WHERE title=? ORDER BY content_id DESC LIMIT 1
  """, Long.class, "케이팝데몬헌터스");

    // When
    Map<String, Object> row = jdbcTemplate.queryForMap("""
    SELECT content_id,title,release_date FROM contents WHERE content_id=?
  """, id);

    // Then
    assertThat(row.get("content_id")).isEqualTo(id);
    assertThat(row.get("title")).isEqualTo("케이팝데몬헌터스");
    assertThat(row.get("release_date").toString()).startsWith("2025-05-30");
  }

  @Test
  void insertAndSelectArtists_shouldReturnExpectedRow() {
    // Given
    jdbcTemplate.update("""
    INSERT INTO artists(name, profile_image_url)
    VALUES (?,?)
  """, "박보검", "http://x/artist.jpg");

    Long id = jdbcTemplate.queryForObject("""
    SELECT artist_id FROM artists WHERE name=? ORDER BY artist_id DESC LIMIT 1
  """, Long.class, "박보검");

    // When
    Map<String, Object> row = jdbcTemplate.queryForMap("""
    SELECT artist_id, name, profile_image_url FROM artists WHERE artist_id=?
  """, id);

    // Then
    assertThat(row.get("artist_id")).isEqualTo(id);
    assertThat(row.get("name")).isEqualTo("박보검");
    assertThat(row.get("profile_image_url").toString()).startsWith("http://x/artist.jpg");
  }

  @Test
  void insertAndSelectLocations_shouldReturnExpectedRow() {
    // Given
    jdbcTemplate.update("""
    INSERT INTO locations(name, address, latitude, longitude, description)
    VALUES (?,?,?,?,?)
  """, "남산공원", "서울 중구 남산공원길 105",
            37.5511694, 126.9882266, "테스트 촬영지");

    Long id = jdbcTemplate.queryForObject("""
    SELECT location_id FROM locations WHERE name=? ORDER BY location_id DESC LIMIT 1
  """, Long.class, "남산공원");

    // When
    Map<String, Object> row = jdbcTemplate.queryForMap("""
    SELECT location_id, name, latitude, longitude FROM locations WHERE location_id=?
  """, id);

    // Then
    assertThat(row.get("location_id")).isEqualTo(id);
    assertThat(row.get("name")).isEqualTo("남산공원");
    assertThat(row.get("latitude").toString()).startsWith("37.551");
    assertThat(row.get("longitude").toString()).startsWith("126.988");
  }
}
