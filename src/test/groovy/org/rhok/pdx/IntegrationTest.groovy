package org.rhok.pdx

import org.junit.Test
import org.junit.Before
import org.rhok.pdx.dao.MongoLauncher
import org.junit.After
import org.rhok.pdx.web.PortUtil
import org.rhok.pdx.web.SignalStrengthApplication
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.*
import static groovy.util.GroovyTestCase.assertEquals
import org.rhok.pdx.model.Measurements
import org.rhok.pdx.model.DataPoint
import org.rhok.pdx.model.Location
import org.rhok.pdx.dao.MongoAccess
import org.junit.BeforeClass
import org.junit.AfterClass
import java.util.concurrent.Executors

public class IntegrationTest {

    static MongoLauncher launcher
    int port

    @BeforeClass
    public static void beforeClass() {
        launcher = new MongoLauncher()
        launcher.launchMongo()

        new MongoAccess().getCollection().drop()
    }

    @AfterClass
    public static void afterClass() {
        launcher.stopMongo()
    }

    @Before
    public void setup() {
        port = PortUtil.getPort()
        SignalStrengthApplication.main(null)
    }

    @After
    public void tearDown() {
        SignalStrengthApplication.shutdown()
    }


    @Test
    public void testJson() {
        def http = new HTTPBuilder("http://localhost:$port")

        http.request(Method.GET, ContentType.JSON) {
            uri.path = '/'
            uri.query = [lat: "45", lng: "45"]

            response.success = {resp, json ->
                println resp.status
                println json
                assertEquals('{"measurements":[]}'.toString(), json.toString())
            }
        }
    }


    @Test
    public void testKml() {
        def expected = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0">
    <Document>
        <name>signal strength map</name>
        <open>1</open>
    </Document>
</kml>
"""

        def http = new HTTPBuilder("http://localhost:$port")
        http.request(Method.GET, ContentType.TEXT) {
            uri.path = '/'
            uri.query = [lat: "45", lng: "45"]

            headers.Accept = 'application/vnd.google-earth.kml+xml'

            response.success = {resp, reader ->
                println resp.status
                assertEquals(expected, reader.text)
            }
        }
    }

    @Test
    public void testPost() {
        def http = new HTTPBuilder("http://localhost:$port")

        //read
        http.request(Method.GET, ContentType.JSON) {
            uri.path = '/'
            uri.query = [lat: "45", lng: "45"]

            response.success = {resp, json ->
                println resp.status
                println json
                assertEquals('{"measurements":[]}'.toString(), json.toString())
            }
        }
        //post
        def measurements = new Measurements();
        measurements.add(new DataPoint(new Location(45, 45), 100, 10, 0l))
        http.request(Method.POST, ContentType.JSON) {
            uri.path = '/'
            body = measurements

            response.success = {
                println "successful post"
            }
        }

        //read
        http.request(Method.GET, ContentType.JSON) {
            uri.path = '/'
            uri.query = [lat: "45", lng: "45"]

            response.success = {resp, json ->
                println resp.status
                println json
                assertEquals('{"measurements":[{"location":{"lat":45,"lng":45},"timestamp":0,"signal":100,"wifi":10}]}'.toString(), json.toString())
            }
        }
        //TODO
        //read - post - read cycle
    }


}
