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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author arshsab
 * @since 03 2014
 */

public class IncomingRequestProcesser extends HttpServlet {
    private final ExecutorService exec = Executors.newFixedThreadPool(30);

    private ArrayList<String> slaves;
    private ArrayList<Integer> requests;

    private ConcurrentHashMap<Integer, String> map;

    @Override
    @SuppressWarnings("ALL")
    public void init() {
        map = (ConcurrentHashMap<Integer, String>) getServletContext().getAttribute("map");
        slaves = (ArrayList<String>) getServletContext().getAttribute("slaves");
        requests = new ArrayList<Integer>();

        for (int i = 0; i < slaves.size(); i++) {
            requests.add(0);
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

        final int claim = ids.getAndIncrement();

        int i = 0;
        for (final String slave : slaves) {
            if (requests.get(i++) <= 0) {
                continue;
            }

            exec.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(slave + URLEncoder.encode("id", claim + "")
                                + URLEncoder.encode("what", what)).openConnection();

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

            break;
        }

        resp.setContentType("application/json");
        resp.getWriter().println(claim);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}