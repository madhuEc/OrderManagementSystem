package com.example.oms.service;

import com.example.oms.dto.SignupRequest;
import com.example.oms.entity.User;

public interface UserService {

    User signup(SignupRequest signupRequest);
}
