package org.example.messmate.service;

import org.example.messmate.dto.auth.*;
import org.example.messmate.dto.otpDto.OtpSendRequestDto;
import org.example.messmate.dto.otpDto.OtpVerifyRequestDto;
import org.example.messmate.entity.Hostel;
import org.example.messmate.entity.Student;
import org.example.messmate.entity.User;
import org.example.messmate.enums.OtpPurpose;
import org.example.messmate.enums.Role;
import org.example.messmate.exception.EmailAlreadyExistsException;
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

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            OtpService otpService,
            CustomUserDetailsService customUserDetailsService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            HostelRepository hostelRepository,
            StudentRepository studentRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hostelRepository = hostelRepository;
        this.studentRepository = studentRepository;
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

    public void sendLoginOtp(
            OtpSendRequestDto otpSendRequestDto
    ){
        otpService.requestOtp(
                otpSendRequestDto.getIdentifier(),
                OtpPurpose.LOGIN,
                otpSendRequestDto.getChannel()
        );
    }

    public String loginWithOtp(
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

        return jwtService.generateAccessToken(userDetails);

    }

    @Transactional
    public SignupResponseDto signup(
            SignupRequestDto signupRequestDto
    ) {
        String username =
                signupRequestDto
                        .getUsername()
                        .trim()
                        .toLowerCase();


        User registeredUser =
                userRepository
                        .findByUsername(username)
                        .orElse(null);


        if(registeredUser != null){
            boolean verified = registeredUser.getVerified();
            if(verified){
                throw new EmailAlreadyExistsException(
                        "Email already registered"
                );
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

        user.setUsername(signupRequestDto.getUsername());

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        user.setPassword(encodedPassword);

        user.setRole(Role.STUDENT);

        userRepository.save(user);

        // now create corresponding student

        Student student = new Student();

        student.setUser(user);

        student.setName(signupRequestDto.getName());

        student.setRollNumber(signupRequestDto.getRollNo());

        Hostel hostel =
                hostelRepository
                        .findById(signupRequestDto.getHostelId())
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
    public SignupVerifyResponseDto verifySignup(
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

        SignupVerifyResponseDto signupVerifyResponseDto =
                new SignupVerifyResponseDto();

        signupVerifyResponseDto.setIdentifier(identifier);
        signupVerifyResponseDto.setMessage(
                "User verified successfully"
        );
        return signupVerifyResponseDto;
    }

}
