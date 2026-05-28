package com.example.roleslz_backend.Tables.guest.controller;

import com.example.roleslz_backend.Tables.guest.service.GuestService;
import org.springframework.web.bind.annotation.RestController;

@RestController("guest")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

//    @PostMapping("new_guest")
//    public ResponseEntity<?> newGuest(@RequestBody GuestDTO dto){
//        return null;
//    }
}
