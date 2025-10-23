package com.example.kspot.contents.service;

import com.example.kspot.contents.dto.ContentDetailResponse;
import com.example.kspot.contents.dto.ContentItemDto;
import com.example.kspot.contents.dto.ContentLocationResponse;
import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.repository.ContentLocationRepository;
import com.example.kspot.contents.repository.ContentRepository;
import java.util.List;
import java.util.Optional;
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
  public ContentService(ContentRepository contentRepository,
      ContentLocationRepository contentLocationRepository) {
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
    // 검색어 전처리: 기호/공백 제거 + 소문자화
    String normalizedKeyword = normalize(keyword);

    // 기본 포함 검색 시도
    Page<Content> result = contentRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    if (!result.isEmpty()) return result;

    // 연관 검색 (띄어쓰기, 콜론, 영어 대문자 등 무시 + Alias 테이블까지 조회)
    return contentRepository.findByTitleOrAlias(normalizedKeyword, pageable);
  }

  public List<ContentLocationResponse> getRelatedLocations(Long contentId) {
    return contentLocationRepository.findByIdContentId(contentId)
        .stream()
        .map(ContentLocationResponse::fromEntity)
        .toList();
  }

  //인기순 조회 (전체 or 카테고리별)

  public Page<ContentItemDto> getPopularContents(String category, int page, int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("popularity").descending());

    Page<Content> contents;
    if (category == null || category.isBlank()) {
      contents = contentRepository.findAll(pageable);
    } else {
      contents = contentRepository.findByCategory(category, pageable);
    }

    return contents.map(c -> new ContentItemDto(
        c.getContent_id(),
        c.getCategory(),
        c.getTitle(),
        c.getPoster_image_url()
    ));
  }

  private String normalize(String input) {
    if (input == null) return "";
    return input
        .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", "") // 문자, 숫자만 남김
        .toLowerCase(); // 대소문자 무시
  }
}
