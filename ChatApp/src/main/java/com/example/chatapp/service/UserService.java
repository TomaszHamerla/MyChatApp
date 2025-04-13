package com.example.chatapp.service;

import com.example.chatapp.model.user.UserResponse;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public List<UserResponse> finAllUsersExceptSelf(String jwt) {
        String userEmail = jwtService.extractUsername(jwt);

        return userRepository.findAllUsersExceptSelf(userEmail)
                .stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .build())
                .toList();
    }
}
