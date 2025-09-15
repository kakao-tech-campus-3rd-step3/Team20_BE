package com.example.kspot.external.tmdb.scheduler;

import com.example.kspot.external.tmdb.service.TmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@EnableScheduling
public class TmdbScheduler {

    private static final boolean ENABLED = true;
    private static final String  CRON    = "0 10 17 * * *";
    private static final String  ZONE    = "Asia/Seoul";

    private static final Logger log = LoggerFactory.getLogger(TmdbScheduler.class); // 중복 실행 방지용 락

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final TmdbService tmdbService;

    @Autowired
    public TmdbScheduler(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @Scheduled(cron = CRON, zone = ZONE)
    public void scheduledInsert() {
        if (!ENABLED) return;
        runOnce();
    }

    private void runOnce() {
        if (!running.compareAndSet(false, true)) {
            log.warn("스케줄러가 이미 실행되고 있습니다.");
            return;
        }
        try {
            String result = tmdbService.insert();
            log.info("TMDB {}", result);
        } catch (Exception e) {
            log.error("TMDB 스케줄러 실행 실패", e);
        } finally {
            running.set(false);
        }
    }
}
