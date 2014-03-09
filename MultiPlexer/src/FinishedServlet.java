import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author arshsab
 * @since 03 2014
 */

public class FinishedServlet extends HttpServlet {
    private ConcurrentHashMap<Integer, String> map;

    @Override
    public void init() {
        map = (ConcurrentHashMap<Integer, String>) getServletContext().getAttribute("map");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = -1;
        String it = null;
        for (String str : req.getParameterMap().keySet()) {
            if (str.equals("id")) {
                id = Integer.parseInt(req.getParameter("id"));
            } else {
                it = str;
            }
        }

        map.put(id, it);
    }
}
