package com.hackathon;
import com.hackathon.API;
import com.hackathon.DecreasingRequestCallback;
import com.hackathon.SimpleRequestCallback;

import java.util.HashMap;

/**
 * Created by afoote97 on 3/8/14.
 */
public class StarFetch {
    static MongoClient mongoClient;
    static DB db;
    static DBCollection stars;
    static DBCollection repos;
    static final String key = ""; //
    static API api = new API();
    static StarCallback cb = new StarCallback();

    public static void main(String[] args) throws Exception {
        try {
            mongoClient = new MongoClient("54.186.89.119", 27017);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        db = mongoClient.getDB("kare");
        if (!db.getCollectionNames().contains("stars")) {
            db.createCollection("stars", null);
            stars = db.getCollection("stars");
            stars.createIndex(new BasicDBObject("name", 1));
            stars.createIndex(new BasicDBObject("stargazer", 1));

        }

        stars = db.getCollection("stars");
        repos = db.getCollection("repos");

        DBCursor cursor = repos.find(new BasicDBObject("star_count", new BasicDBObject("$gt", 1000)));
        try {
            while (cursor.hasNext()) {
                DBObject repo = cursor.next();
                String url = "repos/" + repo.get("owner") + "/" + repo.get("name") + "/stargazers?per_page=100";
                int starcount =  ((Integer) repo.get("star_count")).intValue();
                //System.out.print(repo.get("name")+":");
                //System.out.println(starcount);
                int pages = starcount / 100;
                for (int i = 1; i<=pages+1; i++ ) {
                    api.get(url + "&page=" + i, cb, repo.get("owner") + "/" + repo.get("name"));
                }
            }
        } finally {
            cursor.close();
        }




    }





}
