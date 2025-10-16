package DumbAndDumber.Danchive.api.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/** 팀 내 역할 */
public enum TeamRole {
    LEADER("leader"),
    MEMBER("member");

    private final String wire;
    TeamRole(String wire) { this.wire = wire; }

    /** API로는 항상 소문자 문자열로 고정 노출 */
    @JsonValue public String toWire() { return wire; }

    /** 문자열 입력을 enum으로 파싱 (대소문자 무시) */
    @JsonCreator
    public static TeamRole fromWire(String s) {
        return Arrays.stream(values())
                .filter(v -> v.wire.equalsIgnoreCase(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown team role: " + s));
    }
}
