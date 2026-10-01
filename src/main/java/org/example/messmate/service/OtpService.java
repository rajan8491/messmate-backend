package org.example.messmate.service;

import jakarta.transaction.Transactional;
import org.example.messmate.entity.Otp;
import org.example.messmate.notification.application.NotificationService;
import org.example.messmate.notification.domain.Notification;
import org.example.messmate.notification.domain.NotificationChannel;
import org.example.messmate.enums.OtpPurpose;
import org.example.messmate.exception.otp.InvalidOtpException;
import org.example.messmate.exception.otp.OtpResendTooSoonException;
import org.example.messmate.notification.domain.NotificationTemplate;
import org.example.messmate.properties.OtpProperties;
import org.example.messmate.repository.OtpRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;

@Service
public class OtpService {

    private final SecureRandom random = new SecureRandom();

    private final OtpProperties otpProperties;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public OtpService(
            OtpProperties otpProperties,
            PasswordEncoder passwordEncoder,
            OtpRepository otpRepository,
            NotificationService notificationService
    ) {
        this.otpProperties = otpProperties;
        this.passwordEncoder = passwordEncoder;
        this.otpRepository = otpRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void requestOtp(
            String identifier,
            OtpPurpose purpose,
            NotificationChannel channel
    ) {
        //TODO -> 1: rate-limit otp generation


        // coolDown
        checkResendCooldown(identifier);

        String otp = generateOtp(otpProperties.length);

        String otpHash = passwordEncoder.encode(otp);

        Instant now = Instant.now();

        // invalidate previous otp

        otpRepository.consumePreviousOtps(
                identifier,
                purpose,
                now
        );

        Otp challenge = new Otp();
        challenge.setIdentifier(identifier);
        challenge.setPurpose(purpose);
        challenge.setOtpHash(otpHash);
        challenge.setCreatedAt(now);
        challenge.setExpiresAt(
                now.plus(otpProperties.getExpiry())
        );
        challenge.setAttempts(0);
        challenge.setVerified(false);

        otpRepository.save(challenge);

        // send plaintext otp to the user
        sendOtp(
                channel,
                identifier,
                otp
        );
    }

    void sendOtp(
            NotificationChannel channel,
            String identifier,
            String otp
    ){
        Notification notification = new Notification(
                channel,
                identifier,
                NotificationTemplate.OTP_VERIFICATION,
                Map.of(
                        "otp", otp
                )
        );

        notificationService.send(notification);
    }

    @Transactional
    public void verifyOtp(
            String identifier,
            OtpPurpose purpose,
            String otp
    ) {

        Otp challenge = otpRepository
                .findTopByIdentifierAndPurposeAndVerifiedFalseOrderByCreatedAtDesc(
                        identifier,
                        purpose
                )
                .orElseThrow(
                        InvalidOtpException::new
                );

        Instant now = Instant.now();

        if(challenge.getExpiresAt().isBefore(now)) {
            throw new InvalidOtpException();
        }

        if(challenge.getAttempts() >= otpProperties.getMaxAttempts()) {
            throw new InvalidOtpException();
        }

        boolean valid = passwordEncoder.matches(
                otp,
                challenge.getOtpHash()
        );

        if(!valid) {
            challenge.setAttempts(challenge.getAttempts() + 1);
            otpRepository.save(challenge);
            throw new InvalidOtpException();
        }

        // consume otp
        challenge.setAttempts(challenge.getAttempts() + 1);
        challenge.setVerified(true);
        challenge.setVerifiedAt(now);

        otpRepository.save(challenge);
    }

    public void checkResendCooldown(String identifier) {

        Instant cutoff = Instant.now().minus(otpProperties.getResendCooldown());

        boolean recentlyCreated =
                otpRepository.existsByIdentifierAndPurposeAndCreatedAtAfter(
                        identifier,
                        OtpPurpose.LOGIN,
                        cutoff
                );

        if(recentlyCreated) {
            throw new OtpResendTooSoonException();
        }

    }

    public String generateOtp(int length) {
        int bound = (int) Math.pow(10, length);

        int otp = random.nextInt(bound);

        return String.format("%0" + length + "d", otp);
    }
}
