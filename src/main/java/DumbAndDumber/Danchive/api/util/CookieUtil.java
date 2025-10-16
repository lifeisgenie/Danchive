package DumbAndDumber.Danchive.api.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Optional;

@UtilityClass
public class CookieUtil {

    public static final String RT_COOKIE = "RT";

    public void addHttpOnlyCookie(HttpServletResponse res, String name, String value, int maxAgeSeconds, String domain) {
        Cookie c = new Cookie(name, value);
        c.setHttpOnly(true);
        c.setSecure(true);
        c.setPath("/");
        c.setMaxAge(maxAgeSeconds);
        if (domain != null && !domain.isBlank()) c.setDomain(domain);
        // SameSite 설정(서블릿 표준엔 없음) → 헤더로 직접 추가
        res.addCookie(c);
        res.addHeader("Set-Cookie",
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Lax%s",
                        name, value, maxAgeSeconds, (domain!=null && !domain.isBlank() ? "; Domain="+domain : "")));
    }

    public Optional<String> getCookie(HttpServletRequest req, String name) {
        if (req.getCookies()==null) return Optional.empty();
        return Arrays.stream(req.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public void clearCookie(HttpServletResponse res, String name, String domain) {
        addHttpOnlyCookie(res, name, "", 0, domain);
    }
}
