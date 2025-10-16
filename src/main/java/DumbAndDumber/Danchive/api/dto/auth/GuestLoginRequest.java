package DumbAndDumber.Danchive.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestLoginRequest {
    private String qr; // ex) 전시회 입장 QR payload
}
