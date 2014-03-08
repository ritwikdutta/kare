package com.hackathon;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Fetch {
    public static void main(String[] args) throws UnknownHostException {

        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        DB db = mongoClient.getDB( "kare" );
        DBCollection coll = db.getCollection("repos");
        DBCursor cursor = coll.find();
        try {
            while (cursor.hasNext()) {

            }
        } finally {
            cursor.close();
        }
        ExecutorService exec = Executors.newFixedThreadPool(150);
        exec.submit(new Runnable() {
            @Override
            public void run() {
                String s = Http.get("https://raw.github.com/twbs/bootstrap/master/README.md");
            }
        });
    }


    static class Http {
        static String get(String urlStr) {
            try {
                URL url = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Connection", "keep-alive");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;

                StringBuilder sb = new StringBuilder();

                while ((line = in.readLine()) != null)
                    sb.append(line);
                in.close();
                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}


