package DumbAndDumber.Danchive.api.config.security;

import DumbAndDumber.Danchive.api.entity.User;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
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
                String subject = jws.getBody().getSubject();
                Instant exp   = jws.getBody().getExpiration().toInstant();

                if (subject != null && subject.startsWith("guest:")) {
                    if (Instant.now().isBefore(exp)) {
                        var auth = new UsernamePasswordAuthenticationToken(
                                subject, null, List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } else {
                    Optional<User> ou = userRepository.findByEmail(subject);
                    if (ou.isPresent()) {
                        User user = ou.get();
                        if (accessToken.equals(user.getAccessToken())
                                && user.getAccessTokenExp() != null
                                && Instant.now().isBefore(user.getAccessTokenExp())
                                && Instant.now().isBefore(exp)) {
                            var auth = new UsernamePasswordAuthenticationToken(
                                    user.getId(), null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            } catch (Exception ignored) { /* invalid token -> pass */ }
        }
        chain.doFilter(req, res);
    }
}