package com.example.kspot.contents.repository;

import com.example.kspot.contents.entity.Content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, Long> {

  // keyword가 포함된 모든 컨텐츠 간략 조회
  Page<Content> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

  // 연관검색
  @Query("""
    SELECT DISTINCT c FROM Content c
    LEFT JOIN c.aliases a
    WHERE 
        LOWER(REPLACE(REPLACE(REPLACE(c.title, ' ', ''), ':', ''), '-', '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(REPLACE(REPLACE(REPLACE(a.alias, ' ', ''), ':', ''), '-', '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
  Page<Content> findByTitleOrAlias(@Param("keyword") String keyword, Pageable pageable);



  // 카테고리별 조회
  Page<Content> findByCategory(String category, Pageable pageable);
}
