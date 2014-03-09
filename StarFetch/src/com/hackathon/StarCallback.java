package com.hackathon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.mongodb.*;

public class StarCallback implements Callback {
    static MongoClient mongoClient;
    static DB db;
    static DBCollection stars;
    static DBCollection repos;

    public StarCallback() {
        try {
            mongoClient = new MongoClient("54.186.89.119", 27017);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        db = mongoClient.getDB("kare");
        repos = db.getCollection("repos");
        stars = db.getCollection("stars");
    }

    @Override
    public void onComplete(String data) {

    }


    public void onComplete(String data, String repo) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        while (true) {
            if (rootNode.get(i) == null) {
                return;
            }

            JsonNode star = rootNode.get(i);

            BasicDBObject doc = new BasicDBObject("stargazer", star.path("login").textValue())
                                    .append("name", repo);
            stars.insert(doc);
            i++;
        }
    }
}
