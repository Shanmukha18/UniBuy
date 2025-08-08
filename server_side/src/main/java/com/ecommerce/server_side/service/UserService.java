package com.ecommerce.server_side.service;

import com.ecommerce.server_side.dto.UserDTO;
import com.ecommerce.server_side.model.User;

import java.util.List;

public interface UserService {
    UserDTO registerUser(User user);
    UserDTO updateUser(Long id, User updatedUser);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
}
