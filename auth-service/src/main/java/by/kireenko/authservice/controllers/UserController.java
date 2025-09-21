package by.kireenko.authservice.controllers;

import by.kireenko.authservice.dto.UpdateUserRequestDto;
import by.kireenko.authservice.dto.UserDto;
import by.kireenko.authservice.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserDto getMyProfile() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping("/me")
    public UserDto updateMyProfile(@RequestBody UpdateUserRequestDto request) {
        return userService.updateCurrentUserProfile(request);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        userService.deleteCurrentUserAccount();
        return ResponseEntity.noContent().build();
    }
}