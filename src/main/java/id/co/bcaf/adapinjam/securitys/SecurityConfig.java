package id.co.bcaf.adapinjam.securitys;

import id.co.bcaf.adapinjam.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login").permitAll() // Buka akses login
                        .requestMatchers("/api/v1/users").permitAll()
                        .requestMatchers("/api/v1/user-employee").permitAll()
                        .requestMatchers("/api/v1/user-employee/add").permitAll()
                        .requestMatchers("/api/v1/auth/login-employee").permitAll()
                        .requestMatchers("/api/v1/auth/update-password").permitAll()
                        .requestMatchers("/api/v1/plafon").permitAll()
                        .requestMatchers("/api/v1/auth/register-customer").permitAll()
                        .requestMatchers("/api/v1/customer/check-profile").permitAll()
                        .requestMatchers("/api/v1/customer/add-customer-details").permitAll()
                        .anyRequest().authenticated()  // Endpoint lain harus pakai token
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
