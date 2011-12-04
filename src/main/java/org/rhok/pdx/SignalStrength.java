package org.rhok.pdx;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class SignalStrength extends HttpServlet {
    private static Logger logger = Logger.getLogger(SignalStrength.class);
    public static final double DEFAULT_RANGE = 0.1;
    public static final int DEFAULT_MAX_COUNT = 1000;
    public static final int DEFAULT_TIMESTAMP = -1;

    private Gson gson = new Gson();

    private MeasurementsDAO dao;

    public SignalStrength() {
        logger.info("creating the SignalStrength servlet");
        MeasurementsDAOImpl dao = new MeasurementsDAOImpl();
        MongoAccess access = new MongoAccess();
        dao.setMongoAccess(access);
        this.dao = dao;
        logger.info("servlet created successfully");

    }

    @Override
    /**
     * GET request. Returns measurements taken in the vicinity of (lat,lng)
     * lat:latitude
     * lng:longitude
     * range: max distance from lat/lng
     * max_count: maximum number of results
     * timestamp: latest date
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String latStr = req.getParameter("lat");
        String lngStr = req.getParameter("lng");
        String rStr = req.getParameter("range");
        String mStr = req.getParameter("max_count");
        String timestampStr = req.getParameter("timestamp");

        double lat = 0;
        double lng = 0;
        double range = 0;
        int maxCount;
        long timestamp;
        try {
            lat = required(latStr);
            lng = required(lngStr);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().print("lat and lng parameters are required and must be numbers");
            return;
        }

        range = optional(rStr, DEFAULT_RANGE);
        maxCount = (int) optional(mStr, DEFAULT_MAX_COUNT);
        timestamp = (long) optional(timestampStr, DEFAULT_TIMESTAMP);

        Location location = new Location(lat, lng);

        Measurements measurements = getMeasurements(location, range, timestamp, maxCount);

        String json = gson.toJson(measurements);

        resp.getWriter().print(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Measurements measurements = parseFromRequest(req.getReader());
        try {
            dao.saveMeasurements(measurements);
        } catch (Exception e) {

        }
        resp.getWriter().print("Successfully saved " + measurements.getMeasurements().size() + " data points");
    }


    private Measurements getMeasurements(Location location, double range, long timestamp, int maxCount) {
        try {
            return dao.getMeasurements(location, range, timestamp, maxCount);
        } catch (Exception e) {
            logger.error(e);
            return new MockMeasurementDAO().getMeasurements(location, range, timestamp, maxCount);
        }

    }


    protected Measurements parseFromRequest(Reader reader) throws IOException {
        Measurements measurements = gson.fromJson(reader, Measurements.class);
        return measurements;
    }

    private double optional(String rStr, double defaultValue) {
        try {
            return Double.parseDouble(rStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private double required(String latStr) {
        return Double.parseDouble(latStr);
    }


    public static void main(String[] args) throws Exception {
        try {
            logger.info("starting application");
            Integer port = getPort();
            Server server = new Server(port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            context.addServlet(new ServletHolder(new SignalStrength()), "/*");
            logger.info("starting the server");
            server.start();
            server.join();
            logger.info("server started");
        } catch (Exception e) {
            logger.error("error during application initialization", e);
            e.printStackTrace();
        }
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
