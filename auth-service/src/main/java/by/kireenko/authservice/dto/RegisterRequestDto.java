package by.kireenko.authservice.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
