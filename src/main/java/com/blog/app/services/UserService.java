package com.blog.app.services;


import com.blog.app.entities.User;
import com.blog.app.services.dto.UserDTO;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(UserDTO userDTO);

    User createUser(String login, String password, String firstName, String lastName, String email, String imageUrl, String langKey);
}
