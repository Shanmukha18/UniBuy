package com.ecommerce.server_side.controller;

import com.ecommerce.server_side.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import com.ecommerce.server_side.model.User;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.server_side.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser);
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
