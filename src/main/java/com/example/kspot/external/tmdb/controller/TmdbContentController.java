package com.example.kspot.external.tmdb.controller;

import com.example.kspot.external.tmdb.service.TmdbContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TmdbContentController {

  private final TmdbContentService loader;

  public TmdbContentController(TmdbContentService loader) {
    this.loader = loader;
  }

  @GetMapping("/api/tmdb/content/load")
  public String loadTmdbData() throws Exception {
    loader.loadContentsFromFile();
    return "✅ TMDB에서 Content 데이터 로딩 완료!";
  }
}
