package com.example.chatapp.service;

import com.example.chatapp.exception.EmailAlreadyExistsException;
import com.example.chatapp.exception.RoleNotInitialized;
import com.example.chatapp.model.Role;
import com.example.chatapp.model.auth.AuthReq;
import com.example.chatapp.model.auth.AuthResponse;
import com.example.chatapp.model.email.EmailTemplateName;
import com.example.chatapp.model.user.User;
import com.example.chatapp.repository.RoleRepository;
import com.example.chatapp.repository.TokenRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.security.JwtService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    private AuthReq authReq;

    @BeforeEach
    void setUp() {
        authReq = new AuthReq();
        authReq.setEmail("user@example.com");
        authReq.setPassword("password123");
    }

    @Test
    void shouldRegisterUserSuccessfully() throws MessagingException {
        // given
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

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenEmailIsTaken() {
        // given
        Role userRole = new Role();
        userRole.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Email already taken"));

        // when + then
        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class, () ->
                authService.register(authReq, "http://localhost:8080")
        );
        assertEquals("Email user@example.com jest już zarejestrowany!", ex.getMessage());
    }

    @Test
    void shouldThrowRoleNotInitializedExceptionWhenRoleMissing() {
        // given
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // when + then
        assertThrows(RoleNotInitialized.class, () ->
                authService.register(authReq, "http://localhost:8080")
        );
    }

    @Test
    void shouldLoginSuccessfully() {
        // given
        User mockUser = User.builder()
                .id(123L)
                .email("user@example.com")
                .password("encodedPassword")
                .enabled(true)
                .build();

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(mockUser)).thenReturn("mock-jwt-token");

        // when
        AuthResponse response = authService.login(authReq);

        // then
        assertEquals("mock-jwt-token", response.token());
        assertEquals(123L, response.id());
        verify(logService).logInfo("Logged user");
    }
}
