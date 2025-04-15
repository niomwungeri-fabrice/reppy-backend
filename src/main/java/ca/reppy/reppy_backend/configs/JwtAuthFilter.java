package ca.reppy.reppy_backend.configs;

import ca.reppy.reppy_backend.dtos.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthFilter extends GenericFilterBean {
    private final JwtService jwtService;

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/auths/register",
            "/auths/login",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // ✅ Skip JWT authentication for public endpoints
        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Validate Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(httpResponse, "Authorization header is missing or invalid");
            return;
        }

        // ✅ Validate JWT token
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtService.validateToken(token);

            String email = claims.getSubject(); // Subject (email) is extracted here

            if (email == null || email.isEmpty()) {
                sendErrorResponse(httpResponse, "Invalid token: No subject found");
                return;
            }
            // ✅ Ensure email is not null before creating UserDetails
            UserDetails userDetails = User.withUsername(email)
                    .password("") // Spring Security requires a non-null password
                    .authorities(List.of()) // No roles for now
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            sendErrorResponse(httpResponse, "Invalid or expired token");
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Forbidden")
                .message(message)
                .trace(message)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
