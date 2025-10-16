package DumbAndDumber.Danchive.api.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        // 예: principal에 userId(Long)이 들어있다고 가정 (프로젝트 상황에 맞게 변환)
        if (auth.getPrincipal() instanceof Long id) return id;
        if (auth.getPrincipal() instanceof String s) return Long.valueOf(s);
        // 필요한 경우 UserDetails 캐스팅 등 프로젝트 구조에 맞춰 수정
        throw new IllegalStateException("지원하지 않는 principal 타입: " + auth.getPrincipal().getClass());
    }
}
