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
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/ws/**", "/ws/info/**", "/ws").permitAll()
                .antMatchers(HttpMethod.GET, "/ws/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/ws/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/places/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/zones/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/guidage/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .antMatchers(HttpMethod.POST, "/api/places/**").hasRole("SUPERVISEUR")
                .antMatchers(HttpMethod.PUT, "/api/places/**").hasRole("SUPERVISEUR")
                .antMatchers(HttpMethod.DELETE, "/api/places/**").hasRole("SUPERVISEUR")
                .antMatchers("/api/personnes/**").hasRole("SUPERVISEUR")
                .antMatchers("/api/services/**").hasRole("SUPERVISEUR")
                // Authenticated users can access reservations and vehicles
                .antMatchers(HttpMethod.GET, "/api/reservations-place/statut-carte").permitAll()
                .antMatchers("/simulation/**").hasRole("SUPERVISEUR")
                .antMatchers("/flux/**").permitAll()
                .antMatchers("/api/reservations-place/**").authenticated()
                .antMatchers("/api/reservations-service/**").authenticated()
                .antMatchers("/api/vehicules/**").authenticated()
                .antMatchers("/api/reservations-place/**").authenticated()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        //configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000", "http://172.31.253.157:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With",  "Accept", "Cache-Control", "Last-Event-ID"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
