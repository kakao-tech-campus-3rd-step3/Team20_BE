package com.example.kspot.contents.service;

import com.example.kspot.contents.dto.ContentDetailResponse;
import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.repository.ContentLocationRepository;
import com.example.kspot.contents.repository.ContentRepository;
import jakarta.websocket.OnClose;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.text.html.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContentService {
  private final ContentRepository contentRepository;
  private final ContentLocationRepository contentLocationRepository;

  @Autowired
  public ContentService(ContentRepository contentRepository,  ContentLocationRepository contentLocationRepository) {
    this.contentRepository = contentRepository;
    this.contentLocationRepository = contentLocationRepository;
  }

  // 전체 컨텐츠 목록 조회
  public Page<Content> getAllContents(Pageable pageable) {
    return contentRepository.findAll(pageable);
  }

  // id로 컨텐츠 조회
  public Optional<ContentDetailResponse> getContentDetailWithArtists(Long id) {
    return contentRepository.findById(id)
        .map(ContentDetailResponse::fromEntity);
  }

  // title로 컨텐츠 목록 조회
  public Page<Content> searchContentByTitle(String keyword, Pageable pageable) {
    return contentRepository.findByTitleContainingIgnoreCase(keyword, pageable);
  }

  public List<Map<String, Object>> getRelatedLocations(Long contentId) {
    return contentLocationRepository.findByIdContentId(contentId)
        .stream()
        .map(cl -> {
          Map<String, Object> map = new HashMap<>();
          map.put("contentId", cl.getContent().getContent_id());
          map.put("locationId", cl.getLocation().getLocationId());
          map.put("sceneDescription", cl.getSceneDescription());
          return map;
        })
        .collect(Collectors.toList());
  }
}
