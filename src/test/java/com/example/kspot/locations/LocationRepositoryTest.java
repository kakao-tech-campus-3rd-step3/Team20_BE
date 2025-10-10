package com.example.kspot.locations;

import com.example.kspot.locations.entity.Location;
import com.example.kspot.locations.repository.LocationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @PersistenceContext
    EntityManager em;

    //테이블 수정 시 updatedAt 갱신 테스트
    @Test
    void updatedAt_shouldChangeWhenDataIsModified() throws InterruptedException {
        // given
        Location loc = new Location();
        loc.setName("테스트 장소");
        loc.setAddress("서울 어딘가");
        loc.setLatitude(37.1);
        loc.setLongitude(127.1);
        locationRepository.saveAndFlush(loc);

        LocalDateTime before = loc.getUpdatedAt();

        // when:
        Thread.sleep(1000);
        loc.setName("테스트 장소-수정");
        em.flush();
        em.refresh(loc);

        // then
        assertThat(loc.getUpdatedAt()).isAfter(before);
    }
}