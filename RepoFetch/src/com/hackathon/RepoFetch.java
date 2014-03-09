package com.hackathon;
import com.hackathon.API;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.util.HashMap;

/**
 * Created by afoote97 on 3/8/14.
 */
public class RepoFetch {
    static API api;
    static SimpleRequestCallback scb = new SimpleRequestCallback();
    static DecreasingRequestCallback dcb = new DecreasingRequestCallback();
    static MongoClient mongoClient;
    static DB db;
    static DBCollection repos;

    public static void main(String[] args) throws Exception {
        String host;
        if (args.length > 0) {
            host = args[0];
        } else {
            host = "localhost";
        }
        api = new API(args[1]);

        try {
            mongoClient = new MongoClient(host, 27017);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        db = mongoClient.getDB("kare");
        if (db.getCollectionNames().contains("repos")) {
            db.createCollection("repos", null);
            repos = db.getCollection("repos");
            repos.createIndex(new BasicDBObject("name", 1));
            repos.createIndex(new BasicDBObject("star_count", 1));
        }
        //we can filter by stars for a while, minimize requests
        //manual ranges for the first ones


        fetchAllPages("search/repositories?q=stars:" + ">60000");//1700

        fetchAllPages("search/repositories?q=stars:" + "1000..1699");
        fetchAllPages("search/repositories?q=stars:" + "710..999");
        fetchAllPages("search/repositories?q=stars:" + "550..509");
        fetchAllPages("search/repositories?q=stars:" + "450..549");
        fetchAllPages("search/repositories?q=stars:" + "380..449");
        fetchAllPages("search/repositories?q=stars:" + "330..379");
        fetchAllPages("search/repositories?q=stars:" + "300..329");

        fetchAllPages("search/repositories?q=stars:" + "270..299");
        fetchAllPages("search/repositories?q=stars:" + "240..269");
        fetchAllPages("search/repositories?q=stars:" + "220..239");
        fetchAllPages("search/repositories?q=stars:" + "200..219");
        fetchAllPages("search/repositories?q=stars:" + "183..199");
        fetchAllPages("search/repositories?q=stars:" + "170..182");


        //random for loops w00t
        for (int i = 160; i>=130; i-=10) {
            fetchAllPages("search/repositories?q=stars:" + i + ".."+(i+9));
        }
        for (int i = 125; i>=90; i-=5) {
            fetchAllPages("search/repositories?q=stars:" + i + ".."+(i+4));
        }
        fetchAllPages("search/repositories?q=stars:" + "86..89");
        fetchAllPages("search/repositories?q=stars:" + "82..85");
        fetchAllPages("search/repositories?q=stars:" + "79..81");
        fetchAllPages("search/repositories?q=stars:" + "76..78");
        fetchAllPages("search/repositories?q=stars:" + "73..75");
        fetchAllPages("search/repositories?q=stars:" + "70..72");
        fetchAllPages("search/repositories?q=stars:" + "67..69");
        fetchAllPages("search/repositories?q=stars:" + "65..66");
        fetchAllPages("search/repositories?q=stars:" + "63..64");
        fetchAllPages("search/repositories?q=stars:" + "61..62");
        fetchAllPages("search/repositories?q=stars:" + "59..60");
        fetchAllPages("search/repositories?q=stars:" + "57..58");
        for (int i = 56; i<40; i--) {
            fetchAllPages("search/repositories?q=stars:" + i);
        }



        //the above method doesn't work anymore, so lets try a more complex one




        //api.get("search?created:2014-01-01..2014-02-01%20stars:"+"1000..1699"+"&per_page:100&page:1", scb);




    }

    public static void fetchAllPages(String base_url) {
        //10 pages of results
        for (int i = 1; i<=10; i++) {
            api.get(base_url+"&per_page=100&page="+i, scb);
        }
    }

}
