package org.rhok.pdx.web;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.rhok.pdx.model.DataPoint;
import org.rhok.pdx.model.Measurements;
import org.rhok.pdx.model.RequestParams;

import java.util.Collection;

public class KmlMapper {


    public Kml toKml(Measurements measurements, RequestParams params) {
        final Kml kml = new Kml();

        //TODO - absolute scale of cell phone intensity
        double maxIntensity = getMaxIntensity(measurements);
        double averageIntensity = average(measurements.getMeasurements());

        Document doc = kml.createAndSetDocument().withName("signal strength map").withOpen(true);
//        final LookAt lookat = doc.createAndSetLookAt()
//                .withLongitude(params.longitude)
//                .withLatitude(params.latitude);
//        Folder folder = doc.createAndAddFolder();
//        folder.withName("strength tiles").withOpen(true);


        for (DataPoint dataPoint : measurements.getMeasurements()) {
            int color = (int) (((dataPoint.getSignal()) / maxIntensity) * 255);
            String styleId = "testStyle" + color;
            Style style = iconStyle(color, styleId);

            doc.createAndAddPlacemark()
                    .withDescription("signal strength: " + dataPoint.getSignal() + "<br>latitude: " + dataPoint.getLocation().getLat() + "<br>longitude: " + dataPoint.getLocation().getLng())
                    .addToStyleSelector(style)
                    .withStyleUrl(style.getId())
                    .createAndSetPoint()
                    .addToCoordinates(dataPoint.getLocation().getLng(), dataPoint.getLocation().getLat());
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

    private Style iconStyle(int rgb, String id) {
        String href = getIconForColor(rgb);
        Style style = new Style();
        style.withId(id).createAndSetIconStyle()
                .withColorMode(ColorMode.NORMAL)
                .withScale(1.5d)
                .createAndSetIcon()
                .withHref(href);
        return style;
    }

    private String getIconForColor(int rgb) {
        //on a scale from 0 to 255
        String color = "";
        if (rgb < 10) {
            color = "Red";
        } else if (rgb < 128) {
            color = "Orange";
        } else {
            color = "Green";
        }
        return "http://localhost:8881/web/img/signal" + color + ".png";
    }

    private Style polygonStyle(String rgb, String id) {
        Style style = new Style();
        style.createAndSetPolyStyle().withColor("50" + rgb).withOutline(false);
        style.setId(id);
        return style;
    }


}
