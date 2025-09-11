package com.example.kspot;

import com.example.kspot.service.LocationApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LocationApiServiceTest {

    @Autowired
    private LocationApiService locationApiService;

    @Test
    void testApiCall() throws Exception{
        locationApiService.callApi();
    }
}
