package com.hackathon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.Callback;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.HashMap;

/**
 * Created by afoote97 on 3/8/14.
 */
public class SimpleRequestCallback implements Callback {
    MongoClient mongoClient;
    DB db;
    DBCollection repos;
    public SimpleRequestCallback() {
        try {
            mongoClient = new MongoClient("localhost", 27017);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        db = mongoClient.getDB("mydb");
        repos = db.getCollection("repos");
        repos.createIndex(new BasicDBObject("name", "1"));
        repos.createIndex(new BasicDBObject("star_count", "1"));

    }

    @Override
    public void onComplete(String data) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readValue(data, JsonNode.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonNode items = rootNode.get("items");
        for (int i = 0; i<100; i++) {
            final JsonNode repo = items.get(i);
            if (null == repo) {
                continue;
            }
            HashMap<String, String> map = new HashMap() {
                {
                    put("name", repo.get("name"));
                    put("owner", repo.get("owner").get("login"));
                    put("star_count", repo.get("stargazers_count"));
                    put("desc", repo.get("description"));
                    put("watcher_count", repo.get("watchers_count"));
                    put("fork_count", repo.get("forks_count"));
                    put("lang", repo.get("language"));
                    put("master_branch", repo.get("master_branch"));
                }
            };
            db.getCollection("repos").insert(new BasicDBObject(map));
        }


    }
}
