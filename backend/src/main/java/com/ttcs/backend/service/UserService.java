package com.ttcs.backend.service;

import com.ttcs.backend.entity.User;
import com.ttcs.backend.repository.UserRepository;
import com.ttcs.backend.specification.UserSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getFilteredUsers(String email, String fullName, String phone, Long roleId, Pageable pageable) {
        Specification<User> spec = Specification.where(UserSpecs.withFetchRole())
                .and(UserSpecs.hasEmail(email))
                .and(UserSpecs.hasFullName(fullName))
                .and(UserSpecs.hasPhone(phone))
                .and(UserSpecs.hasRoleId(roleId));
        return userRepository.findAll(spec, pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {
        String normalizedEmail = user.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email nay da duoc su dung");
        }

        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        String normalizedEmail = userDetails.getEmail().trim().toLowerCase(Locale.ROOT);
        if (!user.getEmail().equals(normalizedEmail) && userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email nay da duoc su dung");
        }

        user.setEmail(normalizedEmail);
        user.setFullName(userDetails.getFullName());
        user.setPhone(userDetails.getPhone());
        user.setRole(userDetails.getRole());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
