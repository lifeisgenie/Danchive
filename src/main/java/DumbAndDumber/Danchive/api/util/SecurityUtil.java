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

        Object p = auth.getPrincipal();
        if (p instanceof Long id) return id;
        if (p instanceof String s) return Long.valueOf(s);
        if (p instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            // username을 userId로 쓰는 케이스 지원 (프로젝트에 맞게 변환)
            return Long.valueOf(ud.getUsername());
        }
        if (p instanceof DumbAndDumber.Danchive.api.entity.User u) {
            return u.getId();
        }
        throw new IllegalStateException("지원하지 않는 principal 타입: " + p.getClass());
    }
}
