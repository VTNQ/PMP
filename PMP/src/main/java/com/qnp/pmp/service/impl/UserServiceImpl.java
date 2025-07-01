package com.qnp.pmp.service.impl;


import com.mongodb.client.MongoCollection;
import com.qnp.pmp.config.MongoDBConnection;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import org.bson.Document;

public class UserServiceImpl implements UserService {
    private final MongoCollection<Document> usersCollection;
    public UserServiceImpl(){
        usersCollection= MongoDBConnection.getDatabase().getCollection("users");
    }
    @Override
    public void saveUser(User user) {
        Document document = new Document()
                .append("username", user.getUsername())
                .append("password", user.getPassword());
        usersCollection.insertOne(document);
    }
}
