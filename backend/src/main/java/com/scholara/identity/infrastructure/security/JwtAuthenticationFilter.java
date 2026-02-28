package com.scholara.identity.infrastructure.security;

import com.scholara.identity.domain.exception.InvalidTokenException;
import com.scholara.identity.domain.exception.TokenExpiredException;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.repository.UserRepository;
import com.scholara.identity.domain.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that extracts JWT from cookies and sets up Spring Security context.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final CookieService cookieService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = cookieService.extractAccessToken(request).orElse(null);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                JwtTokenService.JwtClaims claims = jwtTokenService.validateToken(token);

                User user = userRepository.findById(claims.userId())
                        .orElse(null);

                if (user != null && user.isEnabled()) {
                    ScholaraPrincipal principal = new ScholaraPrincipal(user);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    principal.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (TokenExpiredException | InvalidTokenException e) {
                // Token invalid or expired.
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip filter for public endpoints to improve performance
        return path.startsWith("/v1/auth/register") ||
               path.startsWith("/v1/auth/login") ||
               path.startsWith("/v1/auth/verify-email") ||
               path.startsWith("/v1/auth/forgot-password") ||
               path.startsWith("/v1/auth/reset-password") ||
               path.startsWith("/oauth2/") ||
               path.startsWith("/login/oauth2/") ||
               path.startsWith("/actuator/health");
    }
}
