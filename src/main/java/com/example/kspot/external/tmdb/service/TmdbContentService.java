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
import lombok.extern.slf4j.Slf4j;


@Slf4j
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
    String mediaType = item.optString("media_type", category); // movie or tv
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
    log.info("✅ 컨텐츠 삽입 완료: {} ({})", contentId, mediaType);
    insertContentArtists(contentId, mediaType);
  }

  private void insertContentArtists(Long contentId, String mediaType) throws IOException, JSONException {
    String creditsUrl = String.format(
        "https://api.themoviedb.org/3/%s/%d/credits?language=ko-KR",
        mediaType.equalsIgnoreCase("tv") ? "tv" : "movie", contentId
    );

    Request request = new Request.Builder()
        .url(creditsUrl)
        .get()
        .addHeader("accept", "application/json")
        .addHeader("Authorization", "Bearer " + tmdbApiToken)
        .build();

    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) {
      log.info("⚠️ 출연진 불러오기 실패: {} ({})", contentId, mediaType);
      return;
    }

    JSONObject json = new JSONObject(response.body().string());
    JSONArray castArray = json.optJSONArray("cast");
    if (castArray == null || castArray.length() == 0) {
      log.info("ℹ️ 출연진 없음: {} ({})", contentId, mediaType);
      return;
    }

    for (int i = 0; i < castArray.length(); i++) {
      JSONObject cast = castArray.getJSONObject(i);
      Long artistId = cast.optLong("id", -1);
      if (artistId == -1) continue;

      // ✅ artist_id가 존재하는 경우만 삽입
      int exists = jdbcTemplate.queryForObject(
          "SELECT COUNT(*) FROM artists WHERE artist_id = ?",
          Integer.class,
          artistId
      );
      if (exists == 0) continue;

      jdbcTemplate.update("""
              INSERT IGNORE INTO content_artist (content_id, artist_id)
              VALUES (?, ?)
              """, contentId, artistId);
    }

    log.info("✅ 출연진 삽입 완료: {} ({})", contentId, mediaType);
  }
}