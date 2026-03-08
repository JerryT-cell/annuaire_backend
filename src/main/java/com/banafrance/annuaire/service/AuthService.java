package com.banafrance.annuaire.service;

import com.banafrance.annuaire.config.JwtService;
import com.banafrance.annuaire.dto.request.*;
import com.banafrance.annuaire.dto.response.AuthResponse;
import com.banafrance.annuaire.dto.response.MessageResponse;
import com.banafrance.annuaire.exception.DuplicateResourceException;
import com.banafrance.annuaire.exception.InvalidTokenException;
import com.banafrance.annuaire.exception.ResourceNotFoundException;
import com.banafrance.annuaire.model.Profile;
import com.banafrance.annuaire.model.User;
import com.banafrance.annuaire.repository.ProfileRepository;
import com.banafrance.annuaire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailOutboxService emailOutboxService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(user)
                .name(request.getName())
                .surname(request.getSurname())
                .build();
        profileRepository.save(profile);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken) || !"refresh".equals(jwtService.getTokenType(refreshToken))) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String email = jwtService.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildAuthResponse(user);
    }

    public MessageResponse requestPasswordReset(PasswordResetRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = jwtService.generatePasswordResetToken(user.getEmail());
            String payload = "{\"token\":\"" + token + "\",\"email\":\"" + user.getEmail() + "\"}";
            emailOutboxService.queueEmail(user.getEmail(), "password-reset", payload);
        });
        // Always return success to prevent email enumeration
        return MessageResponse.builder().message("If an account exists, a reset email has been sent").build();
    }

    @Transactional
    public MessageResponse applyPasswordReset(PasswordResetApplyRequest request) {
        if (!jwtService.validateToken(request.getToken())
                || !"password-reset".equals(jwtService.getTokenType(request.getToken()))) {
            throw new InvalidTokenException("Invalid or expired password reset token");
        }

        String email = jwtService.getEmailFromToken(request.getToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return MessageResponse.builder().message("Password reset successfully").build();
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .build();
    }
}
