package com.hackathon;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Repos extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q").replace(" ", "+");
        //https://github.com/command_bar/users?q=ar
        resp.setContentType("application/json");
        resp.getWriter().write(Http.read("https://github.com/command_bar/repos_for/" + q));
    }
}
