package com.hackathon;

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
 * @author Ritwik Dutta
 * @since 0.0.1
 */

public class Recommendation extends HttpServlet {

    private String[] accessTokens = new String[] {"6f83e3598e620bf5f55d676124e7f0b92de84ab8", "12f2e41bf7e1f3e695ca2e801a18d4f5561a8905", "456c2e2224a028da58d60f1f6272b478097bc4c2","f0d129eb9e7dc390781d6346bc5da5b1f3a7496c"};
    private int currentToken = 0;

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> recs = null;
        try {
            if (currentToken == accessTokens.length) {
                currentToken = 0;
            }
            System.out.println("Current access token: " + accessTokens[currentToken]);
            recs = Recommender.getRecommendations(req.getParameter("repo"), "access_token=" + accessTokens[currentToken]);
            currentToken++;

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
