package org.example.messmate.controller;

import org.example.messmate.dto.hostelDto.HostelResponseDto;
import org.example.messmate.service.HostelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/hostels")
public class HostelController {
    private final HostelService hostelService;

    public HostelController(HostelService hostelService) {
        this.hostelService = hostelService;
    }

    @GetMapping("")
    public ResponseEntity<List<HostelResponseDto>> getHostels() {
        List<HostelResponseDto> hostels = hostelService.getHostels();

        return new ResponseEntity<>(hostels, HttpStatus.OK);
    }
}
