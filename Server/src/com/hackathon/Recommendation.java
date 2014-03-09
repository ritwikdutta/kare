package com.hackathon;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.hackathon.Tags.*;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Recommendation extends HttpServlet {

    private DBCollection coll;

    @Override
    public void init() throws ServletException {
        coll = (DBCollection)getServletContext().getAttribute("correlations");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String repo = req.getParameter("repo");
        DBCursor c = coll.find(new BasicDBObject("name", repo));
        ArrayList<BasicDBObject> recs = new ArrayList<>();
        try {
            while (c.hasNext()) {
                BasicDBObject next = (BasicDBObject) c.next();
                recs.add(next);
            }
        } finally {
            c.close();
        }
        Arrays.sort(recs.toArray(), new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                BasicDBObject db1 = (BasicDBObject)o1;
                BasicDBObject db2 = (BasicDBObject)o2;
                return (int)((double)db1.get("score") - (double)db2.get("score"));

            }
        });
        StringBuilder sb = new StringBuilder();
        for (final BasicDBObject o : recs) {
            sb.append(new ListViewTemplate().set(new HashMap<String, String>() {{
                put("$href", "http://github.com/" + o.get("name"));
                put("$text", (String)o.get("name"));
            }}));
        }
        PrintWriter pw = resp.getWriter();

        pw.write(div(sb.toString()));
        pw.flush();
        pw.close();
    }
}
