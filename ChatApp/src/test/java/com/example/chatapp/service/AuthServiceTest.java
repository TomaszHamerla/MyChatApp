package com.example.chatapp.service;

import com.example.chatapp.exception.ActivationTokenException;
import com.example.chatapp.exception.EmailAlreadyExistsException;
import com.example.chatapp.exception.RoleNotInitialized;
import com.example.chatapp.model.Role;
import com.example.chatapp.model.Token;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private User user;
    private Token token;

    @BeforeEach
    void setUp() {
        authReq = new AuthReq();
        authReq.setEmail("user@example.com");
        authReq.setPassword("password123");

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .enabled(false)
                .build();

        token = Token.builder()
                .token("valid-token")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();
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

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {
        // given
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when + then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(authReq);
        });
    }

    @Test
    void shouldThrowExceptionIfPrincipalIsNotUser() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("not a user");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // when + then
        assertThrows(ClassCastException.class, () -> {
            authService.login(authReq);
        });
    }

    @Test
    void activateAccountShouldActivateAccountSuccessfully() throws MessagingException {
        // given
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        authService.activateAccount("valid-token", "http://localhost");

        // then
        assertTrue(user.isEnabled());
        assertNotNull(token.getValidatedAt());
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void activateAccountShouldThrowWhenTokenIsExpiredAndSendNewEmail() {
        // given
        token.setExpiresAt(LocalDateTime.now().minusHours(1)); // expired
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        // when + then
        ActivationTokenException ex = assertThrows(ActivationTokenException.class, () -> {
            authService.activateAccount("valid-token", "http://localhost");
        });

        assertEquals("Token aktywacyjny wygasł, wysłano nowy na podany adres email", ex.getMessage());
    }

    @Test
    void activateAccountShouldThrowWhenTokenNotFound() {
        // given
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // when + then
        assertThrows(ActivationTokenException.class, () -> {
            authService.activateAccount("invalid-token", "http://localhost");
        });
    }

    @Test
    void activateAccountShouldThrowWhenUserNotFound() {
        // given
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(UsernameNotFoundException.class, () -> {
            authService.activateAccount("valid-token", "http://localhost");
        });
    }

    @Test
    void sendResetPasswordLinkShouldSendResetPasswordEmail() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // when
        authService.sendResetPasswordLink("test@example.com", "http://localhost");

        // then
        verify(tokenRepository, atLeastOnce()).save(any(Token.class));
    }

    @Test
    void sendResetPasswordLinkShouldThrowWhenUserNotFound() {
        // given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // when + then
        assertThrows(UsernameNotFoundException.class, () -> {
            authService.sendResetPasswordLink("notfound@example.com", "http://localhost");
        });

        verifyNoInteractions(tokenRepository);
        verifyNoInteractions(logService);
    }

    @Test
    void sendResetPasswordLinkShouldLogErrorWhenEmailSendingFails() throws MessagingException {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        doThrow(new MessagingException()).when(emailService)
                .sendEmail(any(),any(), any(), any(), any());

        // when
        authService.sendResetPasswordLink("test@example.com", "http://localhost");

        // then
        verify(logService, atLeastOnce()).logError(any());
    }
}
