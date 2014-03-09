import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private final String multiplexerUrl = "http://localhost:8080";
    private final ScheduledExecutorService exec = Executors.newScheduledThreadPool(100);

    private final ConcurrentHashMap<Long, Callback> callbacks = new ConcurrentHashMap<>();

    public void get(final String url, final Callback callback) {
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(multiplexerUrl + "/incoming?what=" + URLEncoder.encode(url, "UTF-8")).openConnection();

                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        String line;

                        StringBuilder sb = new StringBuilder();

                        while ((line = in.readLine()) != null)
                            sb.append(line);
                        in.close();

                        final long id = Long.parseLong(sb.toString());

                        callbacks.put(id, callback);

                        exec.schedule(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    HttpURLConnection conn = (HttpURLConnection) new URL(multiplexerUrl + "/retrieve" + "?id=" + URLEncoder.encode(id + "", "UTF-8")).openConnection();

                                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                                    String line;

                                    StringBuilder sb = new StringBuilder();

                                    while ((line = in.readLine()) != null)
                                        sb.append(line);
                                    in.close();

                                    if (sb.toString().equals("error")) {
                                        exec.schedule(this, 1, TimeUnit.SECONDS);
                                    } else {
                                        callbacks.get(id).onComplete(sb.toString());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 1, TimeUnit.SECONDS);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });




    }
}
