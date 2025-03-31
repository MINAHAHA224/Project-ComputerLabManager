package com.example.computerweb.services.impl;

import com.example.computerweb.repositories.IAccountRepository;
import com.example.computerweb.services.IAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl  implements IAccountService {
    private  final IAccountRepository   iAccountRepository;
    @Override
    public boolean checkEmailExist(String email) {
        return this.iAccountRepository.existsByEmail(email);
    }
}
