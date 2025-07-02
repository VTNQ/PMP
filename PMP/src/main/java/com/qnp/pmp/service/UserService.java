package com.qnp.pmp.service;

import com.qnp.pmp.entity.User;

public interface UserService {
    void saveUser(User user);
    User loginUser(String username, String password);
}
