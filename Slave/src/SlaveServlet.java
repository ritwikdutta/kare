import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        final String callbackServer = req.getParameter("callback");
        final String url = req.getParameter("url");
        final String accessToken = this.accessToken;
        final String id = req.getParameter("id");

        exec.submit(new Runnable() {
            @Override
            public void run() {
                String result = null;

                // Fetch the info.

                try {
                    final String newUrl = url.contains("?") ? url + "&" + accessToken : url + "?" + accessToken;

                    URL url2 = new URL(newUrl);

                    URLConnection conn = url2.openConnection();

                    conn.setRequestProperty("Connection", "keep-alive");

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

                }



            }

            private void post(String id, String what, String server) throws IOException {
                URL url = new URL(server);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");


//                String type = "application/x-www-form-urlencoded";
//                String encodedData = URLEncoder.encode( rawData, "UTF-8" );
//                URL u = new URL("http://www.example.com/page.php");
//                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//                conn.setDoOutput(true);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty( "Content-Type", type );
//                conn.setRequestProperty( "Content-Length", String.valueOf(encodedData.length()));
//                OutputStream os = conn.getOutputStream();
//                os.write(encodedData.getBytes());
            }



        });


    }
}
