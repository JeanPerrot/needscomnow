package org.rhok.pdx;

import com.google.gson.Gson;
import com.sun.xml.internal.bind.v2.TODO;
import de.micromata.opengis.kml.v_2_2_0.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SignalStrength extends HttpServlet {
    private static Logger logger = Logger.getLogger(SignalStrength.class);
    public static final double DEFAULT_RANGE = 0.1;
    public static final int DEFAULT_MAX_COUNT = Integer.MAX_VALUE;
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
     *
     * depending on content type, return:
     * kml content type: kml document
     * json content type: json...
     *
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
        RequestParams params = new RequestParams(lat, lng, range, timestamp, maxCount);
        Location location = new Location(params.latitude, params.longitude);
        Measurements measurements = getMeasurements(location, params.range, params.timestamp, params.maxCount);

        String contentType = req.getContentType();
//        contentType = "kml";
        if (contentType != null && contentType.toLowerCase().contains("kml")) {
            doGetKml(resp, measurements, params);
        } else {
            doGetJson(resp, measurements);
        }
    }

    private void doGetKml(HttpServletResponse resp, Measurements measurements, RequestParams params) throws IOException {
        final Kml kml = new Kml();

        //TODO - absolute scale of cell phone intensity
        double maxIntensity = getMaxIntensity(measurements);
        int divisions = 50;


        Document doc = kml.createAndSetDocument().withName("signal strength map").withOpen(true);

        Folder folder = doc.createAndAddFolder();
        folder.withName("strength tiles").withOpen(true);

        Location topLeft = new Location(params.latitude - params.range, params.longitude - params.range);
        Location bottomRight = new Location(params.latitude + params.range, params.longitude + params.range);
        Map<Location, Collection<DataPoint>> bins = getBins(measurements, topLeft, bottomRight, divisions);

        double widthInc = (bottomRight.getLng() - topLeft.getLng()) / divisions;
        double heightInc = (bottomRight.getLat() - topLeft.getLat()) / divisions;
        int index = 0;
        for (Map.Entry<Location, Collection<DataPoint>> entry : bins.entrySet()) {

            int signal = average(entry.getValue());

            int someNumber = (int) ((((double) signal) / maxIntensity) * 255);
            String color = Integer.toHexString(someNumber);
            String styleId = "testStyle" + color + "_" + index;
            Style style = polygonStyle("14" + color + "ff", styleId);

            double lat = entry.getKey().getLat() + heightInc / 2;
            double lng = entry.getKey().getLng() + widthInc / 2;

            folder.createAndAddPlacemark().addToStyleSelector(style).withStyleUrl(style.getId())
                    .withOpen(Boolean.FALSE)
                    .createAndSetPolygon()
                    .createAndSetOuterBoundaryIs()
                    .withLinearRing(
                            new LinearRing()
                                    .addToCoordinates(lng - widthInc, lat - heightInc)
                                    .addToCoordinates(lng + widthInc, lat - heightInc)
                                    .addToCoordinates(lng + widthInc, lat + heightInc)
                                    .addToCoordinates(lng - widthInc, lat + heightInc));

        }
        kml.marshal(resp.getOutputStream());
    }

    private double getMaxIntensity(Measurements measurements) {
        double max = -1;
        for (DataPoint dataPoint : measurements.getMeasurements()) {
            if (dataPoint.getSignal() > max)
                max = dataPoint.getSignal();
        }
        return max;
    }

    private int average(Collection<DataPoint> value) {
        int total = 0;
        for (DataPoint dataPoint : value) {
            total += dataPoint.getSignal();
        }
        return total / value.size();
    }

    //TODO move somewhere else
    private Map<Location, Collection<DataPoint>> getBins(Measurements measurement, Location topLeft, Location bottomRight, int divisions) {
        Map<Location, Collection<DataPoint>> retValue = new HashMap<Location, Collection<DataPoint>>();

        for (DataPoint dataPoint : measurement.getMeasurements()) {
            Location key = getKey(dataPoint.getLocation(), topLeft, bottomRight, divisions);
            Collection<DataPoint> dataPoints = retValue.get(key);
            if (dataPoints == null) {
                dataPoints = new ArrayList<DataPoint>();
                retValue.put(key, dataPoints);
            }
            dataPoints.add(dataPoint);
        }

        return retValue;
    }

    private Location getKey(Location location, Location topLeft, Location bottomRight, int divisions) {
        double lat = topLeft.getLat();
        double lng = topLeft.getLng();

        double widthInc = (bottomRight.getLng() - lng) / divisions;
        double heightInc = (bottomRight.getLat() - lat) / divisions;

        int x = (int) ((location.getLng() - lng) / widthInc);
        int y = (int) ((location.getLat() - lat) / heightInc);

        return new Location(lat + y * widthInc, lng + x * heightInc);
    }


    private Style polygonStyle(String rgb, String id) {
        Style style = new Style();
        style.createAndSetPolyStyle().withColor("50" + rgb).withOutline(false);
        style.setId(id);
        return style;
    }


    private void doGetJson(HttpServletResponse resp, Measurements measurements) throws IOException {
        resp.setContentType("application/json");

        String json = gson.toJson(measurements);
        resp.getWriter().print(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        Measurements measurements = parseFromRequest(req.getReader());
        try {
            dao.saveMeasurements(measurements);
            resp.getWriter().print("Successfully saved " + measurements.getMeasurements().size() + " data points");
        } catch (Exception e) {
            logger.error("error saving measurements", e);
            resp.getWriter().print("Error saving measurements: " + e.getMessage());
            resp.setStatus(500);
        }
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

    private static class RequestParams {
        private RequestParams(double latitude, double longitude, double range, long timestamp, int maxCount) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.range = range;
            this.timestamp = timestamp;
            this.maxCount = maxCount;
        }

        final double latitude;
        final double longitude;
        final double range;
        final long timestamp;
        final int maxCount;

    }
}
