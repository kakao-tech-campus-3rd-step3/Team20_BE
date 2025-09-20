package com.example.kspot.contents.repository;

import com.example.kspot.contents.entity.Content;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

  // keyword가 포함된 모든 컨텐츠 간략 조회
  Page<Content> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
