package com.example.roleslz_backend.Utills;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class LocationData {
    private String latitude;
    private String longitude;

    public LocationData(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
