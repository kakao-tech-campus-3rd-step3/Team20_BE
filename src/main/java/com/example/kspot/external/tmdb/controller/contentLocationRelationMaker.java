package com.example.kspot.external.tmdb.controller;

import com.example.kspot.external.tmdb.service.contentLoctionService;
import com.example.kspot.global.dto.ApiResponseDto;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class contentLocationRelationMaker {

  private final contentLoctionService service;

  public contentLocationRelationMaker(contentLoctionService service) {
    this.service = service;
  }

  @PostMapping("/api/content/location/relationMake")
  public ResponseEntity<ApiResponseDto<String>> relationMake() throws Exception {
    ClassPathResource resource = new ClassPathResource("한국문화정보원_미디어콘텐츠_UTF8.csv");

    BufferedReader br = new BufferedReader(
        new InputStreamReader(resource.getInputStream(), Charset.forName("UTF-8")));

    service.importFromCsv(br);
    ApiResponseDto<String> response =
        new ApiResponseDto<>(200, "content_location간 관계 테이블 생성완료!", null);
    return ResponseEntity.ok(response);
  }
}
