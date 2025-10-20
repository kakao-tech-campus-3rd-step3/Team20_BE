package com.example.kspot.external.tmdb.service;

import com.example.kspot.external.tmdb.exception.CsvParsingException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class contentLoctionService {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public contentLoctionService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  public void importFromCsv(BufferedReader br) {
    try (CSVReader csvReader = new CSVReader(br)) {

      // 헤더 스킵
      csvReader.readNext();

      String[] cols;
      while ((cols = csvReader.readNext()) != null) {
        // 컬럼 개수가 부족하면 스킵
        if (cols.length < 14) {
          throw new CsvParsingException("컬럼 수 부족: 최소 6개 필요, 현재 컬럼 수 : " + cols.length);
        }

        String mediaType = cols[1].trim().replaceAll("^\"|\"$", "").toLowerCase();
        String title = cols[2].trim();
        String locationName = cleanText(cols[3]);
        String sceneDescription = cols[5].trim();

        if (!mediaType.equals("drama") && !mediaType.equals("movie")) {
          continue;
        }

        Long contentId = getContentIdByTitle(title);
        Long locationId = getLocationIdByName(locationName);

        if (contentId != null && locationId != null) {
          insertContentLocation(contentId, locationId, sceneDescription);
        } else {
          log.warn("Skip row -> content: {} {}, location: {} {}, sceneDescription: {}",
              title, contentId, locationName, locationId, sceneDescription);
        }
      }

      log.info("✅ CSV import complete.");

    } catch (IOException | CsvValidationException e ) {
      throw new CsvParsingException("CSV File 처리 중 오류 발생!!!",e);
    }
  }

  private String cleanText(String text) {
    return text
        .replace("\uFEFF", "")
        .replace("\u200B", "")
        .replace("\u200C", "")
        .replace("\u200D", "")
        .replace("\u00A0", " ")
        .replaceAll("\\p{C}", "")
        .replaceAll("\\s+", " ")
        .trim();
  }

  /**
   * content 테이블 또는 title_alias 테이블에서 제목(alias)으로 content_id 조회
   */
  private Long getContentIdByTitle(String title) {
    String sql = """
        SELECT c.content_id
        FROM contents c
        LEFT JOIN title_alias ta ON c.content_id = ta.content_id
        WHERE c.title = ? OR ta.alias = ?
        LIMIT 1
        """;

    try {
      return jdbcTemplate.queryForObject(sql, Long.class, title, title);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * location 테이블에서 장소명으로 id 조회
   */
  private Long getLocationIdByName(String name) {
    String sql = "SELECT location_id FROM locations WHERE name = ?";
    try {
      return jdbcTemplate.queryForObject(sql, Long.class, name);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * content_location 테이블에 데이터 삽입 (중복 방지)
   */
  private void insertContentLocation(Long contentId, Long locationId, String description) {
    // 중복 체크
    String checkSql = "SELECT COUNT(*) FROM content_location WHERE content_id = ? AND location_id = ?";
    Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, contentId, locationId);

    if (count != null && count > 0) {
      //log.info("⚠️ 이미 존재하는 조합입니다. content_id={}, location_id={}", contentId, locationId);
      return;
    }

    String sql = "INSERT INTO content_location (content_id, location_id, scene_description) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, contentId, locationId, description);
    log.info("Insert OK contentId : {}, locationId : {}", contentId, locationId);
  }
}
