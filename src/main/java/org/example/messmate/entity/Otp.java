package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.messmate.enums.OtpPurpose;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(
        name = "otp_challenges",
        indexes = {
                @Index(
                        name = "idx_otp_identifier_purpose",
                        columnList = "identifier, purpose"
                )
        }
)
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Integer attempts;

    @Column(nullable = false)
    private Boolean verified;

    private Instant verifiedAt;

}
