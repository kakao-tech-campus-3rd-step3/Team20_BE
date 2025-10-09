package com.example.kspot.contents;

import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ContentPopularControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContentRepository contentRepository;

    @BeforeEach
    void setUp(){
        List<Content> contents = Arrays.asList(
                createContent(1L, "DRAMA", "오징어 게임", "https://example.com/squidgame.jpg", 99.5),
                createContent(2L, "DRAMA", "이태원 클라스", "https://example.com/itaewon.jpg", 88.1),
                createContent(3L, "DRAMA", "더 글로리", "https://example.com/glory.jpg", 91.2),
                createContent(4L, "DRAMA", "도깨비", "https://example.com/goblin.jpg", 85.0),

                createContent(5L, "MOVIE", "신과 함께", "https://example.com/withgod.jpg", 94.5),
                createContent(6L, "MOVIE", "기생충", "https://example.com/parasite.jpg", 97.3),
                createContent(7L, "MOVIE", "범죄도시", "https://example.com/theoutlaws.jpg", 82.4)
        );
                contentRepository.saveAll(contents);
    }

    private Content createContent(Long id, String category, String title, String posterUrl, Double popularity) {
        Content c = new Content();
        c.setContent_id(id);
        c.setCategory(category);
        c.setTitle(title);
        c.setPoster_image_url(posterUrl);
        c.setPopularity(popularity);
        return c;
    }

    @Test
    @DisplayName("1.전체 인기 콘텐츠 TOP10 조회 성공")
    void getPopularContentsTop10_allCategory() throws Exception {
        mockMvc.perform(get("/contents/popular")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("인기 콘텐츠 조회 성공"))
                .andExpect(jsonPath("$.data.items[0].title").value("오징어 게임")) // popularity 99.5
                .andExpect(jsonPath("$.data.items[1].title").value("기생충"))// 97.3
                .andExpect(jsonPath("$.data.items[2].title").value("신과 함께"))//94.5
                .andExpect(jsonPath("$.data.items[3].title").value("더 글로리"));//91.2
    }

    @Test
    @DisplayName("2.카테고리별 인기 콘텐츠 조회 성공(drama)")
    void getPopularContents_drama() throws Exception {
        mockMvc.perform(get("/contents/popular")
                        .param("category", "DRAMA")
                        .param("page","0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("인기 콘텐츠 조회 성공"))
                .andExpect(jsonPath("$.data.items.length()").value(4))
                .andExpect(jsonPath("$.data.items[0].title").value("오징어 게임")) // popularity 99.5
                .andExpect(jsonPath("$.data.items[1].title").value("더 글로리"))//91.2
                .andExpect(jsonPath("$.data.items[2].title").value("이태원 클라스"))//88.1
                .andExpect(jsonPath("$.data.items[3].title").value("도깨비"));//85.0
    }

    @Test
    @DisplayName("3.존재하지 않는 카테고리 조회 시 예외 발생")
    void getPopularContents_invalidCategory() {
        Exception exception = assertThrows(Exception.class, () ->
                mockMvc.perform(get("/contents/popular")
                                .param("category", "KPOP")
                                .param("page", "0")
                                .param("size", "20")
                                .accept(MediaType.APPLICATION_JSON))
                        .andReturn()
        );
        assertTrue(exception.getMessage().contains("해당 카테고리의 콘텐츠가 없습니다."));
    }
}