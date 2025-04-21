package com.meditrack.auth_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AdminTestController provides test endpoints to verify role-based access control.
 * These endpoints are protected and can only be accessed by users with the appropriate roles.
 */
@RestController
public class AdminTestController {

    /**
     * Endpoint that can only be accessed by users with the ADMIN role.
     * @return A success message if the user has ADMIN access
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "✅ You have ADMIN access!";
    }

    /**
     * Endpoint that can only be accessed by users with the DOCTOR role.
     * @return A success message if the user has DOCTOR access
     */
    @GetMapping("/doctor-only")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorEndpoint() {
        return "✅ You have DOCTOR access!";
    }
} 