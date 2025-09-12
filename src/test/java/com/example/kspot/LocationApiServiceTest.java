package com.example.kspot;

import com.example.kspot.entity.Location;
import com.example.kspot.repository.LocationRepository;
import com.example.kspot.service.LocationApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocationApiServiceTest {

    @Autowired
    private LocationApiService locationApiService;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void fetchAndSaveLocations_shouldInsertOrUpdateLocations() throws Exception {
        // given: 현재 DB에 저장된 개수
        long beforeCount = locationRepository.count();

        // when: API 호출하고 저장
        locationApiService.fetchAndSaveLocations();

        // then: DB에 데이터가 늘었거나, 업데이트 되었는지 확인
        long afterCount = locationRepository.count();
        assertThat(afterCount).isGreaterThanOrEqualTo(beforeCount);

        List<Location> all = locationRepository.findAll();
        assertThat(all.get(0).getName()).isNotBlank();
        assertThat(all.get(0).getAddress()).isNotBlank();
    }
}
