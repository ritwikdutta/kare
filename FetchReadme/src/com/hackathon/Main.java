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
 * @since 02 2014
 */

public class Main {
    public static void main(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        long startTime = System.currentTimeMillis();

        ExecutorService exec = Executors.newFixedThreadPool(150);

        ConcurrentHashMap<String, AtomicInteger> repos = new ConcurrentHashMap<>();

        final String REPO_NAME = "etsy/AndroidStaggeredGrid";

        String initialData = exec.submit(new RepoRetriever(REPO_NAME)).get();

        JsonNode root = mapper.readTree(initialData);

        final int STARS_PAGES = (int) Math.ceil(root.path("stargazers_count").intValue() / 100.0);

        for (int i = 0; i < STARS_PAGES; i++) {
            StarRetriever retriever =
                    new StarRetriever(REPO_NAME, i, repos, exec);

            exec.submit(retriever);
        }

        exec.awaitTermination(STARS_PAGES * 4, TimeUnit.SECONDS);
        exec.shutdown();
        while (!exec.isTerminated())
            exec.awaitTermination(STARS_PAGES, TimeUnit.MINUTES);

        exec = Executors.newFixedThreadPool(150);

        PrintWriter out = new PrintWriter(new FileWriter(REPO_NAME.split("/")[1] + ".stars"));


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
        for (Map.Entry<Integer, String> entry : sorted.entrySet()) {
            out.println("With " + entry.getKey() + " stars found: " + entry.getValue());

            if (i++ > 100) {
                break;
            }
        }

        out.println();
        out.println("Corrected: ");

        i = 0;

        final TreeMap<Double, String> corrected = new TreeMap<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                int attempt = tryCompare(o1, o2);
                return attempt == 0 ? 1 : attempt;
            }

            private int tryCompare(Double o1, Double o2) {
                return (int) Math.round((o2 - o1) * (1 << 16));
            }
        });

        for (final Map.Entry<Integer, String> entry : sorted.entrySet()) {
            exec.submit(new Runnable() {
                private final ObjectMapper mapper = new ObjectMapper();
                private final double BASE = 1.4;

                @Override
                public void run() {
                    String json = Http.get("https://api.github.com/repos/" + entry.getValue());
                    JsonNode node = null;
                    try {
                        node = mapper.readTree(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    int stars = node.path("stargazers_count").intValue();

                    double log = Math.sqrt(stars);

                    synchronized (corrected) {
                        corrected.put(entry.getKey() / log, entry.getValue());
                        System.out.println("Finished finding corrected value for: " + entry.getValue());
                    }
                }
            });

            if (i++ > 100 || entry.getKey() < 10) {
                break;
            }
        }

        exec.awaitTermination(20, TimeUnit.SECONDS);
        exec.shutdown();
        while (!exec.isTerminated())
            exec.awaitTermination(STARS_PAGES, TimeUnit.MINUTES);

        for (Map.Entry<Double, String> entry : corrected.entrySet()) {
            out.println("With " + entry.getKey() + " score found: " + entry.getValue());
        }


        out.flush();
        out.close();

        System.out.println("Found a total of: " + sorted.size() + " repos.");
        System.out.println("Finished in: " + (System.currentTimeMillis() - startTime));
    }
}


class StarRetriever implements Runnable {
    private final static ObjectMapper mapper = new ObjectMapper();

    private final ExecutorService exec;
    private final ConcurrentHashMap<String, AtomicInteger> repos;
    private final String repo;
    private final int page;

    StarRetriever(String repo, int page, ConcurrentHashMap<String, AtomicInteger> repos, ExecutorService exec) {
        this.page = page;
        this.repo = repo;
        this.repos = repos;
        this.exec = exec;
    }

    private static final AtomicInteger userCount = new AtomicInteger();

    @Override
    public void run() {
        String url = "https://api.github.com/repos/" + repo + "/stargazers?per_page=100&page=" + page;
        String data = Http.get(url);

        try {
            JsonNode root = mapper.readTree(data);

            for (int i = 0; root.has(i) ; i++) {
                final String login = root.path(i).path("login").textValue();

                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        JsonNode root = null;
                        try {
                            root = mapper.readTree(Http.get("https://api.github.com/users/" + login + "/starred?per_page=100"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }


                        for (int i = 0; root.has(i); i++) {
                            String name = root.path(i).path("full_name").textValue();

                            repos.putIfAbsent(name, new AtomicInteger());

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

class RepoRetriever implements Callable<String> {
    private final String repo;

    RepoRetriever(String repo) {
        this.repo = repo;
    }


    @Override
    public String call() throws Exception {
        String url = "https://api.github.com/repos/" + repo;

        return Http.get(url);
    }
}


class Http {
    static String get(String urlStr) {
        try {
            if (!urlStr.contains("?")) {
                urlStr += "?";
            }

            if (!urlStr.endsWith("?")) {
                urlStr += "&";
            }

            urlStr += "client_id=449f08df6a7cd6d4f711&client_secret=2a2431ffcbf29d9b448d9800d7cd858745137f2c";

//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("210.101.131.232", 8080));
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Connection", "keep-alive");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            StringBuilder sb = new StringBuilder();

            while ((line = in.readLine()) != null)
                sb.append(line);
            in.close();


//            System.out.println(sb.toString());

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}