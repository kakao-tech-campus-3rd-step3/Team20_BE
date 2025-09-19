package com.example.kspot.contents.controller;

import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.service.ContentService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contents")
public class ContentRestController {
  private final ContentService contentService;

  @Autowired
  public ContentRestController(ContentService contentService) {
    this.contentService = contentService;
  }

  // 1. 전체 컨텐츠 조회
  @GetMapping
  public ResponseEntity<Map<String, Object>> getAllContents(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Page<Content> contentPage = contentService.getAllContents(page, size);
    List<Map<String, Object>> items = contentPage.getContent().stream().map(c ->{
      Map<String, Object> item = new HashMap<>();
      item.put("contentId", c.getContent_id());
      String category = c.getCategory().equalsIgnoreCase("tv") ? "drama" : c.getCategory().toLowerCase();
      item.put("category", category);
      item.put("title", c.getTitle());
      item.put("posterImageUrl", c.getPoster_image_url());
      item.put("releaseDate", c.getRelease_date());
      return item;
    }).collect(Collectors.toList());

    Map<String, Object> pagination = new HashMap<>();
    pagination.put("currentPage", page);
    pagination.put("itemsPerPage", size);
    pagination.put("totalItems", contentPage.getTotalElements());
    pagination.put("totalPages", contentPage.getTotalPages());

    Map<String, Object> data = new HashMap<>();
    data.put("items", items);
    data.put("pagination", pagination);

    Map<String, Object> response = new HashMap<>();
    response.put("status", 200);
    response.put("message", "컨텐츠 목록 조회 성공");
    response.put("data", data);

    return ResponseEntity.ok(response);
  }
}
