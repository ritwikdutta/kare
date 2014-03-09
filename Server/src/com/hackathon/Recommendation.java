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
import java.util.concurrent.ExecutionException;

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
        System.out.println(coll.findOne());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> recs = null;
        try {
            Recommender.getRecommendations(req.getParameter("repo"), "access_token=71ad4d33a5df2eee6f81caa088eeeb047c7785aa");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        for (final String s : recs ) {
            sb.append(new ListViewTemplate().set(new HashMap<String, String>() {{
                put("href", "http://github.com/" + s);
                put("text", s);

            }}));
        }
        PrintWriter pw = resp.getWriter();
        pw.write(div(sb.toString()));
        pw.flush();
        pw.close();
    }
}
