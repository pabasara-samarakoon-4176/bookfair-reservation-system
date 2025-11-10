package com.bookfair.user.service;

import com.bookfair.user.controller.UserController.UpdateUserRequest;
import com.bookfair.user.model.User;
import com.bookfair.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Update user information (name and contact number)
     */
    public User updateUser(Integer userId, UpdateUserRequest request) {
        User user = getUserById(userId);

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }

        if (request.getContactNumber() != null && !request.getContactNumber().isEmpty()) {
            user.setContactNumber(request.getContactNumber());
        }

        return userRepository.save(user);
    }

    /**
     * Change user password
     */
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (oldPassword.equals(newPassword)) {
            throw new RuntimeException("New password cannot be the same as old password");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(Integer userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    /**
     * Get users by role
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get users by business ID
     */
    public List<User> getUsersByBusinessId(Integer businessId) {
        return userRepository.findByBusinessId(businessId);
    }

    /**
     * Update user role
     */
    public User updateUserRole(Integer userId, String role) {
        User user = getUserById(userId);
        user.setRole(role);
        return userRepository.save(user);
    }

    /**
     * Count total users
     */
    public long countUsers() {
        return userRepository.count();
    }
}
