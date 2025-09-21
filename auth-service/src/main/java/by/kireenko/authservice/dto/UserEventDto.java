package by.kireenko.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDto implements Serializable {
    private String eventType;
    private String username;
    private String email;
    private String password;
    private List<String> recipients;
}