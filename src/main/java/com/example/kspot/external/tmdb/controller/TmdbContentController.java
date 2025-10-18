package com.example.kspot.external.tmdb.controller;

import com.example.kspot.external.tmdb.service.TmdbContentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TmdbContentController {

  private final TmdbContentService loader;

  public TmdbContentController(TmdbContentService loader) {
    this.loader = loader;
  }

  @PostMapping("/api/tmdb/content/load")
  public String loadTmdbData() throws Exception {
    loader.loadContentsFromFile();
    return "✅ TMDB에서 Content관련 데이터(artist 연계 포함) 로딩 완료!";
  }
}
