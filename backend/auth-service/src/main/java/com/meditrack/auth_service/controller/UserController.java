package com.meditrack.auth_service.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;
import com.google.firebase.auth.UserRecord;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Setup a test admin user and get their token
     */
    @PostMapping("/setup-admin")
    public Map<String, String> setupAdmin() {
        try {
            // Create or get user
            UserRecord userRecord;
            try {
                userRecord = FirebaseAuth.getInstance().getUserByEmail("test@example.com");
            } catch (FirebaseAuthException e) {
                // Create new user if doesn't exist
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail("test@example.com")
                    .setPassword("123456")
                    .setEmailVerified(true);
                userRecord = FirebaseAuth.getInstance().createUser(request);
            }
            
            // Set admin role
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "ADMIN");
            FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);
            
            // Create a custom token with the admin role
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid(), claims);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Admin user setup complete");
            response.put("uid", userRecord.getUid());
            response.put("token", customToken);
            return response;
        } catch (FirebaseAuthException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * Get a test token (pro dev way)
     */
    @PostMapping("/get-test-token")
    public Map<String, String> getTestToken() {
        try {
            // Create or get user
            UserRecord userRecord;
            try {
                userRecord = FirebaseAuth.getInstance().getUserByEmail("test@example.com");
            } catch (FirebaseAuthException e) {
                // Create new user if doesn't exist
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail("test@example.com")
                    .setPassword("123456")
                    .setEmailVerified(true);
                userRecord = FirebaseAuth.getInstance().createUser(request);
            }
            
            // Set admin role
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "ADMIN");
            FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);
            
            // Create a custom token that can be exchanged for an ID token
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid(), claims);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", customToken);
            response.put("uid", userRecord.getUid());
            return response;
        } catch (FirebaseAuthException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * Debug endpoint to verify token
     */
    @PostMapping("/verify-token")
    public Map<String, Object> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("uid", decodedToken.getUid());
            response.put("email", decodedToken.getEmail());
            response.put("claims", decodedToken.getClaims());
            return response;
        } catch (FirebaseAuthException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
} 