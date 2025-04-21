package com.meditrack.auth_service.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * FirebaseAuthFilter is a security filter that intercepts incoming requests
 * and validates Firebase ID tokens. It extracts the token from the Authorization
 * header, verifies it with Firebase, and sets up the Spring Security context
 * with the authenticated user's information.
 */
@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Get the Authorization header from the request
        String header = request.getHeader("Authorization");

        // If no Authorization header or not Bearer token, continue without authentication
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token from the Authorization header
        String token = header.replace("Bearer ", "");

        try {
            // Verify the Firebase ID token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            
            // Extract the role claim from the token
            String role = (String) decodedToken.getClaims().get("role");

            // Create a list of authorities based on the role
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role)
            );

            // Create an authentication token with the user's UID and authorities
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(decodedToken.getUid(), null, authorities);

            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            // If token verification fails, return 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
} 