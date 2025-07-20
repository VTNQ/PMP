package com.qnp.pmp.service;

import com.qnp.pmp.dto.UserViewDTO;
import com.qnp.pmp.dto.UserViewModel;
import com.qnp.pmp.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);
    void createAdmin(User user);
    User loginUser(String username, String password);
    User getUser();
    List<UserViewDTO> getUserByRoleAdmin();
    List<UserViewDTO> getUserByRoleUser();
}
