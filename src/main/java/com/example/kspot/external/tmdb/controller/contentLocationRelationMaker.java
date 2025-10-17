package com.example.kspot.external.tmdb.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.kspot.external.tmdb.service.contentLoctionService;
@RestController
public class contentLocationRelationMaker {
  private final contentLoctionService service;

  public contentLocationRelationMaker(contentLoctionService service) {
    this.service = service;
  }

  @PostMapping("/api/content/location/relationMake")
  public String relationMake() throws Exception {
    // resources 폴더 안 파일 접근
    ClassPathResource resource = new ClassPathResource("한국문화정보원_미디어콘텐츠_UTF8.csv");

    BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), Charset.forName("UTF-8")));

    service.importFromCsv(br); // importFromCsv 메서드를 BufferedReader 버전으로 오버로딩
    return "content_location간 관계 테이블 생성완료!";
  }
}
