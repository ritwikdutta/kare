package com.hackathon;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * @author arshsab
 * @since 03 2014
 */

public class Main {
    public static void main(String... args) throws Exception {
        Server server = new Server(Integer.parseInt(args[0]));

        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(SlaveServlet.class, "/work");

        handler.setAttribute("access_token", args[1]);

        server.setHandler(handler);

        server.start();
        server.join();
    }
}
