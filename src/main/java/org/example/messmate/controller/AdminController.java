package org.example.messmate.controller;

import jakarta.validation.Valid;
import org.example.messmate.dto.hostelDto.HostelCreateDto;
import org.example.messmate.dto.hostelDto.HostelResponseDto;
import org.example.messmate.dto.hostelDto.HostelUpdateDto;
import org.example.messmate.dto.otpDto.OtpSendRequestDto;
import org.example.messmate.dto.otpDto.OtpSendResponseDto;
import org.example.messmate.dto.otpDto.OtpVerifyRequestDto;
import org.example.messmate.dto.studentdto.RemoveStudentVerifyRequestDto;
import org.example.messmate.dto.studentdto.StudentResponseDto;
import org.example.messmate.service.HostelService;
import org.example.messmate.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final HostelService hostelService;
    private final StudentService studentService;

    public AdminController(HostelService hostelService, StudentService studentService) {
        this.hostelService = hostelService;
        this.studentService = studentService;
    }

    @GetMapping("/hostels")
    public ResponseEntity<List<HostelResponseDto>> getAllHostels() {
        List<HostelResponseDto> hostels = hostelService.getHostels();
        return ResponseEntity.ok(hostels);
    }

    @PostMapping("/hostels")
    public ResponseEntity<Void> addHostel(
            @Valid @RequestBody HostelCreateDto hostelCreateDto
    ){

        hostelService.createHostel(hostelCreateDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/hostels/{id}")
    public ResponseEntity<Void> updateHostel(
            @Valid @RequestBody HostelUpdateDto hostelUpdateDto,
            @PathVariable Long id
    ){
        hostelService.updateHostel(id, hostelUpdateDto);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/hostels/remove/send-otp")
    public ResponseEntity<OtpSendResponseDto> removeHostelOtp(
            @RequestBody OtpSendRequestDto otpSendRequestDto
    ){
        OtpSendResponseDto response =
                hostelService.sendHostelRemoveOtp(otpSendRequestDto);

        return ResponseEntity
                .accepted()
                .body(response);
    }

    @DeleteMapping("/hostels/{id}/remove")
    public ResponseEntity<Void> removeHostel(
            @PathVariable Long id,
            @RequestBody OtpVerifyRequestDto otpVerifyRequestDto
    ){
        hostelService.removeHostel(
                id,
                otpVerifyRequestDto
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/hostels/{hostelId}/students")
    public ResponseEntity<List<StudentResponseDto>> getAllStudents(
            @PathVariable Long hostelId
    ) {
        List<StudentResponseDto> response =
                studentService.getAllStudentsByHostelId(hostelId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/students/remove/send-otp")
    public ResponseEntity<OtpSendResponseDto> removeStudentOtp(
            @RequestBody OtpSendRequestDto otpSendRequestDto
    ){
        OtpSendResponseDto response =
                studentService.sendStudentRemoveOtp(otpSendRequestDto);

        return ResponseEntity
                .accepted()
                .body(response);
    }

    @DeleteMapping("/hostels/{id}/students/remove")
    public ResponseEntity<Void> removeStudents(
            @PathVariable Long id,
            @RequestBody RemoveStudentVerifyRequestDto removeStudentVerifyRequestDto
    ){
        studentService.removeStudentsByHostelId(
                id,
                removeStudentVerifyRequestDto
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
