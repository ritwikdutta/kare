package com.hackathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author arshsab
 * @since 03 2014
 */

public class API {
    private final String multiplexerUrl = "http://ec2-54-186-6-142.us-west-2.compute.amazonaws.com";
    private final ScheduledExecutorService exec = Executors.newScheduledThreadPool(100);

    private final ConcurrentHashMap<Long, Callback> callbacks = new ConcurrentHashMap<>();

    public void get(final String url, final Callback callback) {
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    String str = Http.get(url);

                    if (str == null) {
                        exec.schedule(this, 1, TimeUnit.SECONDS);
                    } else {
                        callback.onComplete(str);
                    }

                }
            });
    }

    public void get(final String url, final Callback callback, final String data) {
        exec.submit(new Runnable() {
            @Override
            public void run() {
                String str = Http.get(url);

                if (str == null) {
                    exec.schedule(this, 1, TimeUnit.SECONDS);
                } else {
                    callback.onComplete(str, data);
                }

            }
        });
    }



    static class Http {
        static String get(String url) {
            url = "https://api.github.com/" + url + (url.contains("?") ? "&" : "?") + "client_id=66c2fad412d93ec92a21" + "&client_secret=a77f513ae5dc9680f5ab1ea5448bd7720785eded";

            try {
                URLConnection conn = new URL(url).openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;

                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                return sb.toString();
            } catch (IOException e) {
                //e.printStackTrace();

                return null;
            }
        }
    }
}
