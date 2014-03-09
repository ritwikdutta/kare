package com.hackathon;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author arshsab
 * @since 03 2014
 */

public class Main {
    public static void main(String... args) throws Exception {
        Properties props = new Properties();

        props.load(new FileInputStream("slaves.properties"));

        ArrayList<String> slaveServers = new ArrayList<String>();

        for (int i = 1; ; i++) {
            String server = props.getProperty("server." + i);

            if (server != null)
                slaveServers.add(server);
            else
                break;
        }


        Server server = new Server(Integer.parseInt(args[0]));

        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(FinishedServlet.class, "/finished");
        handler.addServlet(IncomingRequestProcesser.class, "/incoming");
        handler.addServlet(RetrieveServlet.class, "/retrieve");

        handler.setMaxFormContentSize(2000000000);

        handler.setAttribute("me", props.getProperty("me"));
        handler.setAttribute("slaves", slaveServers);
        handler.setAttribute("map", new ConcurrentHashMap<Integer, String>());


        server.setHandler(handler);

        server.start();
        server.join();

    }
}
