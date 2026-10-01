package org.example.messmate.repository;

import org.example.messmate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndVerifiedIsTrue(String username);

    boolean existsByUsername(String identifier);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM User u WHERE u.username IN :usernames")
    void deleteAllByUsernameIn(
            @Param("usernames") List<String> usernames
    );
}
