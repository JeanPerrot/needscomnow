package org.rhok.pdx;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class KmlTest {

    @Test
    public void test() throws FileNotFoundException {
        final Kml kml = new Kml();

        Style style = new Style();
        style.createAndSetIconStyle().withColor("5fffcc00");
        style.setId("testStyle");


        kml.createAndSetPlacemark().addToStyleSelector(style)
                .withName("London, UK").withOpen(Boolean.TRUE).withStyleUrl("#testStyle")
                .createAndSetPoint().addToCoordinates(-0.126236, 51.500152);
        kml.marshal(System.out);
        kml.marshal(new File("test.kml"));
    }

    @Test
    public void testPolygon() throws FileNotFoundException {
        final Kml kml = new Kml();

        Style style = new Style();
        style.createAndSetPolyStyle().withColor("5f0000cc");
        style.setId("testStyle");

        kml.createAndSetPlacemark().addToStyleSelector(style)
                .withName("London, UK").withOpen(Boolean.TRUE).withStyleUrl("#testStyle").createAndSetPolygon().createAndSetOuterBoundaryIs().withLinearRing(new LinearRing().addToCoordinates(-122.366278, 37.818844).addToCoordinates(-122.365248, 37.819267).addToCoordinates(-122.365640, 37.819861).addToCoordinates(-122.366669, 37.819429));
        kml.marshal(System.out);
        kml.marshal(new File("test.kml"));
    }


    @Test
    public void testDocument() throws FileNotFoundException {
        final Kml kml = new Kml();

        Style style = new Style();
        style.createAndSetPolyStyle().withColor("5f0000cc");
        style.setId("testStyle");

        Document doc = kml.createAndSetDocument().withName("JAK Example1").withOpen(true);

        Folder folder = doc.createAndAddFolder();
        folder.withName("testFolder").withOpen(true);

        folder.createAndAddPlacemark().addToStyleSelector(style)
                .withName("London, UK").withOpen(Boolean.TRUE).withStyleUrl("#testStyle").createAndSetPolygon().createAndSetOuterBoundaryIs().withLinearRing(new LinearRing().addToCoordinates(-122.366278, 37.818844).addToCoordinates(-122.365248, 37.819267).addToCoordinates(-122.365640, 37.819861).addToCoordinates(-122.366669, 37.819429));
        folder.createAndAddPlacemark().addToStyleSelector(style)
                .withName("London, UK").withOpen(Boolean.TRUE).withStyleUrl("#testStyle").createAndSetPolygon().createAndSetOuterBoundaryIs().withLinearRing(new LinearRing().addToCoordinates(-122.366378, 37.828844).addToCoordinates(-122.335248, 37.814267).addToCoordinates(-122.365640, 37.819861).addToCoordinates(-122.366669, 37.819429));
        kml.marshal(System.out);
        kml.marshal(new File("test.kml"));
    }

    private static void createPlacemarkWithChart(Document document, Folder folder, double longitude, double latitude,
                                                 String continentName, int coveredLandmass) {

        int remainingLand = 100 - coveredLandmass;
        Icon icon = new Icon()
                .withHref("http://chart.apis.google.com/chart?chs=380x200&chd=t:" + coveredLandmass + "," + remainingLand + "&cht=p&chf=bg,s,ffffff00");
        Style style = document.createAndAddStyle();
        style.withId("style_" + continentName) // set the stylename to use this style from the placemark
                .createAndSetIconStyle().withScale(5.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(5.0); // set color and size of the continent name

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each continent
        placemark.withName(continentName)
                .withStyleUrl("#style_" + continentName)
                        // 3D chart imgae
                .withDescription(
                        "<![CDATA[<img src=\"http://chart.apis.google.com/chart?chs=430x200&chd=t:" + coveredLandmass + "," + remainingLand + "&cht=p3&chl=" + continentName + "|remaining&chtt=Earth's surface\" />")
                        // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(12000000);

        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
    }
}
