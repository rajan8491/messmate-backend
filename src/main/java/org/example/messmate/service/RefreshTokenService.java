package org.example.messmate.service;

import org.example.messmate.entity.RefreshToken;
import org.example.messmate.entity.User;
import org.example.messmate.exception.UserNotFoundException;
import org.example.messmate.exception.UserUnauthorizedException;
import org.example.messmate.repository.RefreshTokenRepository;
import org.example.messmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;


@Service
public class RefreshTokenService {
    @Value("${jwt.refresh-expiry:604800}")
    private long refreshDurationSeconds;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom =  new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public record TokenPairHolder(
            String rawToken,
            RefreshToken tokenEntity
    ){}

    @Transactional
    public TokenPairHolder createRefreshToken(String username){
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                UserNotFoundException::new
                        );

        String rawToken = generateSecureRandomString();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshDurationSeconds));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return new TokenPairHolder(rawToken, refreshToken);
    }

    public TokenPairHolder rotateRefreshToken(
            String rawIncomingToken
    ){
        String incomingTokenHash = hashToken(rawIncomingToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(incomingTokenHash)
                        .orElseThrow(UserUnauthorizedException::new);

        // Reuse / Theft Detection: If an already-revoked token is used, invalidate all sessions!

        if(refreshToken.isRevoked()){
            refreshTokenRepository.revokeAllValidTokensByUser(refreshToken.getUser());

            throw new UserUnauthorizedException();
        }

        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            throw new UserUnauthorizedException();
        }

        // generate new refreshToken

        String token = generateSecureRandomString();
        String tokenHash = hashToken(token);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUser(refreshToken.getUser());
        newRefreshToken.setTokenHash(tokenHash);
        newRefreshToken.setExpiryDate(Instant.now().plusSeconds(refreshDurationSeconds));
        newRefreshToken.setRevoked(false);

        refreshTokenRepository.save(newRefreshToken);

        // Mark old token as revoked and track replacement
        refreshToken.setRevoked(true);
        refreshToken.setReplacedByHash(tokenHash);
        refreshTokenRepository.save(newRefreshToken);

        return new TokenPairHolder(token, newRefreshToken);
    }

    @Transactional
    public void revokeRefreshToken(String rawToken){
        String tokenHash = hashToken(rawToken);
        refreshTokenRepository
                .findByTokenHash(tokenHash)
                .ifPresent(refreshToken -> refreshToken.setRevoked(true));
    }

    private String generateSecureRandomString(){
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token){
        try  {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex){
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }


}
