package by.kireenko.authservice.services;

import by.kireenko.authservice.dto.AuthenticationRequestDto;
import by.kireenko.authservice.dto.AuthenticationResponseDto;
import by.kireenko.authservice.dto.RegisterRequestDto;
import by.kireenko.authservice.model.Role;
import by.kireenko.authservice.model.User;
import by.kireenko.authservice.repositories.UserRepository;
import by.kireenko.authservice.utils.JwtUtil;
import by.kireenko.authservice.utils.UserEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserEventProducer userEventProducer;

    @Transactional
    public AuthenticationResponseDto register(RegisterRequestDto request) {
        log.info("Attempting to register a new user with username: {}", request.getUsername());
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User {} successfully saved with ID: {}", savedUser.getUsername(), savedUser.getId());
        userEventProducer.sendUserCreatedEvent(savedUser, request.getPassword());

        String token = jwtUtil.generateToken(savedUser);
        log.info("JWT token generated for user: {}", user.getUsername());

        return new AuthenticationResponseDto(token);
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        log.info("Attempting to authenticate user: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        log.info("Authentication successful for user: {}", request.getUsername());

        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getUsername());
                    return new IllegalStateException("User not found after authentication");
                });

        String token = jwtUtil.generateToken(user);
        log.info("JWT token generated for user: {}", user.getUsername());

        return new AuthenticationResponseDto(token);
    }
}
