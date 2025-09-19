package com.example.kspot;

import com.example.kspot.artists.controller.ArtistsController;
import com.example.kspot.artists.dto.ArtistsResponseDto;
import com.example.kspot.artists.service.ArtistsService;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ArtistsController.class)
class ArtistsControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean
    ArtistsService artistsService;

    @Test
    void 전체_아티스트_조회() throws Exception {

        // given
        var items = List.of(
            new ArtistsResponseDto(1L, "이지금", "https://image.url/profile.jpg"),
            new ArtistsResponseDto(2L, "홍길동", "https://image.url/profile1.jpg")
        );
        given(artistsService.getArtists()).willReturn(items);

        String expected = """
            {
              "status": 200,
              "message": "아티스트 목록 조회 성공",
              "data": { "items": [
                { "artistId": 1, "name": "이지금", "profileImageUrl": "https://image.url/profile.jpg" },
                { "artistId": 2, "name": "홍길동", "profileImageUrl": "https://image.url/profile1.jpg" }
              ]}
            }
            """;

        // when
        var mvc = mockMvc.perform(get("/api/artists")
                        .accept(MediaType.APPLICATION_JSON)).andReturn();

        // then
        String actual = mvc.getResponse().getContentAsString();
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void 특정_아티스트_조회() throws Exception {

        // given
        var item = new ArtistsResponseDto(1L, "이지금", "https://image.url/profile.jpg");
        given(artistsService.getArtistById(1L)).willReturn(item);

        String expected = """
            {
              "status": 200,
              "message": "아티스트 조회 성공",
              "data": {
                "artistId": 1,
                "name": "이지금",
                "profileImageUrl": "https://image.url/profile.jpg"
              }
            }
            """;

        // when
        var mvc = mockMvc.perform(get("/api/artists/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)).andReturn();

        // then
        String actual = mvc.getResponse().getContentAsString();
        JSONAssert.assertEquals(expected, actual, true);
    }

}
