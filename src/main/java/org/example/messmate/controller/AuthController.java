package org.example.messmate.controller;

import jakarta.validation.Valid;
import org.example.messmate.dto.auth.*;
import org.example.messmate.dto.otpDto.OtpResendRequestDto;
import org.example.messmate.dto.otpDto.OtpSendRequestDto;
import org.example.messmate.dto.otpDto.OtpSendResponseDto;
import org.example.messmate.dto.otpDto.OtpVerifyRequestDto;
import org.example.messmate.exception.UserUnauthorizedException;
import org.example.messmate.properties.JwtCookieProperties;
import org.example.messmate.service.*;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtCookieProperties jwtCookieProperties;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtCookieProperties jwtCookieProperties, RefreshTokenService refreshTokenService, OtpService otpService, CustomUserDetailsService customUserDetailsService, JwtService jwtService) {
        this.authService = authService;
        this.jwtCookieProperties = jwtCookieProperties;
        this.refreshTokenService = refreshTokenService;
        this.otpService = otpService;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe(
            Authentication authentication
    ) {

        Jwt jwt = (Jwt) authentication.getPrincipal();

        if(jwt == null) {
            throw new UserUnauthorizedException();
        }

        String username = jwt.getSubject();

        UserResponseDto userResponseDto = customUserDetailsService.getByUsername(username);

        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/google/verify")
    public ResponseEntity<GoogleVerifyResponseDto> verifyGoogleToken(
            @Valid @RequestBody GoogleVerifyRequestDto requestDto
    ) {
        var googleVerifyResponseDto = authService.verifyGoogleToken(requestDto.getToken());

        if(googleVerifyResponseDto.isNewUser()){
            return ResponseEntity.ok(googleVerifyResponseDto);
        }

        ResponseCookie cookie =
                getRefreshTokenCookie(googleVerifyResponseDto.getUsername());
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(googleVerifyResponseDto);
    }

    @PostMapping("/google/complete-profile")
    public ResponseEntity<AuthResponseDto> completeGoogleProfile(
            @Valid @RequestBody CompleteGoogleProfileDto requestDto
    ) {
        AuthResponseDto authResponseDto = authService.completeGoogleRegistration(requestDto);

        ResponseCookie cookie = getRefreshTokenCookie(authResponseDto.getUsername());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(authResponseDto);
    }

    /**
     * 3. Google Login (Strict)
     * Throws 404 (ResourceNotFoundException) if user has not signed up.
     */
    @PostMapping("/google/login")
    public ResponseEntity<AuthResponseDto> googleLogin(
            @Valid @RequestBody GoogleVerifyRequestDto requestDto
    ) {
        AuthResponseDto authResponseDto = authService.loginWithGoogleStrict(requestDto.getToken());
        //add the refresh token to cookie

        ResponseCookie cookie = getRefreshTokenCookie(authResponseDto.getUsername());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(authResponseDto);
    }

    @NonNull
    private ResponseCookie getRefreshTokenCookie(String username) {
        RefreshTokenService.TokenPairHolder tokenPairHolder =
                refreshTokenService.createRefreshToken(username);

        return ResponseCookie
                .from(
                        jwtCookieProperties.name,
                        tokenPairHolder.rawToken()
                )
                .httpOnly(
                        jwtCookieProperties.httpOnly
                )
                .secure(
                        jwtCookieProperties.secure
                )
                .path(
                        jwtCookieProperties.path
                )
                .maxAge(
                        jwtCookieProperties.maxAge
                )
                .sameSite(
                        jwtCookieProperties.sameSite
                )
                .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @Valid @RequestBody StandardSignupRequestDto requestDto
    ) {

        SignupResponseDto response = authService.signup(requestDto);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signup/verify")
    public ResponseEntity<AuthResponseDto> verifySignup(
            @Valid @RequestBody SignupVerifyRequestDto signupVerifyRequestDto
    ){
        AuthResponseDto authResponseDto =
                authService.verifySignup(signupVerifyRequestDto);

        ResponseCookie cookie = getRefreshTokenCookie(authResponseDto.getUsername());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(authResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto
    ){

        String accessToken = authService.authenticateAndGetToken(loginRequestDto);

        UserResponseDto userResponseDto = customUserDetailsService.getByUsername(
                loginRequestDto.getUsername()
        );

        AuthResponseDto authResponseDto =
                AuthResponseDto.builder()
                        .accessToken(accessToken)
                        .username(userResponseDto.getUsername())
                        .role(userResponseDto.getRole().toString())
                        .message("User login successful")
                        .build();

        //add the refresh token to cookie

        ResponseCookie cookie = getRefreshTokenCookie(authResponseDto.getUsername());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(authResponseDto);
    }

    @PostMapping("/login/send-otp")
    public ResponseEntity<OtpSendResponseDto> requestOtp(
            @Valid @RequestBody OtpSendRequestDto otpSendRequestDto
    ){
        authService.sendLoginOtp(otpSendRequestDto);

        OtpSendResponseDto otpSendResponseDto = new OtpSendResponseDto();
        otpSendResponseDto.setIdentifier(otpSendRequestDto.getIdentifier());

        return ResponseEntity.status(HttpStatus.OK).body(otpSendResponseDto);
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<AuthResponseDto> verifyOtp(
            @Valid @RequestBody OtpVerifyRequestDto otpVerifyRequestDto
    ){
        AuthResponseDto authResponseDto = authService.loginWithOtp(otpVerifyRequestDto);

        ResponseCookie cookie = getRefreshTokenCookie(authResponseDto.getUsername());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(authResponseDto);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<OtpSendResponseDto> resendOtp(
            @Valid @RequestBody OtpResendRequestDto otpResendRequestDto
    ){

        otpService.requestOtp(
                otpResendRequestDto.getIdentifier(),
                otpResendRequestDto.getPurpose(),
                otpResendRequestDto.getChannel()
        );

        OtpSendResponseDto otpSendResponseDto = new OtpSendResponseDto();
        otpSendResponseDto.setIdentifier(otpResendRequestDto.getIdentifier());

        return ResponseEntity.ok(otpSendResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                    name = "jwtCookieProperties.name",
                    required = false
            ) String rawRefreshToken
    ){
        ResponseCookie cookie =
                ResponseCookie
                        .from(
                                jwtCookieProperties.name,
                                ""
                        )
                        .httpOnly(
                                jwtCookieProperties.httpOnly
                        )
                        .secure(
                                jwtCookieProperties.secure
                        )
                        .path(
                                jwtCookieProperties.path
                        )
                        .maxAge(
                                0
                        )
                        .sameSite(
                                jwtCookieProperties.sameSite
                        )
                        .build();

        return ResponseEntity.noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue(
                    name = "${jwt.cookie.name}",
                    required = false
            ) String rawRefreshToken
    ){
        if(rawRefreshToken == null || rawRefreshToken.isEmpty()){
            throw new UserUnauthorizedException();
        }

        RefreshTokenService.TokenPairHolder tokenPairHolder =
                refreshTokenService.rotateRefreshToken(rawRefreshToken);

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        tokenPairHolder.tokenEntity().getUser().getUsername()
                );

        String accessToken = jwtService.generateAccessToken(userDetails);

        AccessTokenResponse accessTokenResponse =
                new AccessTokenResponse();

        accessTokenResponse.setAccessToken(accessToken);
        accessTokenResponse.setTokenType("Bearer");
        accessTokenResponse.setExpiresIn(900);

        ResponseCookie cookie =
                ResponseCookie
                        .from(
                                jwtCookieProperties.name,
                                tokenPairHolder.rawToken()
                        )
                        .httpOnly(
                                jwtCookieProperties.httpOnly
                        )
                        .secure(
                                jwtCookieProperties.secure
                        )
                        .path(
                                jwtCookieProperties.path
                        )
                        .maxAge(
                                jwtCookieProperties.maxAge
                        )
                        .sameSite(
                                jwtCookieProperties.sameSite
                        )
                        .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(accessTokenResponse);
    }
}
