package com.qnp.pmp.service;

import com.qnp.pmp.dto.UserViewModel;
import com.qnp.pmp.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);
    User loginUser(String username, String password);
    User getUser();
    List<UserViewModel> getUserByRoleUser();
}
