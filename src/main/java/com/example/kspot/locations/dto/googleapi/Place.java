package com.example.kspot.locations.dto.googleapi;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Place {

    private String id;
    private String name;
    private DisplayName displayName;
    private String formattedAddress;
    private List<Photo> photos;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DisplayName {
        private String text;
    }
}