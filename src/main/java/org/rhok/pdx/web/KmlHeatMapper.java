package org.rhok.pdx.web;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.rhok.pdx.model.DataPoint;
import org.rhok.pdx.model.Measurements;
import org.rhok.pdx.model.RequestParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//experimental - for many markers, attempt to cluster them.
public class KmlHeatMapper {



    public Kml toKml(Measurements measurements, RequestParams params) {
        final Kml kml = new Kml();

        //TODO - absolute scale of cell phone intensity
        double maxIntensity = getMaxIntensity(measurements);
        int divisions = 50;
        Document doc = kml.createAndSetDocument().withName("signal strength map").withOpen(true);
        final LookAt lookat = doc.createAndSetLookAt()
                .withLongitude(-122.50)
                .withLatitude(45.2083);
//        Folder folder = doc.createAndAddFolder();
//        folder.withName("strength tiles").withOpen(true);

        org.rhok.pdx.model.Location topLeft = new org.rhok.pdx.model.Location(params.latitude - params.range, params.longitude - params.range);
        org.rhok.pdx.model.Location bottomRight = new org.rhok.pdx.model.Location(params.latitude + params.range, params.longitude + params.range);
        Map<org.rhok.pdx.model.Location, Collection<DataPoint>> bins = getBins(measurements, topLeft, bottomRight, divisions);

        double widthInc = (bottomRight.getLng() - topLeft.getLng()) / divisions;
        double heightInc = (bottomRight.getLat() - topLeft.getLat()) / divisions;
        int index = 0;
        for (Map.Entry<org.rhok.pdx.model.Location, Collection<DataPoint>> entry : bins.entrySet()) {

            int signal = average(entry.getValue());

            int someNumber = (int) ((((double) signal) / maxIntensity) * 255);
            String color = Integer.toHexString(someNumber);
            String styleId = "testStyle" + color + "_" + index;
            Style style = polygonStyle("14" + color + "ff", styleId);

            double lat = entry.getKey().getLat() + heightInc / 2;
            double lng = entry.getKey().getLng() + widthInc / 2;

            doc.createAndAddPlacemark().addToStyleSelector(style).withStyleUrl(style.getId())
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
        return kml;
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

    private Map<org.rhok.pdx.model.Location, Collection<DataPoint>> getBins(Measurements measurement, org.rhok.pdx.model.Location topLeft, org.rhok.pdx.model.Location bottomRight, int divisions) {
        Map<org.rhok.pdx.model.Location, Collection<DataPoint>> retValue = new HashMap<org.rhok.pdx.model.Location, Collection<DataPoint>>();

        for (DataPoint dataPoint : measurement.getMeasurements()) {
            org.rhok.pdx.model.Location key = getKey(dataPoint.getLocation(), topLeft, bottomRight, divisions);
            Collection<DataPoint> dataPoints = retValue.get(key);
            if (dataPoints == null) {
                dataPoints = new ArrayList<DataPoint>();
                retValue.put(key, dataPoints);
            }
            dataPoints.add(dataPoint);
        }

        return retValue;
    }

    private org.rhok.pdx.model.Location getKey(org.rhok.pdx.model.Location location, org.rhok.pdx.model.Location topLeft, org.rhok.pdx.model.Location bottomRight, int divisions) {
        double lat = topLeft.getLat();
        double lng = topLeft.getLng();

        double widthInc = (bottomRight.getLng() - lng) / divisions;
        double heightInc = (bottomRight.getLat() - lat) / divisions;

        int x = (int) ((location.getLng() - lng) / widthInc);
        int y = (int) ((location.getLat() - lat) / heightInc);

        return new org.rhok.pdx.model.Location(lat + y * widthInc, lng + x * heightInc);
    }


    private Style polygonStyle(String rgb, String id) {
        Style style = new Style();
        style.createAndSetPolyStyle().withColor("50" + rgb).withOutline(false);
        style.setId(id);
        return style;
    }


}
