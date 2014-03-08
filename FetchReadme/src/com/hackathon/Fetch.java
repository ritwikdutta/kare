package com.hackathon;

import com.mongodb.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Fetch {

    private static final String base = "https://raw.github.com/";
    //twbs/bootstrap/master/README.md
    public static void main(String[] args) throws UnknownHostException {

        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        DB db = mongoClient.getDB( "kare" );
        DBCollection coll = db.getCollection("repos");
        DBCursor cursor = coll.find();
        try {
            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                String name = (String) obj.get("name");
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
                    obj.append("tags", Parser.getKeywords(readme));
                }
                coll.save(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
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


