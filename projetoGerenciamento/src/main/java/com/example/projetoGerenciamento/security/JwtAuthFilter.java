package com.example.projetoGerenciamento.security;

import com.example.projetoGerenciamento.model.User;
import com.example.projetoGerenciamento.repository.UserRepository;
import com.example.projetoGerenciamento.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // gets the "Authorization" header from the request
        final String authHeader = request.getHeader("Authorization");

        // if there is no token, passes to the next filter without authenticating
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // removes the "Bearer " prefix, leaving only the token
        final String token = authHeader.substring(7);

        // extracts the email from the token
        final String email = jwtService.extractEmail(token);

        // if the email exists and the user is not yet authenticated in the context
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // fetches the user from the database by email
            User user = userRepository.findByEmail(email).orElse(null);

            // if the user exists and the token is valid, authenticates in Spring Security
            if (user != null && jwtService.isTokenValid(token, user)) {
                // creates the authentication object with the user and their permissions
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                // adds extra request details (IP, session) to the authentication object
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // registers the user in the context — from here Spring Security knows who is logged in
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // continues the filter chain
        filterChain.doFilter(request, response);
    }
}