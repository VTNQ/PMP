package com.qnp.pmp.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ConnectionString;
public class MongoDBConnection {
    private static final String URI="mongodb+srv://PMP:Pmp123@cluster0.ol8bkxp.mongodb.net/pmp_db?retryWrites=true&w=majority";
    private static MongoClient mongoClient;
    public static MongoDatabase getDatabase() {
        if(mongoClient == null) {
            mongoClient= MongoClients.create(URI);
        }
        return mongoClient.getDatabase("pesonal_management");
    }
}
