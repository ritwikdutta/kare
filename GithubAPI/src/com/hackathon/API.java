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
        static String[] keys = {
                "client_id=a945562432a7c488e34a&client_secret=ee5f9df1a7d7292e70d876df9837effb39391485",
                "client_id=0f3708fa0cdb64f9dcac&client_secret=ac67de883d650185c595ed2530934492e786f444",
                "client_id=66c2fad412d93ec92a21&client_secret=a77f513ae5dc9680f5ab1ea5448bd7720785eded",
                "client_id=479f343c5d4566c270f6&client_secret=6ffc9862ddd2366407361efc550bf28519334242",
                "access_token=0f4a4c47fb982267825aadf5cbf33e415b564c0c",
                "access_token=acae0225b7b987e20f9ceea0fb6e4cc8d2b57d4d",
                "access_token=ef322d982cb0fabb6c98a68694a4e097848be57e",
                "access_token=8a83c275f3362c04cf4c34c3ecc9765851bcbedf"
        };
        static int count;
        static String get(String url) {
            url = "https://api.github.com/" + url + (url.contains("?") ? "&" : "?") + keys[count];
            count = (count+1) % 8;
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
