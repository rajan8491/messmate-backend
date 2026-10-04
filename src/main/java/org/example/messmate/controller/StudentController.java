package org.example.messmate.controller;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.messmate.dto.AddRatingRequest;
import org.example.messmate.dto.ExtraPurchaseDto;
import org.example.messmate.dto.SuccessMessageDto;
import org.example.messmate.dto.extraAnalysisDto.ExtraAnalysisResponse;
import org.example.messmate.dto.menudto.ExtraItemDto;
import org.example.messmate.dto.menudto.MenuResponseDto;
import org.example.messmate.dto.studentdto.StudentResponseDto;
import org.example.messmate.entity.Student;
import org.example.messmate.exception.UserUnauthorizedException;
import org.example.messmate.service.MenuService;
import org.example.messmate.service.StudentService;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final MenuService menuService;

    public StudentController(StudentService studentService, MenuService menuService) {
        this.studentService = studentService;
        this.menuService = menuService;
    }


    @GetMapping("/profile")
    public ResponseEntity<StudentResponseDto> getStudent(@AuthenticationPrincipal Jwt jwt) {
        String username = getUsername(jwt);

        StudentResponseDto studentResponseDto =
                studentService.getStudentProfile(username);

        return new ResponseEntity<>(studentResponseDto, HttpStatus.OK);
    }

    @GetMapping("/menu/today")
    public ResponseEntity<MenuResponseDto> getTodayMenu(@AuthenticationPrincipal Jwt jwt) {
        String username = getUsername(jwt);

        StudentResponseDto studentResponseDto = studentService.getStudentProfile(username);

        MenuResponseDto menuResponseDto =
                menuService.getTodayMenu(studentResponseDto.getHostelId());

        return ResponseEntity.ok(menuResponseDto);
    }

    @GetMapping("/menu/day/{day}")
    public ResponseEntity<MenuResponseDto> getMenuByDay(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String day
    ) {
        String username = getUsername(jwt);

        StudentResponseDto studentResponseDto = studentService.getStudentProfile(username);

        MenuResponseDto menuResponseDto =
                menuService.getDayMenu(studentResponseDto.getHostelId(), day);

        return ResponseEntity.ok(menuResponseDto);
    }

    @GetMapping("/extras")
    public ResponseEntity<List<ExtraItemDto>> getExtraItem(
            @RequestParam LocalDate date,
            @RequestParam String meal,
            @AuthenticationPrincipal Jwt jwt
    ) throws BadRequestException {
        if(date == null) {
            throw new BadRequestException("date is null");
        }
        if(meal == null) {
            throw new BadRequestException("meal is null");
        }

        String username = getUsername(jwt);

        StudentResponseDto studentResponseDto = studentService.getStudentProfile(username);

        List<ExtraItemDto> extraItemDto =
                menuService.getExtraItemByDate(date, meal, studentResponseDto.getHostelId());

        return ResponseEntity.ok(extraItemDto);
    }

    private static @Nullable String getUsername(Jwt jwt) {
        if(jwt == null) {
            throw new UserUnauthorizedException();
        }
        return jwt.getSubject();
    }

    @PostMapping("/purchase-extra")
    public ResponseEntity<SuccessMessageDto> purchaseExtraItem(
             @Valid @RequestBody ExtraPurchaseDto extraPurchaseDto,
            @AuthenticationPrincipal Jwt jwt
    ) throws BadRequestException {
        if(extraPurchaseDto == null) {
            throw new BadRequestException("Some field is missing");
        }

        String username = getUsername(jwt);
        studentService.addStudentExtra(extraPurchaseDto, username);

        SuccessMessageDto dto = new SuccessMessageDto("Extra item is added successfully");

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/analyse-extra")
    public ResponseEntity<ExtraAnalysisResponse> analyseExtraItem(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam String groupBy,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String username = getUsername(jwt);

        ExtraAnalysisResponse extraAnalysisResponse =
                studentService.analyseExtraPurchase(
                        username,
                        from,
                        to,
                        groupBy
                );

        return ResponseEntity.ok(extraAnalysisResponse);
    }

    @PostMapping("/ratings")
    public ResponseEntity<Void> addRating(
            @Valid @RequestBody AddRatingRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) throws BadRequestException {
        String username = getUsername(jwt);

        studentService.addRating(
                username,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/change-hostel")
    public ResponseEntity<String> changeHostel(
            @RequestBody Long hostelId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = getUsername(jwt);

        studentService.changeHostel(username, hostelId);

        return ResponseEntity.ok().body("Hostel has been changed successfully");
    }

}
