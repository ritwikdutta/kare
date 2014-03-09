package com.hackathon;

import com.mongodb.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ritwik on 3/8/14.
 */
public class Relation {
    private final static double threshold = 0.5D;
    private final static String fieldName = "related_repos_readme";
    public static double readRelate(ArrayList<String> tags1, ArrayList<String> tags2) throws IOException {
        int count = 0;
        for (String tag: tags1) {
            if (tags2.contains(tag))  {
                count++;
            }
        }
        return (double) (count / (tags1.size() + tags2.size()));
    }

    public static double getDatabaseRelate() throws IOException {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("kare");
        final DBCollection coll = db.getCollection("repos");
        final DBCursor cursor = coll.find();
        try {
            while (cursor.hasNext()) {
                DBCursor cursor2 = coll.find();
                try {
                    BasicDBObject obj1 = (BasicDBObject) cursor.next();
                    if (!obj1.containsField(fieldName)) {
                        obj1.append(fieldName, new HashMap<String, Double>());
                    }
                    while (cursor2.hasNext()) {
                        BasicDBObject obj2 = (BasicDBObject) cursor2.next();
                        if (!obj2.containsField(fieldName)) {
                            obj2.append(fieldName, new HashMap<String, Double>());
                        }
                        double score = readRelate((ArrayList<String>)obj1.get("tags"), (ArrayList<String>)obj2.get("tags"));
                        if (score > threshold) {
                            HashMap<String, Double> map1 = (HashMap<String, Double>) obj1.get(fieldName);
                            HashMap<String, Double> map2 = (HashMap<String, Double>) obj2.get(fieldName);
                            map1.put((String)obj2.get("owner") + (String)obj2.get("name"), score);
                            map2.put((String)obj1.get("owner") + (String)obj1.get("name"), score);
                            obj1.replace(fieldName, map1);
                            obj2.replace(fieldName, map2);
                            coll.save(obj1);
                            coll.save(obj2);
                        }
                    }
                } finally {
                    cursor2.close();
                }
            }
        } finally {
            cursor.close();
        }
        return 0.0;
    }
}
