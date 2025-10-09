package com.example.kspot.itineraries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.kspot.itineraries.entity.Itinerary;
import com.example.kspot.itineraries.repository.ItineraryRepository;
import com.example.kspot.locations.repository.LocationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class ItinerariesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ItineraryRepository itineraryRepository;
  private Long existingItineraryId;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private String jwtToken;

  //테이블에 데이터가 존재하는지 여부 + 데이터에 필수 칼럼(itierary_id 확인)
  @BeforeEach
  void checkTable() {
    Optional<Itinerary> existingItinerary = itineraryRepository.findAll().stream().findFirst();
    assertThat(existingItinerary).isPresent()
        .withFailMessage("Itinerary 테이블 내 데이터가 하나도 없습니다");
    Itinerary itinerary = existingItinerary.get();
    existingItineraryId = itinerary.getItineraryId();
  }

  @BeforeEach
  void login() throws Exception{
    String loginJson = """
        {
            "email" : "jaeho@naver.com",
            "nickname" : "재호",
            "password" : "123456789"
        }
        """;
    MvcResult result = mockMvc.perform(post("/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginJson))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    jwtToken = jsonNode.path("data").path("token").asText(); // 예: {"token":"eyJhbGciOiJI..."}
  }

  @Test
  @DisplayName("1. itineraryId로 여행계획 조회 성공")
  void getItineraryById_success() throws Exception {
    mockMvc.perform(get("/itineraries/{id}", existingItineraryId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.itineraryId").value(existingItineraryId))
        .andExpect(jsonPath("$.data.title").value("BTS 테마 여행"))
        .andExpect(jsonPath("$.data.description").value("방탄소년단의 자취를 느낄 수 있는 여행계획!"))
        .andExpect(jsonPath("$.data.locations").isArray())
        .andExpect(jsonPath("$.message").value("여행 계획 상세 조회 성공"))
        .andExpect(jsonPath("$.status").value(200));
  }

  @Test
  @DisplayName("2. itineraryId로 여행계획 조회 실패")
  void getItineraryById_fail() throws Exception {
    mockMvc.perform(get("/itineraries/{id}", 9752))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.data.locations").doesNotExist())
        .andExpect(jsonPath("$.message").value("존재하지 않는 여행계획 입니다"))
        .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  @DisplayName("3. 새로운 여행계획 추가 성공")
  void postItinerary_success() throws Exception {

    String requestJson = """
            {
              "title": "부산 영화 촬영지 투어",
              "description": "국제시장과 친구 촬영지를 둘러보는 계획입니다.",
              "locations": [
                {"locationId": 4, "visitOrder": 1},
                {"locationId": 36, "visitOrder": 2}
              ]
            }
            """;
    mockMvc.perform(post("/itineraries")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.title").value("부산 영화 촬영지 투어"))
        .andExpect(jsonPath("$.data.description").value("국제시장과 친구 촬영지를 둘러보는 계획입니다."))
        .andExpect(jsonPath("$.data.user").isNotEmpty())
        .andExpect(jsonPath("$.data.locations").isArray())
        .andExpect(jsonPath("$.message").value("새로운 여행 계획이 생성되었습니다"))
        .andExpect(jsonPath("$.status").value(201));
  }
}
