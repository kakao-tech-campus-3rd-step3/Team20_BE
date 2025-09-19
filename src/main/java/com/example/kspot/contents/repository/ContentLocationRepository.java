package com.example.kspot.contents.repository;

import com.example.kspot.contents.entity.ContentLocation;
import com.example.kspot.contents.entity.ContentLocationId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentLocationRepository extends
    JpaRepository<ContentLocation, ContentLocationId> {
  List<ContentLocation> findByIdContentId(Long contentId);
}
