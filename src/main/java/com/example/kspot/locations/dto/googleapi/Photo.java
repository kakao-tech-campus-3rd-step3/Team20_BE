package com.example.kspot.locations.dto.googleapi;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Photo {
    private String name;
    private Integer widthPx;
    private Integer heightPx;
}