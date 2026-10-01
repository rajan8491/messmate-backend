package org.example.messmate.service;

import org.example.messmate.dto.auth.UserResponseDto;
import org.example.messmate.entity.User;
import org.example.messmate.exception.UserNotFoundException;
import org.example.messmate.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndVerifiedIsTrue(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Username not found")
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .disabled(!user.getEnabled())
                .build();
    }

    public UserResponseDto getByUsername(String username){
        username = username
                .trim()
                .toLowerCase();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        UserNotFoundException::new
                );

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setVerified(user.getVerified());
        userResponseDto.setRole(user.getRole());
        return userResponseDto;
    }
}
