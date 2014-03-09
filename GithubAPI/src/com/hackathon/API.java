package com.hackathon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.*;

/**
 * @author arshsab
 * @since 03 2014
 */

public class API {
    private final ExecutorService exec = Executors.newScheduledThreadPool(100);
    private final String key;

    public API(String key) {
        this.key = key;
    }

    public void get(final String url, final Callback callback) {
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        String before = url.contains("?") ? "&" : "?";

                        HttpURLConnection conn = (HttpURLConnection) new URL("https://api.github.com" + url + before + key).openConnection();

                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        String line;

                        StringBuilder sb = new StringBuilder();

                        while ((line = in.readLine()) != null)
                            sb.append(line);
                        in.close();

                        callback.onComplete(sb.toString());

                    } catch (Exception e) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }

                        exec.submit(this);
                    }
                }
            });
    }
}
