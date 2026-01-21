package spring_masters.attendance_system.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring_masters.attendance_system.security.CustomAccessDeniedHandler;
import spring_masters.attendance_system.security.CustomAuthenticationEntryPoint;
import spring_masters.attendance_system.security.JwtAuthFilter;
import spring_masters.attendance_system.security.RateLimitFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(request -> "OPTIONS".equals(request.getMethod())).permitAll()
                        .requestMatchers("/api/weather", "/api/weather/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/subjects/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/analytics/**").authenticated()
                        .requestMatchers("/api/email/**").hasRole("TEACHER")
                        .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/api/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
