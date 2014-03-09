package com.hackathon;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.*;

/**
 * Set up Jetty and start the server
 *
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Main {
    public static void main(String[] args) throws IOException {
        Mode m = Mode.PRODUCTION;
        if (args.length > 0) {
            m = Mode.DEV;
        }

        Server server = configureServer(m);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static Server configureServer(Mode m) {
        ServletContextHandler servletHandler =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/");
        servletHandler.addServlet(Users.class, "/users");

        // prevent urls like website.com/js to be listed explicitly
        servletHandler.setInitParameter(
                "org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        ResourceHandler staticHandler = new ResourceHandler();
        staticHandler.setWelcomeFiles(new String[] { "index.html" });
        staticHandler.setResourceBase("Frontend");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { staticHandler, servletHandler });

        Server s = new Server(m.equals(Mode.DEV) ? 8080 : 80);
        s.setHandler(handlers);
        return s;
    }


    private enum Mode {
        DEV,
        PRODUCTION
    }
}
