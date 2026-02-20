package intellijP.back.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // Public endpoints - authentication
                .antMatchers("/api/auth/**").permitAll()
                // WebSocket endpoints (including SockJS fallback)
                .antMatchers("/ws/**", "/ws/info/**", "/ws").permitAll()
                // Public endpoints - viewing places (visitors can see)
                .antMatchers(HttpMethod.GET, "/api/places/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/zones/**").permitAll()
                // Swagger/OpenAPI
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Admin only endpoints
                .antMatchers(HttpMethod.POST, "/api/places/**").hasRole("SUPERVISEUR")
                .antMatchers(HttpMethod.PUT, "/api/places/**").hasRole("SUPERVISEUR")
                .antMatchers(HttpMethod.DELETE, "/api/places/**").hasRole("SUPERVISEUR")
                .antMatchers("/api/personnes/**").hasRole("SUPERVISEUR")
                .antMatchers("/api/services/**").hasRole("SUPERVISEUR")
                // endpoints simulation R2 - superviseur uniquement
                .antMatchers("/simulation/**").hasRole("SUPERVISEUR")
                // flux SSE R2 - public
                .antMatchers("/flux/**").permitAll()
                // Authenticated users can access reservations and vehicles
                .antMatchers("/api/reservations-place/**").authenticated()
                .antMatchers("/api/reservations-service/**").authenticated()
                .antMatchers("/api/vehicules/**").authenticated()
                // All other requests need authentication
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000", "http://172.31.253.157:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
