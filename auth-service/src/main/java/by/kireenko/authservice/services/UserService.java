package by.kireenko.authservice.services;

import by.kireenko.authservice.dto.UpdateUserRequestDto;
import by.kireenko.authservice.dto.UserDto;
import by.kireenko.authservice.model.User;
import by.kireenko.authservice.repositories.UserRepository;
import by.kireenko.authservice.utils.UserEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        log.info("Admin request to find all users");
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto findUserById(Long id) {
        log.info("Admin request to find user by ID: {}", id);
        return userRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Admin request to delete user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
        userEventProducer.sendUserDeletedEvent(user.getUsername(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUserProfile() {
        User currentUser = getCurrentAuthenticatedUser();
        log.info("Request to get current user profile for: {}", currentUser.getUsername());
        return convertToDto(currentUser);
    }

    @Transactional
    public void deleteCurrentUserAccount() {
        User currentUser = getCurrentAuthenticatedUser();
        log.info("Request to delete current user account for: {}", currentUser.getUsername());
        userRepository.delete(currentUser);
        log.info("User account for {} successfully deleted", currentUser.getUsername());
        userEventProducer.sendUserDeletedEvent(currentUser.getUsername(), currentUser.getEmail());
    }

    @Transactional
    public UserDto updateUserByAdmin(Long id, UpdateUserRequestDto request) {
        log.info("Admin request to update user with ID: {}", id);
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        updateUserData(userToUpdate, request);

        User updatedUser = userRepository.save(userToUpdate);

        userEventProducer.sendUserUpdatedEvent(updatedUser, request.getPassword());

        return convertToDto(updatedUser);
    }

    @Transactional
    public UserDto updateCurrentUserProfile(UpdateUserRequestDto request) {
        User currentUser = getCurrentAuthenticatedUser();
        log.info("Request to update profile for user: {}", currentUser.getUsername());

        updateUserData(currentUser, request);

        User updatedUser = userRepository.save(currentUser);

        userEventProducer.sendUserUpdatedEvent(updatedUser, request.getPassword());

        return convertToDto(updatedUser);
    }

    private User getCurrentAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Getting current authenticated user from Security Context: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Authenticated user '{}' not found in database!", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }

    private void updateUserData(User user, UpdateUserRequestDto request) {
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }
}
