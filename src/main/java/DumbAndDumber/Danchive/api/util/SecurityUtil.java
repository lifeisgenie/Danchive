package DumbAndDumber.Danchive.api.util;

import DumbAndDumber.Danchive.api.entity.User;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.NoSuchElementException;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        return auth;
    }

    /** 현재 사용자 ID (숫자/문자열/커스텀 principal 모두 대응) */
    public static Long getCurrentUserId() {
        Object p = getAuthentication().getPrincipal();

        // 1) 우리 도메인의 User가 그대로 principal인 경우
        if (p instanceof User u) return u.getId();

        // 2) Spring Security UserDetails (username에 id 또는 email이 들어올 수 있음)
        if (p instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            String username = ud.getUsername();
            Long id = parseLongOrNull(username);
            if (id != null) return id;              // username이 "123" 같은 id인 케이스
            // username이 email인 케이스: email로 조회해서 id 반환
            return findUserByEmailOrThrow(username).getId();
        }

        // 3) String principal (id 문자열 or email)
        if (p instanceof String s) {
            Long id = parseLongOrNull(s);
            if (id != null) return id;              // "123"
            return findUserByEmailOrThrow(s).getId(); // "user@dku.ac.kr"
        }

        // 4) Long principal
        if (p instanceof Long id) return id;

        throw new IllegalStateException("지원하지 않는 principal 타입: " + p.getClass());
    }

    /** 현재 사용자 엔티티 (가능하면 principal 그대로, 아니면 보강 조회) */
    public static User getCurrentUserOrThrow() {
        Object p = getAuthentication().getPrincipal();

        // 1) principal이 이미 User
        if (p instanceof User u) return u;

        // 2) UserDetails → username(id or email)
        if (p instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            String username = ud.getUsername();
            Long id = parseLongOrNull(username);
            if (id != null) return findUserByIdOrThrow(id);
            return findUserByEmailOrThrow(username);
        }

        // 3) String principal → id or email
        if (p instanceof String s) {
            Long id = parseLongOrNull(s);
            if (id != null) return findUserByIdOrThrow(id);
            return findUserByEmailOrThrow(s);
        }

        // 4) Long principal → id로 조회
        if (p instanceof Long id) return findUserByIdOrThrow(id);

        throw new IllegalStateException("지원하지 않는 principal 타입: " + p.getClass());
    }

    // ===== 내부 헬퍼 =====

    private static Long parseLongOrNull(String s) {
        if (s == null) return null;
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    private static UserRepository repo() {
        return SpringContext.getBean(UserRepository.class);
    }

    private static User findUserByIdOrThrow(Long id) {
        return repo().findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. id=" + id));
    }

    private static User findUserByEmailOrThrow(String email) {
        return repo().findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. email=" + email));
    }
}