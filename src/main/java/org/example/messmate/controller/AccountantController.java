package org.example.messmate.controller;

import jakarta.validation.Valid;
import org.example.messmate.dto.AccountantDto;
import org.example.messmate.dto.menudto.ItemCatalog;
import org.example.messmate.dto.menudto.MenuResponseDto;
import org.example.messmate.dto.menudto.UpdateTodayMenuDto;
import org.example.messmate.dto.menudto.WeeklyMenuUpdateDto;
import org.example.messmate.exception.UserUnauthorizedException;
import org.example.messmate.service.AccountantService;
import org.example.messmate.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accountant")
public class AccountantController {
    private final MenuService menuService;
    private final AccountantService accountantService;

    public AccountantController(
            MenuService menuService,
            AccountantService accountantService
    ) {
        this.menuService = menuService;
        this.accountantService = accountantService;
    }

    @GetMapping("/profile")
    public ResponseEntity<AccountantDto> getAccountant(@AuthenticationPrincipal Jwt jwt) {
        if(jwt == null) {
            throw new UserUnauthorizedException();
        }

        String username = jwt.getSubject();

        AccountantDto accountantDto =
                accountantService.getAccountantProfile(username);

        return new ResponseEntity<>(accountantDto, HttpStatus.OK);
    }

    @GetMapping("/items")
    public ResponseEntity<ItemCatalog> getItems(){
        ItemCatalog items =
                menuService.getItemsCatalog();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/menu/today")
    public ResponseEntity<MenuResponseDto> getTodayMenu(
            @AuthenticationPrincipal Jwt jwt
    ){
        if(jwt == null) {
            throw new UserUnauthorizedException();
        }

        String username = jwt.getSubject();

        Long hostelId =
                accountantService.getHostelId(username);

        MenuResponseDto menuResponseDto =
                menuService.getTodayMenu(hostelId);

        return ResponseEntity.ok().body(menuResponseDto);
    }

    @PutMapping("/menu/today")
    public ResponseEntity<Void> updateTodayMenu(
            @Valid @RequestBody UpdateTodayMenuDto updateTodayMenuDto,
            @AuthenticationPrincipal Jwt jwt
    ){
        if(jwt == null) {
            throw new UserUnauthorizedException();
        }

        String username = jwt.getSubject();
        Long hostelId =
                accountantService.getHostelId(username);

        menuService.updateTodayMenu(hostelId, updateTodayMenuDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/menu/weekly")
    public ResponseEntity<List<MenuResponseDto>> getWeeklyMenu(
            @AuthenticationPrincipal Jwt jwt
    ){
        if(jwt == null) {
            throw new UserUnauthorizedException();
        }

        String username = jwt.getSubject();
        Long hostelId =
                accountantService.getHostelId(username);

        List<MenuResponseDto> menusDto =
                menuService.getWeeklyMenu(hostelId);

        return ResponseEntity.ok().body(menusDto);

    }

    @PostMapping("/menu/weekly")
    public ResponseEntity<Void> addWeeklyMenu(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody List<WeeklyMenuUpdateDto> weeklyMenuUpdateDto
    ){
        if(jwt == null) {
            throw new UserUnauthorizedException();
        }

        String username = jwt.getSubject();
        Long hostelId =
                accountantService.getHostelId(username);

        menuService.updateWeeklyMenu(
                weeklyMenuUpdateDto,
                hostelId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/reviews/analyse")
    public ResponseEntity<String> analyseFeedback(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam boolean fresh
    ){
        if(jwt == null) {
            throw new UserUnauthorizedException();
        }
        String username = jwt.getSubject();

        System.out.println("username = " + username + " -> fresh = " + fresh);

        // analyse feedback

        return ResponseEntity.ok().body("analysis generated");
    }
}
