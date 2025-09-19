package com.example.kspot.contents.service;

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
  public Page<Content> getAllContents(int page, int size) {
    return contentRepository.findAll(PageRequest.of(page, size));
  }

  // id로 컨텐츠 조회
  public Optional<Map<String, Object>> getContentDetailWithArtists(Long id) {
    return contentRepository.findById(id)
        .map(c -> {
          Map<String, Object> item = new HashMap<>();
          item.put("contentId", c.getContent_id());
          item.put("category", c.getCategory());
          item.put("title", c.getTitle());
          item.put("posterImageUrl", c.getPoster_image_url());
          item.put("releaseDate", c.getRelease_date());

          List<Map<String, Object>> artists = c.getArtists().stream()
              .map(a -> {
                Map<String, Object> map = new HashMap<>();
                map.put("artistId", a.getArtistId());
                map.put("name", a.getName());
                return map;
              })
              .collect(Collectors.toList());
          item.put("artists", artists);
          return item;
        });
  }

  // title로 컨텐츠 목록 조회
  public Page<Content> searchContentByTitle(String keyword, int page, int size) {
    return contentRepository.findByTitleContainingIgnoreCase(keyword, PageRequest.of(page, size));
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
