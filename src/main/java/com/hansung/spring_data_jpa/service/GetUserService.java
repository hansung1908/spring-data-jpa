package com.hansung.spring_data_jpa.service;

import com.hansung.spring_data_jpa.domain.User;
import com.hansung.spring_data_jpa.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserService {
    private UserRepository userRepository;

    public GetUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String email) {
        Optional<User> userOpt = userRepository.findById(email);
        return userOpt.orElseThrow(() -> new NoUserException());
    }
}
