package com.example.kspot.locations.dto;

import java.util.List;

public record LocationResponse (
        Long locationId,
        String name,
        String address,
        Double latitude,
        Double longtitude,
        List<RelatedContent> relatedContents
){
    public record RelatedContent(
            Long contentId,
            String title,
            String category
    ){}
}
