package com.example.kspot.contents.service;

import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.repository.ContentRepository;
import java.util.Optional;
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
  public Optional<Content> getContentById(Long id) {
    return contentRepository.findById(id);
  }

  // title로 컨텐츠 목록 조회
  public Page<Content> searchContentByTitle(String keyword, int page, int size) {
    return contentRepository.findByTitleContainingIgnoreCase(keyword, PageRequest.of(page, size));
  }
}
