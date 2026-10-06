package org.example.messmate.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.example.messmate.dto.auth.*;
import org.example.messmate.dto.otpDto.OtpSendRequestDto;
import org.example.messmate.dto.otpDto.OtpVerifyRequestDto;
import org.example.messmate.entity.Hostel;
import org.example.messmate.entity.Student;
import org.example.messmate.entity.User;
import org.example.messmate.enums.OtpPurpose;
import org.example.messmate.enums.Role;
import org.example.messmate.exception.BadRequestException;
import org.example.messmate.exception.DuplicateResourceException;
import org.example.messmate.exception.ResourceNotFoundException;
import org.example.messmate.exception.UserNotFoundException;
import org.example.messmate.notification.domain.NotificationChannel;
import org.example.messmate.repository.HostelRepository;
import org.example.messmate.repository.StudentRepository;
import org.example.messmate.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HostelRepository hostelRepository;
    private final StudentRepository studentRepository;
    private final GoogleAuthService googleAuthService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            OtpService otpService,
            CustomUserDetailsService customUserDetailsService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            HostelRepository hostelRepository,
            StudentRepository studentRepository,
            GoogleAuthService googleAuthService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hostelRepository = hostelRepository;
        this.studentRepository = studentRepository;
        this.googleAuthService = googleAuthService;
    }

    public Authentication authenticate(
            LoginRequestDto loginRequestDto
    ){
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                password
        );

        return authenticationManager.authenticate(authentication);
    }

    public String authenticateAndGetToken(
            LoginRequestDto loginRequestDto
    ){
        Authentication authentication = authenticate(loginRequestDto);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        assert userDetails != null;
        return jwtService.generateAccessToken(userDetails);
    }

    /**
     * Verifies Google token:
     * - If user exists -> logs in directly.
     * - If user doesn't exist -> returns isNewUser: true (prompts for Name & Hostel).
     */
    @Transactional(readOnly = true)
    public GoogleVerifyResponseDto verifyGoogleToken(String idTokenString) {
        GoogleIdToken.Payload payload = googleAuthService.verifyToken(idTokenString);
        String email = payload.getEmail();
        // Optional: enforce college domain (@nitkkr.ac.in)
        if (email == null || !email.endsWith("@nitkkr.ac.in")) {
            throw new BadRequestException("Please provide college email");
        }
        String name = (String) payload.get("name");

        Optional<User> existingUser = userRepository.findByUsernameAndVerifiedIsTrue(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
            String accessToken = jwtService.generateAccessToken(userDetails);

            return GoogleVerifyResponseDto.builder()
                    .isNewUser(false)
                    .email(user.getUsername())
                    .name(name)
                    .role(user.getRole().name().toLowerCase())
                    .username(user.getUsername())
                    .accessToken(accessToken)
                    .build();
        }

        return GoogleVerifyResponseDto.builder()
                .isNewUser(true)
                .email(email)
                .name(name != null ? name : "")
                .build();
    }

    /**
     * Completes registration for new Google user by saving Name and Hostel.
     */
    @Transactional
    public AuthResponseDto completeGoogleRegistration(CompleteGoogleProfileDto dto) {
        GoogleIdToken.Payload payload = googleAuthService.verifyToken(dto.getToken());
        String email = payload.getEmail();

        if (userRepository.existsByUsername(email)) {
            throw new DuplicateResourceException("Account already registered. Please log in.");
        }

        // 1. Create User entity with verified email and dummy secure password
        User user = new User();
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRole(Role.STUDENT);
        user.setVerified(true); // Pre-verified via Google OAuth
        User savedUser = userRepository.save(user);

        // 2. Create Student profile entity linked to user and selected hostel
        Student student = new Student();
        student.setUser(savedUser);
        student.setName(dto.getName());
        student.setRollNumber(dto.getRollNo());
        Hostel hostel =
                hostelRepository
                        .findById(dto.getHostelId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Hostel")
                        );

        student.setHostel(hostel);

        studentRepository.save(student);

        // 3. Issue JWT Access Token
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        String accessToken = jwtService.generateAccessToken(userDetails);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name().toLowerCase())
                .message("Profile registered successfully")
                .build();
    }

    /**
     * Strict Login with Google: Throws ResourceNotFoundException if user doesn't exist.
     */
    @Transactional(readOnly = true)
    public AuthResponseDto loginWithGoogleStrict(String idTokenString) {
        GoogleIdToken.Payload payload = googleAuthService.verifyToken(idTokenString);
        String email = payload.getEmail();

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account does not exist. Please sign up first."));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        String accessToken = jwtService.generateAccessToken(userDetails);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .username(user.getUsername())
                .role(user.getRole().name().toLowerCase())
                .message("Login successful")
                .build();
    }

    public void sendLoginOtp(
            OtpSendRequestDto otpSendRequestDto
    ){
        otpService.requestOtp(
                otpSendRequestDto.getIdentifier(),
                OtpPurpose.LOGIN,
                otpSendRequestDto.getChannel()
        );
    }

    public AuthResponseDto loginWithOtp(
            OtpVerifyRequestDto otpVerifyRequestDto
    ){

        otpService.verifyOtp(
                otpVerifyRequestDto.getIdentifier(),
                OtpPurpose.LOGIN,
                otpVerifyRequestDto.getOtp()
        );

        // we need UserDetails to generate token

        String username = otpVerifyRequestDto
                .getIdentifier()
                .trim()
                .toLowerCase(Locale.ROOT);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        String accessToken = jwtService.generateAccessToken(userDetails);

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(UserNotFoundException::new);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .username(username)
                .role(user.getRole().name().toLowerCase(Locale.ROOT))
                .message("Login successful")
                .build();

    }

    @Transactional
    public SignupResponseDto signup(
            StandardSignupRequestDto dto
    ) {
        String username =
                dto
                        .getEmail()
                        .trim()
                        .toLowerCase();


        User registeredUser =
                userRepository
                        .findByUsername(username)
                        .orElse(null);


        if(registeredUser != null){
            boolean verified = registeredUser.getVerified();
            if(verified){
                throw new DuplicateResourceException("User already registered. Please log in.");
            }
            else{
                otpService.requestOtp(
                        username,
                        OtpPurpose.SIGNUP,
                        NotificationChannel.EMAIL
                );

                SignupResponseDto signupResponseDto =
                        new SignupResponseDto();

                signupResponseDto.setIdentifier(username);
                signupResponseDto.setMessage(
                        "OTP sent successfully"
                );

                return signupResponseDto;
            }
        }

        User user = new User();

        user.setUsername(username);

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);

        user.setRole(Role.STUDENT);

        userRepository.save(user);

        // now create corresponding student

        Student student = new Student();

        student.setUser(user);

        student.setName(dto.getName());

        String rollNo = (dto.getRollNo() != null && !dto.getRollNo().isBlank())
                ? dto.getRollNo()
                : dto.getEmail().split("@")[0];

        student.setRollNumber(rollNo);

        Hostel hostel =
                hostelRepository
                        .findById(dto.getHostelId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Hostel")
                        );

        student.setHostel(hostel);

        studentRepository.save(student);

        otpService.requestOtp(
                username,
                OtpPurpose.SIGNUP,
                NotificationChannel.EMAIL
        );

        SignupResponseDto signupResponseDto =
                new SignupResponseDto();

        signupResponseDto.setIdentifier(username);
        signupResponseDto.setMessage(
                "OTP sent successfully"
        );
        return signupResponseDto;
    }

    @Transactional
    public AuthResponseDto verifySignup(
            SignupVerifyRequestDto signupVerifyRequestDto
    ){

        String identifier =
                signupVerifyRequestDto
                        .getIdentifier()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        String otp = signupVerifyRequestDto.getOtp();

        otpService.verifyOtp(
                identifier,
                OtpPurpose.SIGNUP,
                otp
        );

        User registeredUser = userRepository
                .findByUsername(identifier)
                .orElse(null);

        if(registeredUser == null){
            throw new UserNotFoundException("User not found");
        }
        registeredUser.setVerified(true);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(identifier);

        String accessToken = jwtService.generateAccessToken(userDetails);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .username(registeredUser.getUsername())
                .role(registeredUser.getRole().name().toLowerCase())
                .message("Login successful")
                .build();
    }

}
