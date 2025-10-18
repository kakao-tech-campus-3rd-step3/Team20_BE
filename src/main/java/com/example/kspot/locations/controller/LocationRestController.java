package com.example.kspot.locations.controller;

import com.example.kspot.locations.dto.LocationResponse;
import com.example.kspot.locations.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Locations", description = "촬영지 관련 API")
@RestController
@RequestMapping("/locations")
public class LocationRestController {

    private final LocationService locationService;

    public LocationRestController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(
            summary = "촬영지 상세 조회",
            description = "로케이션 ID를 이용해 특정 촬영지의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationDetail(@Parameter(description = "조회할 촬영지의 ID", example = "5")
                                                                  @PathVariable Long id) {
        LocationResponse response = locationService.getLocationDetail(id);
        return ResponseEntity.ok(response);
    }
}
