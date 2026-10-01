package org.example.messmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(
                        name = "idx_token_hash",
                        columnList = "token_hash"
                )
        }
)
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true
    )
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "expiry_date",
            nullable = false
    )
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(
            name = "replaced_by_hash",
            length = 64
    )
    private String replacedByHash;
}
