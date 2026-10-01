package org.example.messmate.service;

import org.example.messmate.dto.AccountantDto;
import org.example.messmate.entity.Accountant;
import org.example.messmate.entity.Hostel;
import org.example.messmate.entity.User;
import org.example.messmate.exception.UserNotFoundException;
import org.example.messmate.repository.AccountantRepository;
import org.example.messmate.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountantService {

    private final AccountantRepository accountantRepository;
    private final UserRepository userRepository;

    public AccountantService(AccountantRepository accountantRepository, UserRepository userRepository) {
        this.accountantRepository = accountantRepository;
        this.userRepository = userRepository;
    }

    public Long getHostelId(String username) {
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(UserNotFoundException::new);

        Accountant accountant =
                user.getAccountant();

        return accountant.getHostel().getId();

    }

    public AccountantDto getAccountantProfile(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Accountant accountant =
                user.getAccountant();

        AccountantDto accountantDto = new AccountantDto();
        accountantDto.setName(accountant.getName());
        accountantDto.setEmail(username);
        accountantDto.setHostelId(accountant.getHostel().getId());
        accountantDto.setHostelName(accountant.getHostel().getName());
        accountantDto.setHostelEmail(accountant.getHostel().getEmail());

        return accountantDto;
    }
}
