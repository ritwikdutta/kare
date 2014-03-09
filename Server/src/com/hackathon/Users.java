package com.hackathon;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Users extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q");
        //https://github.com/command_bar/users?q=ar
        URL url = new URL("https://github.com/command_bar/users?q=" + q);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String str = br.readLine();
            if (str == null) {
                break;
            }
            sb.append(str);
        }
        resp.setContentType("application/json");
        resp.getWriter().write(sb.toString());
    }
}
