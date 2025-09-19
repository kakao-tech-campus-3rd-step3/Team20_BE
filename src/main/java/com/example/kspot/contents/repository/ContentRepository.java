package com.example.kspot.contents.repository;

import com.example.kspot.contents.entity.Content;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
  Optional<Content> findByTitle(String title);
}
