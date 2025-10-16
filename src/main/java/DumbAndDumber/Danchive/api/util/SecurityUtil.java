package DumbAndDumber.Danchive.api.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
    private SecurityUtil() {}
    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 프로젝트에 맞게 Principal에서 userId를 꺼내도록 수정
        // 예: CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        // return principal.getUserId();
        if (auth == null || auth.getPrincipal() == null) throw new IllegalStateException("인증 정보가 없습니다.");
        // 데모: principal이 Long이라고 가정
        if (auth.getPrincipal() instanceof Long id) return id;
        throw new IllegalStateException("사용자 식별자를 가져올 수 없습니다.");
    }
}
