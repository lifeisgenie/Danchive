package DumbAndDumber.Danchive.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FcmTokenRequest {
    @NotBlank
    private String token;
}