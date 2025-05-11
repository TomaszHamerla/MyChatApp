package com.example.chatapp.service;

import com.example.chatapp.exception.ActivationTokenException;
import com.example.chatapp.exception.EmailAlreadyExistsException;
import com.example.chatapp.exception.RoleNotInitialized;
import com.example.chatapp.model.Token;
import com.example.chatapp.model.auth.AuthResponse;
import com.example.chatapp.model.user.User;
import com.example.chatapp.model.auth.AuthReq;
import com.example.chatapp.model.email.EmailTemplateName;
import com.example.chatapp.model.urlRoute.UrlRoute;
import com.example.chatapp.repository.RoleRepository;
import com.example.chatapp.repository.TokenRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.chatapp.model.email.EmailTemplateName.ACTIVATE_ACCOUNT;
import static com.example.chatapp.model.email.EmailTemplateName.RESET_PASSWORD;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final LogService logService;

    public void register(AuthReq authReq, String url) throws MessagingException {
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
            throw new EmailAlreadyExistsException("Email " + user.getEmail() + " jest już zarejestrowany!");
        }
        logService.logInfo("Added new user to db");
        url = url + UrlRoute.ACTIVE_ACCOUNT.getName();
        sendValidationEmail(user, url, ACTIVATE_ACCOUNT, "Aktywacja konta");
    }

    /*
    if user is enabled throwing DisabledException
    given bad credentials throwing BadCredentialsException
     */
    public AuthResponse login(AuthReq authReq) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReq.getEmail(),
                        authReq.getPassword()
                )
        );
        var user = ((User) auth.getPrincipal());

        logService.logInfo("Logged user");
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId());
    }

    @Transactional(noRollbackFor = ActivationTokenException.class)
    public void activateAccount(String token, String url) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ActivationTokenException("Błędny token aktywacyjny"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            url = url + UrlRoute.ACTIVE_ACCOUNT.getName();
            sendValidationEmail(savedToken.getUser(), url, ACTIVATE_ACCOUNT, "Aktywacja konta");
            throw new ActivationTokenException("Token aktywacyjny wygasł, wysłano nowy na podany adres email");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public void sendResetPasswordLink(String email, String url) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        url = url + UrlRoute.NEW_PASSWORD.getName();
        try {
            sendValidationEmail(user, url, RESET_PASSWORD, "Reset hasła");
        } catch (MessagingException e) {
            logService.logError(e.getMessage());
        }
    }

    @Transactional
    public void resetPassword(AuthReq authReq) {
        var user = userRepository.findByEmail(authReq.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(authReq.getPassword()));
        userRepository.save(user);
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

    private void sendValidationEmail(User user, String url, EmailTemplateName emailTemplateName, String subject) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                emailTemplateName,
                newToken,
                subject,
                url
        );
    }
}
