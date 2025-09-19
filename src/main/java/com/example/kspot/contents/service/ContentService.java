package com.example.kspot.contents.service;

import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.repository.ContentRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContentService {
  private final ContentRepository contentRepository;

  @Autowired
  public ContentService(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
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
              .map(a -> Map.of(
                  "artistId", a.getArtistId(),
                  "name", a.getName()
              )).collect(Collectors.toList());
          item.put("artists", artists);
          return item;
        });
  }

  // title로 컨텐츠 목록 조회
  public Page<Content> searchContentByTitle(String keyword, int page, int size) {
    return contentRepository.findByTitleContainingIgnoreCase(keyword, PageRequest.of(page, size));
  }
}
