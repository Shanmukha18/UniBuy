package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import com.ecommerce.server_side.model.User;
import org.springframework.stereotype.Service;
import com.ecommerce.server_side.repository.UserRepository;
import com.ecommerce.server_side.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserDTO registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        user.setRole("USER");
        user.setEnabled(true);
        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    @Override
    public UserDTO updateUser(Long id, User updatedUser) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        User saved = userRepository.save(existing);
        return mapToDTO(saved);
    }


    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
