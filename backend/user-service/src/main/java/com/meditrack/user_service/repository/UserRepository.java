package com.meditrack.user_service.repository;

import com.meditrack.user_service.model.User;
import com.meditrack.user_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByFirebaseUid(String firebaseUid);
    List<User> findByRole(Role role);
}