package com.hackathon;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.Callback;
import com.mongodb.BasicDBObject;

import java.util.HashMap;

/**
 * Created by afoote97 on 3/8/14.
 */
public class DecreasingRequestCallback extends SimpleRequestCallback implements Callback {

    DecreasingRequestCallback() {
        super();
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
        long total = rootNode.get("total_count").asLong();
        System.out.println(total);
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
