package by.kireenko.authservice.controllers;

import by.kireenko.authservice.dto.AuthenticationRequestDto;
import by.kireenko.authservice.dto.AuthenticationResponseDto;
import by.kireenko.authservice.dto.RegisterRequestDto;
import by.kireenko.authservice.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthenticationResponseDto register(@RequestBody RegisterRequestDto request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public AuthenticationResponseDto authenticate(@RequestBody AuthenticationRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
