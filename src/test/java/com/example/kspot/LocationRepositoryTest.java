package com.example.kspot;

import com.example.kspot.entity.Location;
import com.example.kspot.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    //테이블 수정 시 updatedAt 갱신 테스트
    @Test
    void updatedAt_shouldChangeWhenDataIsModified() throws InterruptedException {
        // given
        Location loc = new Location();
        loc.setName("테스트 장소");
        loc.setAddress("서울 어딘가");
        loc.setLatitude(37.1);
        loc.setLongitude(127.1);
        loc.setDescription("old desc");
        locationRepository.save(loc);

        LocalDateTime before = loc.getUpdatedAt();

        // when
        Thread.sleep(1000);
        loc.setDescription("new desc");
        locationRepository.save(loc);

        // then
        Location after = locationRepository.findById(loc.getLocationId()).orElseThrow();
        assertThat(after.getUpdatedAt()).isAfter(before);
    }
}
