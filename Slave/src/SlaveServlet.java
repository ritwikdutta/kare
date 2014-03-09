import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author arshsab
 * @since 03 2014
 */

public class SlaveServlet extends HttpServlet {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final ExecutorService exec = Executors.newFixedThreadPool(150);
    private String accessToken;

    @Override
    public void init() {
        accessToken = (String) getServletContext().getAttribute("token");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        final String callbackServer = req.getParameter("callback");
        final String url = req.getParameter("what");
        final String accessToken = this.accessToken;
        final String id = req.getParameter("id");

        exec.submit(new Runnable() {
            @Override
            public void run() {
                String result = null;

                // Fetch the info.

                try {
                    final String newUrl = "https://api.github.com" + (url.startsWith("/") ? "" : "/") + url +
                            (url.contains("?") ? "&" + accessToken : "?" + accessToken);

                    URL url2 = new URL(newUrl);

                    URLConnection conn = url2.openConnection();


                    conn.setRequestProperty("Connection", "keep-alive");
                    conn.setConnectTimeout(30 * 1000);

                    conn.connect();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String line;

                    StringBuilder sb = new StringBuilder();

                    while ((line = in.readLine()) != null)
                        sb.append(line);
                    in.close();

                    result = sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (result == null) {
                    try {
                        post(id, "error", callbackServer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        post(id, result, callbackServer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void post(String id, String what, String server) throws IOException {
                URL url = new URL(server + (server.endsWith("/") ? "" : "/") + "finished?id=" + id);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.connect();

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(what);
                wr.flush();
                wr.close();

                conn.getInputStream();
            }
        });
    }
}
