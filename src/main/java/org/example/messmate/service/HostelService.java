package org.example.messmate.service;

import org.example.messmate.dto.hostelDto.HostelCreateDto;
import org.example.messmate.dto.hostelDto.HostelResponseDto;
import org.example.messmate.dto.hostelDto.HostelUpdateDto;
import org.example.messmate.dto.otpDto.OtpSendRequestDto;
import org.example.messmate.dto.otpDto.OtpSendResponseDto;
import org.example.messmate.dto.otpDto.OtpVerifyRequestDto;
import org.example.messmate.dto.studentdto.StudentResponseDto;
import org.example.messmate.entity.Accountant;
import org.example.messmate.entity.Hostel;
import org.example.messmate.entity.User;
import org.example.messmate.enums.OtpPurpose;
import org.example.messmate.enums.Residents;
import org.example.messmate.enums.Role;
import org.example.messmate.exception.ResourceNotFoundException;
import org.example.messmate.mapper.HostelMapper;
import org.example.messmate.repository.AccountantRepository;
import org.example.messmate.repository.HostelRepository;
import org.example.messmate.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class HostelService {
    private final HostelRepository hostelRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountantRepository accountantRepository;
    private final OtpService otpService;

    public HostelService(HostelRepository hostelRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, AccountantRepository accountantRepository, OtpService otpService) {
        this.hostelRepository = hostelRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.accountantRepository = accountantRepository;
        this.otpService = otpService;
    }

    public HostelResponseDto getHostelById(@NonNull Long id) {
        Hostel hostel =
                hostelRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Hostel not found")
                        );

        return HostelMapper.toDto(hostel);
    }

    public List<HostelResponseDto> getHostels() {
        List<Hostel> hostels = hostelRepository.findAll();

        List<HostelResponseDto> hostelsDto = new ArrayList<>();

        for(Hostel hostel : hostels){
            HostelResponseDto hostelDto =
                    HostelMapper.toDto(hostel);
            hostelsDto.add(hostelDto);
        }

        return hostelsDto;
    }

    @Transactional
    public void createHostel(
            HostelCreateDto hostelCreateDto
    ) {
        // create user -> accountant
        // 1. extract accountant details
        String username = hostelCreateDto.getLoginId();
        String password = hostelCreateDto.getPassword();

        User user = new User();
        user.setUsername(username);
        user.setPassword(
                passwordEncoder.encode(password)
        );
        user.setRole(Role.ACCOUNTANT);
        user.setVerified(true);

        userRepository.save(user);

        // 2. create accountant -> extract accountant details
        String accName = hostelCreateDto.getAccountantName();
        String accPhone = hostelCreateDto.getAccountantPhone();

        Accountant accountant = new Accountant();
        accountant.setName(accName);
        accountant.setPhone(accPhone);
        accountant.setUser(user);

        accountantRepository.save(accountant);

        // 3. create hostel -> extract hostel details
        Hostel hostel = getHostel(hostelCreateDto, accountant);

        hostelRepository.save(hostel);
    }

    @Transactional
    public void updateHostel(Long id, HostelUpdateDto dto) {
        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hostel not found with id: " + dto.getId()
                        )
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            hostel.setName(dto.getName());
        }

        if (dto.getResidents() != null) {
            hostel.setResidents(dto.getResidents());
        }

        if (dto.getHostelEmail() != null && !dto.getHostelEmail().isBlank()) {
            hostel.setEmail(dto.getHostelEmail());
        }

        if (dto.getHostelPhone() != null) {
            hostel.setPhone(dto.getHostelPhone());
        }

        // update accountant field if accountant details is coming

        if(dto.getLoginId() != null && dto.getPassword() != null){
            Accountant accountant = hostel.getAccountant();
            User user;
            if(accountant == null){
                user = new User();
                accountant = new Accountant();
                accountant.setUser(user);
            }
            else user = accountant.getUser();

            user.setUsername(dto.getLoginId());
            user.setPassword(
                    passwordEncoder.encode(dto.getPassword())
            );
            user.setRole(Role.ACCOUNTANT);
            user.setVerified(true);

            userRepository.save(user);

            if (dto.getAccountantName() != null) {
                accountant.setName(dto.getAccountantName());
            }


            if (dto.getAccountantPhone() != null) {
                accountant.setPhone(dto.getAccountantPhone());
            }

            accountantRepository.save(accountant);
        }
        hostelRepository.save(hostel);
    }

    private static @NonNull Hostel getHostel(HostelCreateDto hostelCreateDto, Accountant accountant) {
        Long hostelId = hostelCreateDto.getId();
        String hostelName = hostelCreateDto.getName();
        Residents residents = hostelCreateDto.getResidents();
        String hostelEmail = hostelCreateDto.getHostelEmail();
        String hostelPhone = hostelCreateDto.getHostelPhone();

        Hostel hostel = new Hostel();
        hostel.setId(hostelId);
        hostel.setName(hostelName);
        hostel.setResidents(residents);
        hostel.setEmail(hostelEmail);
        hostel.setPhone(hostelPhone);
        hostel.setAccountant(accountant);
        return hostel;
    }

    public OtpSendResponseDto sendHostelRemoveOtp(
            OtpSendRequestDto otpSendRequestDto
    ) {
        String identifier =
                otpSendRequestDto
                        .getIdentifier()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        otpService.requestOtp(
                identifier,
                OtpPurpose.REMOVE_HOSTEL,
                otpSendRequestDto.getChannel()
        );

        OtpSendResponseDto responseDto =
                new OtpSendResponseDto();

        responseDto.setIdentifier(identifier);
        return responseDto;
    }

    @Transactional
    public void removeHostel(
            Long id,
            OtpVerifyRequestDto otpVerifyRequestDto
    ) {
        String identifier =
                otpVerifyRequestDto
                        .getIdentifier()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        otpService.verifyOtp(
                identifier,
                OtpPurpose.REMOVE_HOSTEL,
                otpVerifyRequestDto.getOtp()
        );

        hostelRepository.deleteById(id);

    }

}
