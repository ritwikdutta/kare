package com.hackathon;

import com.mongodb.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Fetch {

    private static final String base = "https://raw.github.com/";
    //twbs/bootstrap/master/README.md
    public static void main(String[] args) throws UnknownHostException {


        ExecutorService exec = Executors.newFixedThreadPool(150);
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        DB db = mongoClient.getDB( "kare" );
        final DBCollection coll = db.getCollection("repos");
        final DBCursor cursor = coll.find();
        try {
            while (cursor.hasNext()) {
                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        BasicDBObject obj = (BasicDBObject) cursor.next();
                        String name = (String) obj.get("owner");
                        String repo = (String) obj.get("repo");
                        String masterBranch = (String) obj.get("master_branch");
                        String url = base + name + "/" + repo + "/" + masterBranch;
                        String readme = Http.get(url + "/README.md");
                        if (readme == null) {
                            readme = Http.get(url + "/README.txt");
                        }
                        if (readme == null) {
                            readme = Http.get(url + "/README");
                        }
                        if (readme == null) {
                            readme = Http.get(url + "/README.rst");
                        }

                        if (readme == null) {
                            readme = Http.get(url + "/README.txt");
                        }
                        if (readme != null) {
                            try {
                                obj.append("tags", Parser.getKeywords(readme + (String)obj.get("desc")));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        coll.save(obj);
                    }
                });

            }
        } finally {
            cursor.close();
        }
        exec.shutdown();
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
                return null;
            }
        }
    }
}


