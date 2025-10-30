package com.example.kspot.contents;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.repository.ContentRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class ContentsControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ContentRepository contentRepository;
  private Long existingContentId;

  //테이블에 데이터가 존재하는지 여부 + 데이터에 필수 칼럼(content_id 확인)
  @BeforeEach
  void setUp(){
    Optional<Content> existingContent = contentRepository.findAll().stream().findFirst();
    assertThat(existingContent).isPresent()
        .withFailMessage("Content 폴더 내 데이터가 하나도 없습니다");
    Content content = existingContent.get();
    existingContentId = content.getContent_id();
  }

  @Test
  @DisplayName("1. 모든 컨텐츠 조회 성공")
  void getAllContent_success() throws Exception {
    mockMvc.perform(get("/contents")
        .param("page", "0")
        .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("콘텐츠 목록 조회 성공"))
        .andExpect(jsonPath("$.data.items").isArray());
  }

  @Test
  @DisplayName("2. 특정 컨텐츠 id로 조회 성공-496243")
  void getContentById_success() throws Exception {
    mockMvc.perform(get("/contents/{id}", 496243))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.contentId").value("496243"))
        .andExpect(jsonPath("$.data.releaseDate").value("2019-05-30T00:00:00"))
        .andExpect(jsonPath("$.data.category").value("MOVIE"))
        .andExpect(jsonPath("$.data.title").value("기생충"))
        .andExpect(jsonPath("$.data.artists").isArray())
        .andExpect(jsonPath("$.message").value("콘텐츠 상세 조회 성공"))
        .andExpect(jsonPath("$.status").value(200));
  }

  @Test
  @DisplayName("3. 특정 컨텐츠 id로 조회 실패-9756")
  void getContentById_fail() throws Exception {
    mockMvc.perform(get("/contents/{id}", 9756))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("콘텐츠를 찾을 수 없습니다"))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("4. title로 관련 컨텐츠 조회 성공-방탄")
  void getContentBytitle_success() throws Exception {
    mockMvc.perform(get("/contents/search?title={title}", "방탄"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.message").value("연관 콘텐츠 조회 성공"))
        .andExpect(jsonPath("$.status").value(200));
  }

  @Test
  @DisplayName("5. id를 사용한 관련 location 조회 성공-333")
  void getContentByLocation_success() throws Exception {
    mockMvc.perform(get("/contents/{id}/related-location", 333))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.message").value("콘텐츠 관련 장소 조회 성공"))
        .andExpect(jsonPath("$.status").value(200));
  }

  @Test
  @DisplayName("6. id를 사용한 관련 location 조회 실패-2없는 경우")
  void getContentByLocation_fail() throws Exception {
    mockMvc.perform(get("/contents/{id}/related-location", 3))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("콘텐츠 관련 장소 조회 성공"))
        .andExpect(jsonPath("$.status").value(200));
  }

  @Test
  @DisplayName("7. title_alias를 사용한 content 조회 성공-케데헌")
  void getContentByAlias_success() throws Exception {
    mockMvc.perform(get("/contents/search?title={}", "케데헌"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.message").value("연관 콘텐츠 조회 성공"))
        .andExpect(jsonPath("$.status").value(200));
  }
}
