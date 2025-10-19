package com.example.kspot.contents.controller;

import com.example.kspot.global.dto.ApiResponseDto;
import com.example.kspot.contents.dto.ContentDetailResponse;
import com.example.kspot.contents.dto.ContentItemDto;
import com.example.kspot.contents.dto.ContentListResponse;
import com.example.kspot.contents.dto.ContentLocationResponse;
import com.example.kspot.contents.dto.PaginationDto;
import com.example.kspot.contents.entity.Content;
import com.example.kspot.contents.service.ContentService;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Contents", description = "콘텐츠 관련 API")
@RestController
@RequestMapping("/contents")
public class ContentRestController {
  private final ContentService contentService;

  @Autowired
  public ContentRestController(ContentService contentService) {
    this.contentService = contentService;
  }

  // 1. 전체 콘텐츠 조회
  @Operation(summary = "전체 콘텐츠 목록 조회", description = "모든 콘텐츠를 페이지네이션 형태로 조회합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping
  public ResponseEntity<ApiResponseDto<ContentListResponse>> getAllContents(Pageable pageable) {
    Page<Content> contentPage = contentService.getAllContents(pageable);

    // 전체 콘텐츠들 간략정보 넣기
    List<ContentItemDto> items = contentPage.getContent().stream()
        .map(c -> new ContentItemDto(
            c.getContent_id(),
            c.getCategory(),
            c.getTitle(),
            c.getPoster_image_url()
        )).collect(Collectors.toList());

    // pagination 정보 넣기
    PaginationDto pagination = new PaginationDto(
        contentPage.getNumber(),
        contentPage.getSize(),
        contentPage.getTotalElements(),
        contentPage.getTotalPages()
    );
    ContentListResponse data = new ContentListResponse(items, pagination);
    ApiResponseDto<ContentListResponse> response = new ApiResponseDto<>(200, "콘텐츠 목록 조회 성공", data);

    // 합쳐서 response
    return ResponseEntity.ok(response);
  }

  // 2. id로 특정 콘텐츠 조회
  @Operation(summary = "콘텐츠 상세 조회", description = "콘텐츠 ID를 이용해 상세 정보를 조회합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponseDto<ContentDetailResponse>> getContentById(@Parameter(description = "조회할 콘텐츠의 고유 ID", example = "1")
                                                                                @PathVariable Long id) {
    return contentService.getContentDetailWithArtists(id)
        .map(data -> {
          ApiResponseDto<ContentDetailResponse> reponse = new ApiResponseDto<>(200, "콘텐츠 상세 조회 성공", data);
          return ResponseEntity.ok(reponse);
        })
        .orElseGet(() -> {
          ApiResponseDto<ContentDetailResponse> response = new ApiResponseDto<>(404, "콘텐츠를 찾을 수 없습니다", null);
          return ResponseEntity.status(404).body(response);
        });
  }

  // 3. title로 연관 콘텐츠 간략조회
  @Operation(summary = "연관 콘텐츠 검색", description = "제목 키워드로 콘텐츠를 검색합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping("/search")
  public ResponseEntity<ApiResponseDto<ContentListResponse>> getAllContentsByTitle(@Parameter(description = "검색할 콘텐츠 제목 키워드", example = "오징어")
      @RequestParam String title,
      Pageable pageable
    ){
    Page<Content> contents = contentService.searchContentByTitle(title, pageable);

    List<ContentItemDto> items = contents.getContent().stream()
        .map(c-> new ContentItemDto(
            c.getContent_id(),
            c.getCategory(),
            c.getTitle(),
            c.getPoster_image_url()
        )).collect(Collectors.toList());

    PaginationDto pagination = new PaginationDto(
        contents.getNumber(),
        contents.getSize(),
        contents.getTotalElements(),
        contents.getTotalPages()
    );

    ContentListResponse data = new ContentListResponse(items, pagination);
    ApiResponseDto<ContentListResponse> response = new ApiResponseDto<>(200, "연관 콘텐츠 조회 성공", data);
    return ResponseEntity.ok(response);
  }

  // 4. content id로 연관 location 조회
  @Operation(summary = "콘텐츠 관련 장소 조회", description = "콘텐츠 ID로 관련 촬영 장소 목록을 조회합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping("{id}/related-location")
  public ResponseEntity<ApiResponseDto<List<ContentLocationResponse>>> getRelatedLocations( @Parameter(description = "콘텐츠의 고유 ID", example = "1")
                                                                                              @PathVariable("id") Long contentId) {
    List<ContentLocationResponse> locations = contentService.getRelatedLocations(contentId);

    ApiResponseDto<List<ContentLocationResponse>> response = new ApiResponseDto<>(
        200,
        "콘텐츠 관련 장소 조회 성공",
        locations.isEmpty() ? null : locations
    );

    return ResponseEntity.ok(response);
  }

  //5.인기 콘텐츠 조회(전체/카테고리)
  @Operation(summary = "인기 콘텐츠 조회", description = "카테고리별 혹은 전체 인기 콘텐츠를 조회합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping("/popular")
  public ResponseEntity<ApiResponseDto<ContentListResponse>> getPopularContents(
          @Parameter(description = "카테고리 필터 (예: drama, movie 등), 생략 시 전체 인기 순위", example = "drama")
          @RequestParam(required = false) String category,
          @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
          @RequestParam(defaultValue = "0") int page,
          @Parameter(description = "페이지당 콘텐츠 개수", example = "20")
          @RequestParam(defaultValue = "20") int size
  ) {
      Page<ContentItemDto> contents = contentService.getPopularContents(category, page, size);

      PaginationDto pagination = new PaginationDto(
              contents.getNumber(),
              contents.getSize(),
              contents.getTotalElements(),
              contents.getTotalPages()
      );

      ContentListResponse data = new ContentListResponse(contents.getContent(), pagination);
      ApiResponseDto<ContentListResponse> response = new ApiResponseDto<ContentListResponse>(200, "인기 콘텐츠 조회 성공", data);

      return ResponseEntity.ok(response);
  }
}
