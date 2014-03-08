import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.HashMap;

/**
 * Created by afoote97 on 3/8/14.
 */
public class RequestCallback implements Callback {
    MongoClient mongoClient;
    DB db;
    DBCollection repos;
    RequestCallback() {
        try {
            mongoClient = new MongoClient("localhost", 27017);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        db = mongoClient.getDB("mydb");
        if (db.getCollectionNames().contains("repos")) {
            db.createCollection("repos", null);
        }
        repos = db.getCollection("repos");

    }
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
