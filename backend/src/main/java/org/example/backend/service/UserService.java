package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String getUserId(@Nullable User user) {
        return user != null ? user.getId() : null;
    }

    public void saveUserIfNotExists(@NonNull User user) {
        userRepository.findByExternalId(user.getExternalId())
                .orElseGet(() -> userRepository.save(user));
    }

    public User getUserByExternalId(@NonNull String externalId) {
        return userRepository.findByExternalId(externalId).orElseThrow();
    }
}
