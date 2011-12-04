package org.rhok.pdx;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class SignalStrength extends HttpServlet {

    private Gson gson = new Gson();

    private MeasurementsDAO dao = new MockMeasurementDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String latStr = req.getParameter("lat");
        String lngStr = req.getParameter("lng");
        String rStr = req.getParameter("range");
        String mStr = req.getParameter("max_count");

        double lat = Double.parseDouble(latStr);
        double lng = Double.parseDouble(lngStr);
        double range = Double.parseDouble(rStr);
        int maxCount = Integer.parseInt(mStr);

        Location location = new Location(lat, lng);

        Measurements measurements = getMeasurements(location, range, maxCount);

        String json = gson.toJson(measurements);

        resp.getWriter().print(json);
    }

    private Measurements getMeasurements(Location location, double range, int maxCount) {
        return dao.getMeasurements(location, range, maxCount);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Measurements measurements = parseFromRequest(req.getReader());
        resp.getWriter().print("Request was successfully parsed, but this is still a mock!");
    }

    protected Measurements parseFromRequest(Reader reader) throws IOException {
        Measurements measurements = gson.fromJson(reader, Measurements.class);
        return measurements;
    }

    public static void main(String[] args) throws Exception {
        Integer port = getPort();
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new SignalStrength()), "/*");
        server.start();
        server.join();
    }

    //TODO - externalize
    private static Integer getPort() {
        Integer port = null;
        try {
            port = Integer.valueOf(System.getenv("PORT"));
        } catch (Exception e) {
            port = 8881;
        }
        return port;
    }


}
