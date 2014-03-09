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

        handler.setAttribute("access_token", "access_token=27e796664c69cfe02c36ba2342121a90b897e5f4");

        server.setHandler(handler);

        server.start();
        server.join();
    }
}
