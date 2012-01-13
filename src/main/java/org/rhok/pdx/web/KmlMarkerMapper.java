package org.rhok.pdx.web;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.rhok.pdx.model.DataPoint;
import org.rhok.pdx.model.Measurements;
import org.rhok.pdx.model.RequestParams;

public class KmlMarkerMapper implements KmlMapper {

    private static final String WEB_DIR = "/web/img/";

    public Kml toKml(Measurements measurements, RequestParams params) {
        final Kml kml = new Kml();

        Document doc = kml.createAndSetDocument().withName("signal strength map").withOpen(true);
        doc.addToStyleSelector(getStyle(LEVEL.GOOD));
        doc.addToStyleSelector(getStyle(LEVEL.LOW));
        doc.addToStyleSelector(getStyle(LEVEL.NONE));
        for (DataPoint dataPoint : measurements.getMeasurements()) {
            LEVEL level = LEVEL.from(dataPoint.getSignal());
            Style style = getStyle(level);
            doc.createAndAddPlacemark()
                    .withDescription("signal strength: " + dataPoint.getSignal() + "<br>latitude: " + dataPoint.getLocation().getLat() + "<br>longitude: " + dataPoint.getLocation().getLng())
                    .withStyleUrl("#"+style.getId())
                    .createAndSetPoint()
                    .addToCoordinates(dataPoint.getLocation().getLng(), dataPoint.getLocation().getLat());
        }
        return kml;
    }

    private Style getStyle(LEVEL level) {
        String styleId = "testStyle" + level;
        return iconStyle(level, styleId);
    }

    private Style iconStyle(LEVEL level, String id) {
        String href = getIconForColor(level);
        Style style = new Style();
        style.withId(id).createAndSetIconStyle()
                .withColorMode(ColorMode.NORMAL)
                .withScale(1.5d)
                .createAndSetIcon()
                .withHref(href);
        return style;
    }

    private String getIconForColor(LEVEL level) {
        String color = "";
        switch (level) {
            case NONE:
                color = "Red";
                break;
            case LOW:
                color = "Orange";
                break;
            case GOOD:
                color = "Green";
                break;
        }
        return WEB_DIR + "signal" + color + ".png";
    }
}
