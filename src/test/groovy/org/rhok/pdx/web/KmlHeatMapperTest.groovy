package org.rhok.pdx.web

import org.junit.Test
import org.rhok.pdx.model.Measurements
import com.google.gson.Gson
import org.rhok.pdx.model.RequestParams


class KmlHeatMapperTest {
    @Test
    public void testNoOverlap() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/data.json")));
        Measurements measurements = new Gson().fromJson(reader, Measurements.class);
        def kml = new KmlHeatMapper().toKml(measurements, new RequestParams(45.55954082197344, -122.71892639160154, 0.894012451171875, -1, Integer.MAX_VALUE))
        StringWriter writer = new StringWriter();
        kml.marshal(writer);
        def doc = new XmlParser().parseText(writer.toString())
        def placemarks = doc.Document.Placemark

        //overlap is when a point's coordinates falls within an existing polygon.
        //overlap detection implies sorting all begining and ending x and y coordinates in four lists
        def polygons = []
        placemarks.each {placemark ->
            def coords = placemark.Polygon.outerBoundaryIs.LinearRing.coordinates
            def data = coords.text().split(" ")
            def points = []
            data.each {datum->
                def split=datum.split(",")
                points << [ln: Double.parseDouble(split[0]), lat: Double.parseDouble(split[1])]
            }

            //brute force
            points.each {point->
                polygons.each {polygon ->
                    junit.framework.Assert.assertFalse("$point is contained in $polygon",contains(polygon,point))
                }
            }
            polygons<<points


        }

        println placemarks.size()
    }
    
    @Test
    public void testContains(){
        junit.framework.Assert.assertTrue(contains([[ln: 0,lat: 0],[ln: 1,lat: 0],[ln: 1,lat: 1],[ln: 0,lat: 1]],[ln: 0.5,lat: 0.5]) )
    } 

    private boolean contains(polygon,point){
        def bl=polygon[0]
        def br=polygon[1]
        def tr=polygon[2]
        def tl=polygon[3]
        println "matches tl ${tl.ln<point.ln && tl.lat>point.lat}"
        println "matches tr ${tr.lat>point.lat && tr.ln>point.ln}"
        println "matches bl ${br.lat<point.lat && br.ln>point.ln}"
        println "matches br ${bl.lat<point.lat && bl.ln<point.ln}"

        return tl.ln<point.ln && tl.lat>point.lat && tr.lat>point.lat && tr.ln>point.ln && br.lat<point.lat && br.ln>point.ln && bl.lat<point.lat && bl.ln<point.ln

    }
}
