package com.qnp.pmp.service.impl;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.qnp.pmp.dto.UserViewModel;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public void saveUser(User user) {

    }

    @Override
    public User loginUser(String username, String password) {

        return null;
    }

    @Override
    public User getUser() {

      return null;
    }

    @Override
    public List<UserViewModel> getUserByRoleUser() {

        return List.of();
    }
}
