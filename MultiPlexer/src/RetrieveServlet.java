import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author arshsab
 * @since 03 2014
 */

public class RetrieveServlet extends HttpServlet {
    private ConcurrentHashMap<Integer, String> map;

    @Override
    public void init() {
        map = (ConcurrentHashMap<Integer, String>) getServletContext().getAttribute("map");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");

        if (map.get(Integer.parseInt(id)) == null) {
            resp.getWriter().println("error");
        } else {
            for (String line : map.remove(Integer.parseInt(id)).split("\n")) {
                resp.getWriter().println(line);
            }
        }

        resp.getWriter().flush();
        resp.getWriter().close();
    }
}
