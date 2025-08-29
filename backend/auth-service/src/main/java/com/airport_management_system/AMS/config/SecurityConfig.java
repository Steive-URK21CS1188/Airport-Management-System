package com.airport_management_system.AMS.config;

import com.airport_management_system.AMS.models.dao.serviceImpl.JwtService;
import com.airport_management_system.AMS.models.filters.JwtAuthenticationFilter;
import com.airport_management_system.AMS.models.dao.serviceImpl.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService,
                                                           JwtService jwtService) {
        return new JwtAuthenticationFilter(customUserDetailsService, jwtService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login", "/api/users/register","/api/users/Validatetoken").permitAll()
                /*.requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/manager/**").hasAuthority("MANAGER")
                .requestMatchers("/api/planes/**").hasAnyAuthority("ADMIN", "MANAGER")
                .requestMatchers("/api/owner/**").hasAnyAuthority("ADMIN", "MANAGER")
                .requestMatchers("/api/pilots/**").hasAnyAuthority("ADMIN", "MANAGER")
                .requestMatchers("/api/hangars/**").hasAnyAuthority("ADMIN", "MANAGER")
                .requestMatchers("/api/hangar-allocation/**").hasAnyAuthority("ADMIN", "MANAGER")
                .requestMatchers("/api/plane-allocation/**").hasAnyAuthority("ADMIN", "MANAGER")
                .requestMatchers("/api/address/**").hasAnyAuthority("ADMIN", "MANAGER")*/
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
