package by.kireenko.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDto {
    private String eventType;
    private String username;
    private String email;
    private String password;
    private List<String> recipients;
}
