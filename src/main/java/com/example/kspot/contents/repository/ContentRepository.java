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

  // Full Text Search (title, alias 둘 다 검색)
  @Query(value = """
      SELECT DISTINCT c.*
      FROM contents c
      LEFT JOIN title_alias a ON a.content_id = c.content_id
      WHERE MATCH(c.title) AGAINST(:keyword IN BOOLEAN MODE)
         OR MATCH(a.alias) AGAINST(:keyword IN BOOLEAN MODE)
      """,
      countQuery = """
        SELECT COUNT(DISTINCT c.content_id)
        FROM contents c
        LEFT JOIN title_alias a ON a.content_id = c.content_id
        WHERE MATCH(c.title) AGAINST(:keyword IN BOOLEAN MODE)
           OR MATCH(a.alias) AGAINST(:keyword IN BOOLEAN MODE)
        """,
      nativeQuery = true)
  Page<Content> fullTextSearch(@Param("keyword") String keyword, Pageable pageable);



  // 카테고리별 조회
  Page<Content> findByCategory(String category, Pageable pageable);
}
