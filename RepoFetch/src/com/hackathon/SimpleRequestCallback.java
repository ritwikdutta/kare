package com.hackathon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.Callback;
import com.mongodb.*;

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
        db = mongoClient.getDB("kare");
        repos = db.getCollection("repos");


    }

    @Override
    public void onComplete(String data) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonNode items = rootNode.path("items");
        for (int i = 0; i<100; i++) {
            final JsonNode repo = items.get(i);
            if (null == repo) {
                return;
            }

            // todo : change get to path

            BasicDBObject doc = new BasicDBObject("name", repo.path("name").textValue())
                                    .append("owner", repo.get("owner").path("login").textValue())
                                    .append("star_count", repo.path("stargazers_count").intValue())
                                    .append("desc", repo.path("description").textValue())
                                    .append("watcher_count", repo.path("watchers_count").intValue())
                                    .append("fork_count", repo.path("forks_count").intValue())
                                    .append("lang", repo.path("language").textValue())
                                    .append("master_branch", repo.path("master_branch").textValue());
            //System.out.println(doc);
            repos.insert(doc);


        }
        return;


    }
}
