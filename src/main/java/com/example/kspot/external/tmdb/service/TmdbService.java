package com.example.kspot.external.tmdb.service;

import com.example.kspot.artists.entity.Artists;
import com.example.kspot.artists.repository.ArtistsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Service
public class TmdbService {

    private final ArtistsRepository artistsRepository;
    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TmdbService(ArtistsRepository artistsRepository) {
        this.artistsRepository = artistsRepository;
    }

    // KST 기준 17시 이전엔 전날 파일사용
    private LocalDate changeKstToUtcDate() {
        LocalDateTime now = LocalDateTime.now();
        return (now.getHour() < 17) ? now.toLocalDate().minusDays(1) : now.toLocalDate();
    }

    private static String PersonExportUrl(LocalDate d) {
        String mm = (d.getMonthValue() < 10 ? "0" : "") + d.getMonthValue();
        String dd = (d.getDayOfMonth() < 10 ? "0" : "") + d.getDayOfMonth();
        int yyyy = d.getYear();
        return "http://files.tmdb.org/p/exports/person_ids_" + mm + "_" + dd + "_" + yyyy + ".json.gz";
    }

    public String insert() {
        LocalDate date = changeKstToUtcDate();
        String url = PersonExportUrl(date);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        int received = 0, inserted = 0, skipped = 0;

        try (Response response = http.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return "다운로드 실패: " + response.code();
            }

            // 라인 단위로 파싱
            try (InputStream raw = response.body().byteStream();
                 GZIPInputStream gis = new GZIPInputStream(raw);
                 BufferedReader br = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8))) {

                List<Artists> buffer = new ArrayList<>(10000);
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.isBlank()) continue;

                    JsonNode n = objectMapper.readTree(line);
                    long tmdbId = n.path("id").asLong();
                    String name = n.path("name").asText(null);
                    if (tmdbId <= 0 || name == null || name.isBlank()) { skipped++; continue; }

                    received++;

                    Artists a = new Artists();
                    a.setArtistId(tmdbId);
                    a.setName(name);

                    buffer.add(a);

                    if (buffer.size() >= 10000) {
                        artistsRepository.saveAll(buffer);
                        inserted += buffer.size();
                        buffer.clear();
                    }
                }
                if (!buffer.isEmpty()) {
                    artistsRepository.saveAll(buffer);
                    inserted += buffer.size();
                }
            }

            return String.format("date=%s, received=%d, inserted=%d, skipped=%d", date, received, inserted, skipped);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

}
