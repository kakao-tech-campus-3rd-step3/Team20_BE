package com.example.kspot.scheduler;

import com.example.kspot.service.LocationApiService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LocationScheduler {
    private final LocationApiService service;

    public LocationScheduler(LocationApiService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void updateLocations() throws Exception {
        runJob();
    }

    public void runJob() throws Exception {
        service.fetchAndSaveLocations();
    }
}