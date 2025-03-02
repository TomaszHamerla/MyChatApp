package com.example.chatapp.service;

import com.example.chatapp.exception.EmailAlreadyExistsException;
import com.example.chatapp.exception.RoleNotInitialized;
import com.example.chatapp.model.Token;
import com.example.chatapp.model.User;
import com.example.chatapp.model.auth.AuthReq;
import com.example.chatapp.repository.RoleRepository;
import com.example.chatapp.repository.TokenRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;

    public void register(AuthReq authReq) {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotInitialized("ROLE USER was not initiated"));
        var user = User.builder()
                .email(authReq.getEmail())
                .password(passwordEncoder.encode(authReq.getPassword()))
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Email " + user.getEmail() + " already exists!");
        }
        generateAndSaveActivationToken(user);
    }

    public String login(AuthReq authReq) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReq.getEmail(),
                        authReq.getPassword()
                )
        );
        var user = ((User) auth.getPrincipal());

        return jwtService.generateToken(user);
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
