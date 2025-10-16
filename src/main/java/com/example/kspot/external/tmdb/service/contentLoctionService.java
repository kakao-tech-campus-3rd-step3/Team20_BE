package com.example.kspot.external.tmdb.service;

import static java.util.Collections.replaceAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

  /**
   * BufferedReader를 바로 받아 CSV 데이터 처리
   */
  public void importFromCsv(BufferedReader br) {
    String line;

    try {
      // 헤더 라인 건너뛰기
      br.readLine();

      while ((line = br.readLine()) != null) {
        // ,로 분리, -1은 빈 값도 포함
        String[] cols = line.split(",", -1);

        // 컬럼 개수가 충분하지 않으면 skip
        if (cols.length < 5) {
          continue;
        }

        String mediaType = cols[1].trim().replaceAll("^\"|\"$", "").toLowerCase();
        String title = cols[2].replaceAll("^\"|\"$", "").trim();
        String locationName = cols[3]
            .replace("\uFEFF", "")              // UTF-8 BOM
            .replace("\u200B", "")              // Zero-width space
            .replace("\u200C", "")              // Zero-width non-joiner
            .replace("\u200D", "")              // Zero-width joiner
            .replace("\u00A0", " ")             // Non-breaking space → 일반 공백
            .replaceAll("\\p{C}", "")           // 모든 제어 문자 제거
            .replaceAll("^\"|\"$", "")          // 앞뒤 따옴표 제거
            .replaceAll("\\s+", " ")            // 연속 공백 → 단일 공백
            .trim();

        String sceneDescription = cols[5].replaceAll("^\"|\"$", "");
        // drama, movie 만 처리
        if (!mediaType.equals("drama") && !mediaType.equals("movie")) {
          continue;
        }

        Long contentId = getContentIdByTitle(title);
        Long locationId = getLocationIdByName(locationName);

        if (contentId != null && locationId != null) {
          insertContentLocation(contentId, locationId, sceneDescription);
        } else {
          log.warn("Skip row -> content: {} {}, location: {} {}, sceneDescription: {}",
              title,contentId, locationName,locationId, sceneDescription);
        }
      }

      log.info("✅ CSV import complete.");

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * content 테이블에서 제목으로 id 조회
   */
  private Long getContentIdByTitle(String title) {
    String sql = "SELECT content_id FROM contents WHERE title = ?";
    try {
      return jdbcTemplate.queryForObject(sql, Long.class, title);
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

  /** content_location 테이블에 데이터 삽입 (중복 방지) */
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
