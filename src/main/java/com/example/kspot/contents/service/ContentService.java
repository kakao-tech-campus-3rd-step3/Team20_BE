package com.example.kspot.contents.service;

import com.example.kspot.contents.dto.ContentDetailResponse;
import com.example.kspot.contents.dto.ContentItemDto;
import com.example.kspot.contents.dto.ContentLocationResponse;
import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.entity.ContentLocation;
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
import org.springframework.data.domain.Sort;
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

  public List<ContentLocationResponse> getRelatedLocations(Long contentId) {
    return contentLocationRepository.findByIdContentId(contentId)
        .stream()
        .map(ContentLocationResponse::fromEntity)
        .toList();
  }

  //인기순 조회 (전체 or 카테고리별)
  public Page<ContentItemDto> getPopularContents(String category, int page, int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("Popularity").descending());

    Page<Content> contents;
    if(category == null || category.isBlank()){
      contents = contentRepository.findAll(pageable);
    }else {
      contents = contentRepository.findByCategory(category, pageable);
      if (contents.isEmpty()){
          throw new IllegalArgumentException("해당 카테고리의 콘텐츠가 없습니다.");
      }
    }

    if(contents.isEmpty()){
      throw new IllegalArgumentException("조회 가능한 콘텐츠가 없습니다.");
    }

    return contents.map(c->new ContentItemDto(
            c.getContent_id(),
            c.getCategory(),
            c.getTitle(),
            c.getPoster_image_url()
    ));
  }
}
