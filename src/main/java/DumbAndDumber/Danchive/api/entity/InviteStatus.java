package DumbAndDumber.Danchive.api.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/** 팀 초대장 상태 */
public enum InviteStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    CANCELED("canceled"),
    EXPIRED("expired");

    private final String wire;
    InviteStatus(String wire) { this.wire = wire; }

    @JsonValue public String toWire() { return wire; }

    @JsonCreator
    public static InviteStatus fromWire(String s) {
        return Arrays.stream(values())
                .filter(v -> v.wire.equalsIgnoreCase(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown invite status: " + s));
    }
}
