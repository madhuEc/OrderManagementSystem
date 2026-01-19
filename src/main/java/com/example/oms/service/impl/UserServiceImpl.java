package com.example.oms.service.impl;

import org.springframework.stereotype.Service;

import com.example.oms.dto.SignupRequest;
import com.example.oms.entity.User;
import com.example.oms.repository.UserRepository;
import com.example.oms.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder;

    @Override
    public User signup(SignupRequest signupRequest) {
        System.out.println(signupRequest);
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword())
                .phoneNumber(signupRequest.getPhoneNumber())
                .address(signupRequest.getAddress())
                .build();

        return userRepository.save(user);
    }
}
