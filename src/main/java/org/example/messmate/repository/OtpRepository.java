package org.example.messmate.repository;

import jakarta.persistence.LockModeType;
import org.example.messmate.entity.Otp;
import org.example.messmate.enums.OtpPurpose;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OtpRepository extends CrudRepository<Otp, Integer> {

    Optional<Otp> findTopByIdentifierAndPurposeAndVerifiedFalseOrderByCreatedAtDesc(
            String identifier,
            OtpPurpose purpose
    );

    @Modifying
    @Query("""
        update Otp o
        set o.verified = true,
            o.verifiedAt = :now
        where o.identifier = :identifier
            and o.purpose = :purpose
            and o.verified = false
    
        """)
    void consumePreviousOtps(
            @Param("identifier") String identifier,
            @Param("purpose") OtpPurpose purpose,
            @Param("now") Instant now
    );

    boolean existsByIdentifierAndPurposeAndCreatedAtAfter(String identifier, OtpPurpose otpPurpose, Instant cutoff);

    // locks the otp rows so that only one thread request read it
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select o
    from Otp o
    where o.identifier = :identifier
      and o.purpose = :purpose
      and o.verified = false
    order by o.createdAt desc
""")
    List<Otp> findActiveChallengesForUpdate(
            @Param("identifier") String identifier,
            @Param("purpose") OtpPurpose purpose
    );
}
