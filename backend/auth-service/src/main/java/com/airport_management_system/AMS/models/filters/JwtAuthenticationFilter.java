package com.airport_management_system.AMS.models.filters;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.airport_management_system.AMS.models.dao.serviceImpl.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { 

    private final UserDetailsService userDetailsService; 
    private final JwtService jwtService; 

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) { 
        this.userDetailsService = userDetailsService; 
        this.jwtService = jwtService; 
    } 

    @Override 
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException { 

        String token = null; 
        String username = null; 
        String authHeader = request.getHeader("Authorization"); 

        if (authHeader != null && authHeader.startsWith("Bearer ")) { 
            token = authHeader.substring(7); 
            username = jwtService.extractUserName(token); 
        } 

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { 
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); 

            if (jwtService.validateToken(token)) { 
                UsernamePasswordAuthenticationToken authtoken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); 

                authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); 
                SecurityContextHolder.getContext().setAuthentication(authtoken); 
            } 
        } 

        filterChain.doFilter(request, response); 
    } 
}
