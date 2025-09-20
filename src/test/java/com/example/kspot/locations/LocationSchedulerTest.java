package com.example.kspot.locations;

import com.example.kspot.locations.scheduler.LocationScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LocationSchedulerTest {

    @Autowired
    private LocationScheduler scheduler;

    @Test
    void runJob_shouldFetchAndSave() throws Exception {
        scheduler.runJob();
    }
}
