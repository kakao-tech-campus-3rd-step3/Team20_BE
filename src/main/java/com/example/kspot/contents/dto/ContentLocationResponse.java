package com.example.kspot.contents.dto;

import com.example.kspot.contents.entity.ContentLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContentLocationResponse {
  private Long contentId;
  private Long locationId;
  private String sceneDescription;

  public static ContentLocationResponse fromEntity(ContentLocation cl) {
    return new ContentLocationResponse(
        cl.getContent().getContent_id(),
        cl.getLocation().getLocationId(),
        cl.getSceneDescription()
    );
  }
}
