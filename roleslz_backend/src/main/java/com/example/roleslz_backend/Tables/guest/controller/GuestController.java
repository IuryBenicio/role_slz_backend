package com.example.roleslz_backend.Tables.guest.controller;

import com.example.roleslz_backend.Tables.guest.DTO.GuestDTO;
import com.example.roleslz_backend.Tables.guest.service.GuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("guest")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping("new_guest")
    public ResponseEntity<?> newGuest(@RequestBody GuestDTO dto){
        return null;
    }
}
