package com.hackathon;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author arshsab
 * @since 03 2014
 */

public class IncomingRequestProcesser extends HttpServlet {
    private final ScheduledExecutorService exec = Executors.newScheduledThreadPool(30);

    private ArrayList<String> slaves;
    private ArrayList<AtomicInteger> requests;
    private ArrayList<AtomicInteger> searches;
    private ArrayList<AtomicLong> times;
    private ArrayList<AtomicLong> timesS;
    private String me;

    private ConcurrentHashMap<Integer, String> map;

    @Override
    @SuppressWarnings("ALL")
    public void init() {
        map = (ConcurrentHashMap<Integer, String>) getServletContext().getAttribute("map");
        me = (String) getServletContext().getAttribute("me");
        slaves = (ArrayList<String>) getServletContext().getAttribute("slaves");
        requests = new ArrayList<AtomicInteger>();
        searches = new ArrayList<AtomicInteger>();
        times = new ArrayList<AtomicLong>();
        timesS = new ArrayList<AtomicLong>();

        for (int i = 0; i < slaves.size(); i++) {
            requests.add(new AtomicInteger(5000));
            times.add(new AtomicLong(System.currentTimeMillis() + 3600 * 1000));
        }

        for (int i = 0; i < slaves.size(); i++) {
            searches.add(new AtomicInteger(60));
            timesS.add(new AtomicLong(System.currentTimeMillis() + 60 * 1000));
        }

        for (int i = 0; i < slaves.size(); i++) {
            if (!slaves.get(i).endsWith("/")) {
                slaves.set(i, slaves.get(i) + "/");
            }

        }
    }

    private final AtomicInteger ids = new AtomicInteger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String what = req.getParameter("what");
        final boolean search = what.startsWith("search") || what.startsWith("/search");

        final int claim = ids.getAndIncrement();


        exec.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (getImpl(what, search, claim)) {
                        break;
                    } else {
                        exec.schedule(this, 1, TimeUnit.SECONDS);
                    }
                }
            }
        });


        resp.setContentType("application/json");
        resp.getWriter().println(claim);
        resp.getWriter().flush();
        resp.getWriter().close();
    }


    boolean getImpl(final String what, boolean search, final long id) {
        int i = -1;
        for (final String slave : slaves) {
            i++;
            synchronized (this) {

                if (!search) {


                    if (times.get(i).get() < System.currentTimeMillis()) {
                        times.set(i, new AtomicLong(times.get(i).get() + 3600 * 1000));
                        requests.set(i, new AtomicInteger(5000));
                    }

                    if (requests.get(i).getAndDecrement() <= 0) {
                        continue;
                    }
                } else {
                    if (timesS.get(i).get() < System.currentTimeMillis()) {
                        timesS.get(i).set(timesS.get(i).get() + 60 * 1000);
                        searches.set(i, new AtomicInteger(5000));
                    }

                    if (searches.get(i).getAndDecrement() <= 0) {
                        continue;
                    }
                }
            }


            exec.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(slave + "work?id=" + id
                                + "&what=" + URLEncoder.encode(what, "UTF-8")
                                + "&callback=" + URLEncoder.encode(me, "UTF-8")).openConnection();

                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        String line;

                        StringBuilder sb = new StringBuilder();

                        while ((line = in.readLine()) != null)
                            sb.append(line);
                        in.close();

                        //ignore.
                        sb.toString();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            return true;
        }

        return false;
    }
}
