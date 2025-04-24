package main.java.com.meditrack.user_service.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import main.java.com.meditrack.user_service.model.User;
import main.java.com.meditrack.user_service.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByFirebaseUid(String uid) {
        return userRepository.findByFirebaseUid(uid);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public User updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setFullName(updatedUser.getFullName());
                    existing.setSpecialty(updatedUser.getSpecialty());
                    existing.setPhoneNumber(updatedUser.getPhoneNumber());
                    return userRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
