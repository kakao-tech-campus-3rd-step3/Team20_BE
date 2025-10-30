package com.example.kspot.locations.dto.googleapi;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GooglePlacesSearchResponse {
    private List<Place> places;
}
