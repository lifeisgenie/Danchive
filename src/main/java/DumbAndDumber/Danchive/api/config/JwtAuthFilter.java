package DumbAndDumber.Danchive.api.config;

import DumbAndDumber.Danchive.api.util.JwtUtil;
import DumbAndDumber.Danchive.domain.User;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String accessToken = header.substring(7);

            try {
                Jws<Claims> jws = jwtUtil.parse(accessToken);
                String email = jws.getBody().getSubject();
                Instant exp   = jws.getBody().getExpiration().toInstant();

                Optional<User> ou = userRepository.findByEmail(email);
                if (ou.isPresent()) {
                    User user = ou.get();

                    // 화이트리스트: DB에 저장된 AT와 정확히 일치하고, 만료 전이어야만 인증
                    if (accessToken.equals(user.getAccessToken())
                            && user.getAccessTokenExp() != null
                            && Instant.now().isBefore(user.getAccessTokenExp())
                            && Instant.now().isBefore(exp)) {

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        user, null,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
                                );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ignored) {
                // 유효하지 않은 토큰이면 인증 없이 다음 필터로 (컨트롤러에서 401 처리)
            }
        }
        chain.doFilter(req, res);
    }
}