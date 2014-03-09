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
    static API api = new API();
    static SimpleRequestCallback scb = new SimpleRequestCallback();
    static DecreasingRequestCallback dcb = new DecreasingRequestCallback();
    static MongoClient mongoClient;
    static DB db;

    public static void main(String[] args) throws Exception {

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
        //we can filter by stars for a while, minimize requests
        //manual ranges for the first ones

        //yolo
        fetchAllPages("search/repositories?stars:" + ">1700");
        /*fetchAllPages("search/repositories?stars:" + "1000..1699");
        fetchAllPages("search/repositories?stars:" + "710..999");
        fetchAllPages("search/repositories?stars:" + "550..509");
        fetchAllPages("search/repositories?stars:" + "450..549");
        fetchAllPages("search/repositories?stars:" + "380..449");
        fetchAllPages("search/repositories?stars:" + "330..379");
        fetchAllPages("search/repositories?stars:" + "300..329");

        fetchAllPages("search/repositories?stars:" + "270..299");
        fetchAllPages("search/repositories?stars:" + "240..269");
        fetchAllPages("search/repositories?stars:" + "220..239");
        fetchAllPages("search/repositories?stars:" + "200..219");
        fetchAllPages("search/repositories?stars:" + "183..199");
        fetchAllPages("search/repositories?stars:" + "170..182");


        //random for loops w00t
        for (int i = 160; i>=130; i-=10) {
            fetchAllPages("search/repositories?stars:" + i + ".."+(i+9));
        }






        //the above method doesn't work anymore, so lets try a more complex one




        api.get("search?created:2014-01-01..2014-02-01%20stars:"+"1000..1699"+"&per_page:100&page:1", scb);


    */

    }

    public static void fetchAllPages(String base_url) {
        //10 pages of results
        for (int i = 1; i<=10; i++) {
            api.get(base_url+"&per_page:100&page:"+i, scb);
        }
    }

}
