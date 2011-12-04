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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String latStr = req.getParameter("lat");
        String lngStr = req.getParameter("lng");
        String rStr = req.getParameter("range");
        String mStr = req.getParameter("max_count");
        String timestampStr = req.getParameter("timestamp");

        double lat = Double.parseDouble(latStr);
        double lng = Double.parseDouble(lngStr);
        double range = Double.parseDouble(rStr);
        int maxCount = Integer.parseInt(mStr);
        long timestamp = Long.parseLong(timestampStr);

        Location location = new Location(lat, lng);

        Measurements measurements = getMeasurements(location, range, timestamp, maxCount);

        String json = gson.toJson(measurements);

        resp.getWriter().print(json);
    }

    private Measurements getMeasurements(Location location, double range, long timestamp, int maxCount) {
        try {
            return dao.getMeasurements(location, range, timestamp, maxCount);
        } catch (Exception e) {
            logger.error(e);
            return new MockMeasurementDAO().getMeasurements(location, range, timestamp, maxCount);
        }

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

    protected Measurements parseFromRequest(Reader reader) throws IOException {
        Measurements measurements = gson.fromJson(reader, Measurements.class);
        return measurements;
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
