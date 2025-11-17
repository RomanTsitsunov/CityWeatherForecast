package doczilla.cityweatherforecast;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");
        ServletHolder holderHome = new ServletHolder("default", DefaultServlet.class);
        holderHome.setInitParameter("resourceBase", Main.class.getResource("/static").toExternalForm());
        holderHome.setInitParameter("dirAllowed", "false");
        ctx.addServlet(holderHome, "/");

        ctx.addServlet(new ServletHolder(new WeatherServlet()), "/weather");

        server.setHandler(ctx);
        server.start();
        server.join();
    }
}