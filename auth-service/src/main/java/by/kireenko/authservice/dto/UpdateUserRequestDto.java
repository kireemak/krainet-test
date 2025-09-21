package by.kireenko.authservice.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}