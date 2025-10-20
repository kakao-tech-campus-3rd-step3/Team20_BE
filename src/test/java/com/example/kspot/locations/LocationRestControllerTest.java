package com.example.kspot.locations;

import com.example.kspot.locations.entity.Location;
import com.example.kspot.locations.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LocationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository locationRepository;

    private Location saved;

    @BeforeEach
    void setUp() {
        //given
        Location loc = new Location();
        loc.setName("더현대 서울");
        loc.setAddress("서울 영등포구 여의대로 108");
        loc.setLatitude(37.5258);
        loc.setLongitude(126.9285);

        saved = locationRepository.save(loc);
    }

    @Test
    void 장소조회_성공() throws Exception {
        // When: 해당 id로 GET 요청
        mockMvc.perform(get("/locations/{id}", saved.getLocationId())
                        .accept(MediaType.APPLICATION_JSON))

                // Then: 200 응답 + 필드 값 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("촬영지 조회 성공"))
                .andExpect(jsonPath("$.data.name").value("더현대 서울"))
                .andExpect(jsonPath("$.data.address").value("서울 영등포구 여의대로 108"))
                .andExpect(jsonPath("$.data.latitude").value(37.5258))
                .andExpect(jsonPath("$.data.longitude").value(126.9285));
    }

    @Test
    void 장소조회_존재하지않음_404() throws Exception {
        // When: 존재하지 않는 id로 GET 요청
        mockMvc.perform(get("/locations/{id}", 9999L)
                        .accept(MediaType.APPLICATION_JSON))

                // Then: 404 응답 + status 필드 확인
                .andExpect(status().isNotFound());
    }
}