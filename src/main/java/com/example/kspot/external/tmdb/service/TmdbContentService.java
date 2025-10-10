package com.example.kspot.external.tmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Component
public class TmdbContentService {

  private static final String TMDB_URL = "https://api.themoviedb.org/3/search/multi";
  private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w1280";
  private static final String BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1920";

  @Value("${tmdb.api-token}")
  private String tmdbApiToken;

  private final JdbcTemplate jdbcTemplate;
  private final OkHttpClient client = new OkHttpClient();
  private final ObjectMapper mapper = new ObjectMapper();

  public TmdbContentService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void loadContentsFromFile() throws IOException, JSONException {
    // JSON 파일 로드
    InputStream inputStream = new ClassPathResource("tmdb_titles.json").getInputStream();
    JsonNode root = mapper.readTree(inputStream);

    Iterator<String> categories = root.fieldNames();
    while (categories.hasNext()) {
      String category = categories.next();
      JsonNode titles = root.get(category);

      for (JsonNode titleNode : titles) {
        String title = titleNode.asText();
        insertContentFromTmdb(title, category);
      }
    }
  }

  private void insertContentFromTmdb(String query, String category)
      throws IOException, JSONException {
    HttpUrl url = HttpUrl.parse(TMDB_URL).newBuilder()
        .addQueryParameter("query", query)
        .addQueryParameter("include_adult", "false")
        .addQueryParameter("language", "ko-KR")
        .addQueryParameter("page", "1")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .get()
        .addHeader("accept", "application/json")
        .addHeader("Authorization", "Bearer " + tmdbApiToken)
        .build();

    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) {
      return;
    }

    JSONObject json = new JSONObject(response.body().string());
    JSONArray results = json.optJSONArray("results");
    if (results == null || results.length() == 0) {
      return;
    }

    JSONObject item = results.getJSONObject(0);
    Long contentId = item.getLong("id");
    String title = item.optString("title", item.optString("name", "제목없음"));
    String posterPath = item.optString("poster_path", null);
    String backdropPath = item.optString("backdrop_path", null);
    String releaseDate = item.optString("release_date", item.optString("first_air_date", ""));
    double popularity = item.optDouble("popularity", 0.0);

    String posterUrl = posterPath != null ? POSTER_BASE_URL + posterPath : null;
    String backdropUrl = backdropPath != null ? BACKDROP_BASE_URL + backdropPath : null;

    jdbcTemplate.update("""
                INSERT INTO contents (content_id, category, title, poster_image_url, release_date, popularity, background_poster)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    title = VALUES(title),
                    poster_image_url = VALUES(poster_image_url),
                    release_date = VALUES(release_date),
                    popularity = VALUES(popularity),
                    background_poster = VALUES(background_poster)
                """,
        contentId, category, title, posterUrl, releaseDate, popularity, backdropUrl
    );
  }
}
