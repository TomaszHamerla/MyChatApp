package com.example.chatapp.service;

import com.example.chatapp.model.Role;
import com.example.chatapp.model.auth.AuthReq;
import com.example.chatapp.model.email.EmailTemplateName;
import com.example.chatapp.model.user.User;
import com.example.chatapp.repository.RoleRepository;
import com.example.chatapp.repository.TokenRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.security.JwtService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private LogService logService;
    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() throws MessagingException {
        // given
        AuthReq authReq = new AuthReq();
        authReq.setEmail("user@example.com");
        authReq.setPassword("password123");

        Role userRole = new Role();
        userRole.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // when
        authService.register(authReq, "http://localhost:8080");

        // then
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("user@example.com")
                        && user.getPassword().equals("encodedPassword")
                        && !user.isEnabled()
                        && user.getRoles().contains(userRole)
        ));
        verify(logService).logInfo("Added new user to db");
        verify(emailService).sendEmail(
                eq("user@example.com"),
                eq(EmailTemplateName.ACTIVATE_ACCOUNT),
                anyString(),
                eq("Aktywacja konta"),
                eq("http://localhost:8080/activate-account")
        );
    }
}
