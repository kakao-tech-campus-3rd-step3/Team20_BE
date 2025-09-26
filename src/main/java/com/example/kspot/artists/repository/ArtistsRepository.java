package com.example.kspot.artists.repository;

import com.example.kspot.artists.entity.Artists;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistsRepository extends JpaRepository<Artists,Long> {}
