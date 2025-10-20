package com.example.kspot.locationReview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.kspot.locationReview.entity.LocationReview;
import com.example.kspot.locationReview.repository.LocationReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class LocationReviewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private LocationReviewRepository locationReviewRepository;

  private Long existingReviewId;
  private Long existingLocationId;
  private String jwtToken = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwidHlwIjoibWFzdGVyIn0.3PZMoQI6OU7L-WBSB7S-tx2ZYJ_Jt37wN6PY3q64uOLbrO18Y_qHPir1dXkWvjE0";


  // ✅ BeforeEach: DB 확인 + 로그인
  @BeforeEach
  void checkTableAndLogin() throws Exception {
    // 1️⃣ LocationReview 테이블에 데이터가 존재하는지 확인
    Optional<LocationReview> existingReviewOpt = locationReviewRepository.findAll().stream()
        .findFirst();
    assertThat(existingReviewOpt)
        .isPresent()
        .withFailMessage("LocationReview 테이블에 데이터가 존재하지 않습니다. 최소 1개는 필요합니다.");

    LocationReview existingReview = existingReviewOpt.get();
    existingReviewId = existingReview.getReviewId();
    existingLocationId = existingReview.getLocationId();
  }

  // ✅ 1. 특정 리뷰 조회 (성공)
  @Test
  @DisplayName("1. 특정 리뷰 조회 (성공)")
  void testGetReviewSuccess() throws Exception {
    mockMvc.perform(get("/location_review/{reviewId}", existingReviewId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.reviewId").value(existingReviewId))
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("리뷰 상세 조회 성공"));
  }

  // ✅ 2. 특정 리뷰 조회 (실패)
  @Test
  @DisplayName("2. 특정 리뷰 조회 (실패)")
  void testGetReviewFail_NotFound() throws Exception {
    long reviewId = -872;
    mockMvc.perform(get("/location_review/{reviewId}", reviewId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("해당 Location 리뷰를 찾을 수 없습니다. reviewId = " + reviewId));
  }

  // ✅ 3. 장소 리뷰목록 조회 (성공)
  @Test
  @DisplayName("3. 장소 리뷰목록 조회 (성공)")
  void testGetLocationReviewListSuccess() throws Exception {
    mockMvc.perform(get("/location_review/location/{locationId}", existingLocationId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("리뷰 목록 조회 성공"))
        .andExpect(jsonPath("$.data.locationReviews").isArray());
  }

  // ✅ 4. 장소 리뷰목록 조회 (실패)
  @Test
  @DisplayName("4. 장소 리뷰목록 조회 (실패)")
  void testGetLocationReviewListFail() throws Exception {
    long locationId = -54;
    mockMvc.perform(get("/location_review/location/{locationId}", locationId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("해당 Location의 리뷰가 존재하지 않습니다 : "+locationId));
  }

  // ✅ 5. 새로운 리뷰 생성 (성공)
  @Test
  @DisplayName("5. 새로운 리뷰 생성 (성공)")
  void testCreateReviewSuccess() throws Exception {
    String createJson = """
        {
            "locationId": %d,
            "title": "테스트 리뷰 제목",
            "detail": "테스트 리뷰 내용입니다",
            "rating": 5
        }
        """.formatted(existingLocationId);

    mockMvc.perform(post("/location_review")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(201))
        .andExpect(jsonPath("$.message").value("새로운 리뷰가 생성되었습니다"))
        .andExpect(jsonPath("$.data.title").value("테스트 리뷰 제목"));
  }

  // ✅ 6. 새로운 리뷰 생성 (실패 - JWT 없음)
  @Test
  @DisplayName("6. 새로운 리뷰 생성 (실패 - JWT 없음)")
  void testCreateReviewFail_Unauthorized() throws Exception {
    String createJson = """
        {
            "locationId": %d,
            "title": "테스트용 리뷰 제목",
            "detail": "테스트용 내용",
            "rating": 3
        }
        """.formatted(4);

    mockMvc.perform(post("/location_review")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("일치하는 토큰이 존재하지 않습니다. tokenHashHex 값: Authorization header is invalid"));
  }

  // ✅ 7. 리뷰 수정 (성공)
  @Test
  @DisplayName("7. 리뷰 수정 (성공)")
  void testUpdateReviewSuccess() throws Exception {
    String updateJson = """
        {
            "locationId": %d,
            "title": "수정된 리뷰 제목",
            "detail": "수정된 내용",
            "rating": 4
        }
        """.formatted(existingLocationId);

    mockMvc.perform(put("/location_review/{reviewId}", 4)
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("리뷰가 업데이트 되었습니다"))
        .andExpect(jsonPath("$.data.title").value("수정된 리뷰 제목"));
  }

  // ✅ 8. 리뷰 수정 (실패 - JWT 없음)
  @Test
  @DisplayName("8. 리뷰 수정 (실패 - JWT 없음)")
  void testUpdateReviewFail_Unauthorized() throws Exception {
    String updateJson = """
        {
            "locationId": %d,
            "title": "수정 제목",
            "detail": "수정 내용",
            "rating": 5
        }
        """.formatted(existingLocationId);

    mockMvc.perform(put("/location_review/{reviewId}", existingReviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("일치하는 토큰이 존재하지 않습니다. tokenHashHex 값: Authorization header is invalid"));
  }

  // ✅ 9. 리뷰 삭제 (성공)
  @Test
  @DisplayName("9. 리뷰 삭제 (성공)")
  void testDeleteReviewSuccess() throws Exception {

    mockMvc.perform(delete("/location_review/{reviewId}", 4)
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("리뷰가 정상적으로 삭제되었습니다"));
  }

  // ✅ 10. 리뷰 삭제 (실패 - JWT 없음)
  @Test
  @DisplayName("10. 리뷰 삭제 (실패 - JWT 없음)")
  void testDeleteReviewFail_Unauthorized() throws Exception {
    mockMvc.perform(delete("/location_review/{reviewId}", 4))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("일치하는 토큰이 존재하지 않습니다. tokenHashHex 값: Authorization header is invalid"));
  }
}
