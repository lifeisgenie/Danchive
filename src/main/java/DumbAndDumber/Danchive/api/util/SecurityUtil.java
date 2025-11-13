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

    public static boolean isGuest() {
        Authentication a = getAuthentication();
        if (a.getAuthorities().stream().anyMatch(ga -> "ROLE_GUEST".equals(ga.getAuthority()))) return true;
        Object p = a.getPrincipal();
        return (p instanceof String s) && s.startsWith("guest:");
    }
    public static String getGuestIdOrNull() {
        Object p = getAuthentication().getPrincipal();
        if (p instanceof String s && s.startsWith("guest:")) return s.substring("guest:".length());
        return null;
    }

    public static Long getCurrentUserId() {
        Object p = getAuthentication().getPrincipal();
        if (p instanceof User u) return u.getId();
        if (p instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            String username = ud.getUsername();
            Long id = parseLongOrNull(username);
            if (id != null) return id;
            return findUserByEmailOrThrow(username).getId();
        }
        if (p instanceof String s) {
            Long id = parseLongOrNull(s);
            if (id != null) return id;
            return findUserByEmailOrThrow(s).getId();
        }
        if (p instanceof Long id) return id;
        throw new IllegalStateException("지원하지 않는 principal 타입: " + p.getClass());
    }

    public static User getCurrentUserOrThrow() {
        Object p = getAuthentication().getPrincipal();
        if (p instanceof User u) return u;
        if (p instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            String username = ud.getUsername();
            Long id = parseLongOrNull(username);
            if (id != null) return findUserByIdOrThrow(id);
            return findUserByEmailOrThrow(username);
        }
        if (p instanceof String s) {
            Long id = parseLongOrNull(s);
            if (id != null) return findUserByIdOrThrow(id);
            return findUserByEmailOrThrow(s);
        }
        if (p instanceof Long id) return findUserByIdOrThrow(id);
        throw new IllegalStateException("지원하지 않는 principal 타입: " + p.getClass());
    }

    private static Long parseLongOrNull(String s) {
        if (s == null) return null;
        try { return Long.valueOf(s); } catch (NumberFormatException ignore) { return null; }
    }
    private static UserRepository repo() { return SpringContext.getBean(UserRepository.class); }
    private static User findUserByIdOrThrow(Long id) {
        return repo().findById(id).orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. id=" + id));
    }
    private static User findUserByEmailOrThrow(String email) {
        return repo().findByEmail(email).orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. email=" + email));
    }
}