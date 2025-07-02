package com.qnp.pmp.service.impl;


import com.mongodb.client.MongoCollection;
import com.qnp.pmp.config.MongoDBConnection;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

public class UserServiceImpl implements UserService {
    private final MongoCollection<Document> usersCollection;

    public UserServiceImpl() {
        usersCollection = MongoDBConnection.getDatabase().getCollection("users");
    }

    @Override
    public void saveUser(User user) {
        Document document = new Document()
                .append("username", user.getUsername())
                .append("password", user.getPassword())
                .append("role", user.getRole())
                .append("status", user.getStatus())
                .append("email", user.getEmail())
                .append("phoneNumber", user.getPhoneNumber())
                .append("createdAt", LocalDateTime.now());

        usersCollection.insertOne(document);
    }

    @Override
    public User loginUser(String username, String password) {
        Document query = new Document("username", username);
        Document found = usersCollection.find(query).first();

        if (found != null) {
            String hashedPassword = found.getString("password");

            // So sánh password người dùng nhập với password đã hash
            if (BCrypt.checkpw(password, hashedPassword)) {
                User user = new User();
                user.setUsername(found.getString("username"));
                user.setPassword(null); // không trả lại mật khẩu
                user.setRole(found.getString("role"));
                user.setStatus(found.getString("status"));
                user.setEmail(found.getString("email"));
                user.setPhoneNumber(found.getString("phoneNumber"));
                return user;
            }
        }

        return null;
    }

    @Override
    public User getUser() {
      Document query=new Document("role",new Document("$ne","SUPERADMIN"));
      Document found = usersCollection.find(query).first();
      if (found != null) {
          User user = new User();
          user.setUsername(found.getString("username"));
          user.setPassword(null);
          user.setRole(found.getString("role"));
          user.setStatus(found.getString("status"));
          user.setEmail(found.getString("email"));
          user.setPhoneNumber(found.getString("phoneNumber"));
          return user;
      }
      return null;
    }
}
