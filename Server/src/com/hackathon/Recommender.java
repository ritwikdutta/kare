package com.hackathon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author arshsab
 * @since 03 2014
 */

public class Recommender {
    public static void main(String... args) throws InterruptedException, ExecutionException, IOException {
        List<String> recommendations = getRecommendations("jquery/jquery", "access_token=71ad4d33a5df2eee6f81caa088eeeb047c7785aa");

        System.out.println(recommendations);
    }

    public static synchronized List<String> getRecommendations(final String REPO_NAME, final String token) throws ExecutionException, InterruptedException, IOException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1 << 13);

        ThreadPoolExecutor exec = new ThreadPoolExecutor(150, 150, 1000, TimeUnit.SECONDS, queue);
        ConcurrentHashMap<String, AtomicInteger> repos = new ConcurrentHashMap<>();

        String str = HttpUtil.get("https://api.github.com/repos/" + REPO_NAME, token);

        JsonNode node = mapper.readTree(str);

        int STAR_GAZERS = node.path("stargazers_count").intValue();

        ConcurrentHashMap<String, Integer> starCounts = new ConcurrentHashMap<>();

        StarRetriever retriever =
            new StarRetriever(REPO_NAME, STAR_GAZERS > 100 ? (int) Math.round(STAR_GAZERS / 100.0) : 1, repos, starCounts, exec, token);

        exec.submit(retriever);


        while (true) {
            if (exec.getActiveCount() == 0) {
                Thread.sleep(100);
                if (exec.getActiveCount() == 0) {
                    break;
                }
            } else {
                Thread.sleep(100);
            }
        }

        TreeMap<Integer, String> sorted = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 - o2 != 0) {
                    return o2 - o1;
                }

                return 1;
            }
        });


        for (Map.Entry<String, AtomicInteger> entry : repos.entrySet()) {
            String name = entry.getKey();
            int score = entry.getValue().get();

            sorted.put(score, name);
        }

        int i = 0;

        final TreeMap<Double, String> corrected = new TreeMap<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                double attempt = tryCompare(o1, o2);

                return Math.round(attempt) == 0.0 ? 1 : (int) attempt;
            }

            public double tryCompare(Double o1, Double o2) {
                return (o2 - o1) * 1000;
            }
        });

        for (final Map.Entry<Integer, String> entry : sorted.entrySet()) {
            corrected.put(entry.getKey() / Math.pow(starCounts.get(entry.getValue()), 1.0 / 3.0), entry.getValue());

            if (i++ > 100) {
                break;
            }
        }

        while (true) {
            if (exec.getActiveCount() == 0) {
                Thread.sleep(100);
                if (exec.getActiveCount() == 0) {
                    break;
                }
            } else {
                Thread.sleep(100);
            }
        }

        List<String> ret = new ArrayList<>();

        System.out.println(corrected);
        System.out.println();
        System.out.println(starCounts);
        System.out.println();

        for (String s : corrected.values()) {
            ret.add(s);
        }

        ret.remove(0);

        return ret;
    }
}


class StarRetriever implements Runnable {
    private final static ObjectMapper mapper = new ObjectMapper();

    private final ExecutorService exec;
    private final ConcurrentHashMap<String, AtomicInteger> repos;
    private final ConcurrentHashMap<String, Integer> starCounts;
    private final String repo;
    private final int page;
    private final String token;

    StarRetriever(String repo, int page, ConcurrentHashMap<String, AtomicInteger> repos, ConcurrentHashMap<String, Integer> starCounts, ExecutorService exec, String token) {
        this.page = page;
        this.repo = repo;
        this.repos = repos;
        this.exec = exec;
        this.token = token;
        this.starCounts = starCounts;
    }

    private static final AtomicInteger userCount = new AtomicInteger();

    @Override
    public void run() {
        String url = "https://api.github.com/repos/" + repo + "/stargazers?per_page=100&page=" + page;
        String data = HttpUtil.get(url, token);

        try {
            JsonNode root = mapper.readTree(data);

            for (int i = 0; root.has(i) ; i++) {
                final String login = root.path(i).path("login").textValue();

                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        JsonNode root = null;
                        try {
                            root = mapper.readTree(HttpUtil.get("https://api.github.com/users/" + login + "/starred?per_page=100", token));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }


                        for (int i = 0; root.has(i); i++) {
                            String name = root.path(i).path("full_name").textValue();

                            repos.putIfAbsent(name, new AtomicInteger());

                            starCounts.putIfAbsent(name, root.path(i).path("stargazers_count").asInt());

                            repos.get(name).getAndIncrement();

                        }

                        System.out.println("Finished user #" + userCount.getAndIncrement() + ": " + login);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished with page: " + page);


    }
}






class HttpUtil {
    static String get(String urlStr, String token) {
        try {
            if (!urlStr.contains("?")) {
                urlStr += "?";
            }

            if (!urlStr.endsWith("?")) {
                urlStr += "&";
            }

            urlStr += token;

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