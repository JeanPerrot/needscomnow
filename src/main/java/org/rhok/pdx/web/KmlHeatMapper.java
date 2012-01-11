package org.rhok.pdx.web;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Style;
import org.rhok.pdx.model.DataPoint;
import org.rhok.pdx.model.Location;
import org.rhok.pdx.model.Measurements;
import org.rhok.pdx.model.RequestParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//experimental - for many markers, attempt to cluster them.
public class KmlHeatMapper implements KmlMapper {

    public static final int DIVISIONS = 500;
    private static final int MAX_SIGNAL = 30;

    @Override
    public Kml toKml(Measurements measurements, RequestParams params) {
        final Kml kml = new Kml();

        Document doc = kml.createAndSetDocument().withName("signal strength map").withOpen(true);
        Location topLeft = new Location(params.latitude - params.range, params.longitude - params.range);
        Location bottomRight = new Location(params.latitude + params.range, params.longitude + params.range);
        Map<Location, Collection<DataPoint>> bins = getBins(measurements, topLeft, bottomRight, DIVISIONS);

        double widthInc = (bottomRight.getLng() - topLeft.getLng()) / DIVISIONS;
        double heightInc = (bottomRight.getLat() - topLeft.getLat()) / DIVISIONS;
        int index = 0;
        for (Map.Entry<Location, Collection<DataPoint>> entry : bins.entrySet()) {

            double signal = average(entry.getValue());
            String color = getBgrColor(signal);

            String styleId = "testStyle" + color + "_" + index;
            Style style = polygonStyle(color, styleId);

            double lat = entry.getKey().getLat() + heightInc / 2;
            double lng = entry.getKey().getLng() + widthInc / 2;

            doc.createAndAddPlacemark().addToStyleSelector(style).withStyleUrl(style.getId())
                    .withOpen(Boolean.FALSE)
                    .withDescription("average signal: " + signal + "\n" + (lng - widthInc) + "," + (lat - heightInc) + "\n" + (lng + widthInc) + "," + (lat + heightInc))
                    .createAndSetPolygon()
                    .createAndSetOuterBoundaryIs()
                    .withLinearRing(
                            new LinearRing()
                                    .addToCoordinates(lng - widthInc, lat - heightInc)
                                    .addToCoordinates(lng + widthInc, lat - heightInc)
                                    .addToCoordinates(lng + widthInc, lat + heightInc)
                                    .addToCoordinates(lng - widthInc, lat + heightInc));

        }
        return kml;
    }


    private String getBgrColor(double signal) {
        int percent = (int) (100 * signal / MAX_SIGNAL);
        int green = 255 * Math.min(100, percent * 2) / 100;
        int red = 255 * Math.max(0, 100 - percent * 2) / 100;
        return "00" + Integer.toHexString(green) + Integer.toHexString(red);
    }

    private double average(Collection<DataPoint> value) {
        int total = 0;
        for (DataPoint dataPoint : value) {
            total += dataPoint.getSignal();
        }
        return total / value.size();
    }

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


    private Style polygonStyle(String bgr, String id) {
        Style style = new Style();
        style.createAndSetPolyStyle().withColor("50" + bgr).withOutline(false);
        style.setId(id);
        return style;
    }


}
