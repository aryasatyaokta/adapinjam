package id.co.bcaf.adapinjam.securitys;

import id.co.bcaf.adapinjam.services.TokenBlacklistService;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtFilter(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String authHeader = httpRequest.getHeader("Authorization");

        // Bypass filter untuk endpoint login
        if (httpRequest.getRequestURI().startsWith("/api/v1/users")
                || httpRequest.getRequestURI().startsWith("/api/v1/auth/login")
                || httpRequest.getRequestURI().startsWith("/api/v1/auth/register-customer")
                || httpRequest.getRequestURI().startsWith("/api/v1/auth/forgot-password")
                || httpRequest.getRequestURI().startsWith("/api/v1/auth/reset-password")
                || httpRequest.getRequestURI().startsWith("/api/v1/auth/login-employee")
                || httpRequest.getRequestURI().startsWith("/api/v1/reset-password/employee")
                || httpRequest.getRequestURI().startsWith("/api/v1/coba/test")
                || httpRequest.getRequestURI().startsWith("/api/v1/reset-password/employee/reset/{token}")) {
            chain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
            return;
        }

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            if (email == null || !jwtUtil.validateToken(token, email)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            UserDetails userDetails = new User(email, "", Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token format");
            return;
        }

        chain.doFilter(request, response);
    }
}
