package org.example.messmate.repository;

import org.example.messmate.entity.RefreshToken;
import org.example.messmate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {


    Optional<RefreshToken> findByTokenHash(String refreshToken);

    @Modifying
    @Query(
            """
            UPDATE RefreshToken r
            SET r.revoked = true
            WHERE r.user = :user
            AND r.revoked = false 
""")
    void revokeAllValidTokensByUser(@Param("user") User user);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken r
            WHERE r.expiryDate < :now
""")
    void deleteAllExpiredSince(@Param("now") LocalDateTime now);

}
