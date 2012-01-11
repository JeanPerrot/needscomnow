package org.rhok.pdx.web;

import de.micromata.opengis.kml.v_2_2_0.Kml;
import org.apache.log4j.Logger;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.rhok.pdx.model.Location;
import org.rhok.pdx.model.Measurements;
import org.rhok.pdx.model.RequestParams;
import org.rhok.pdx.service.SignalStrengthService;

import java.io.IOException;
import java.io.Writer;

public class SignalStrengthResource extends ServerResource {
    private static Logger logger = Logger.getLogger(SignalStrengthResource.class);
    public static final double DEFAULT_RANGE = 0.1;
    public static final int DEFAULT_MAX_COUNT = Integer.MAX_VALUE;
    public static final int DEFAULT_TIMESTAMP = -1;

    private SignalStrengthService service;

    @Get("json")
    public Measurements getMeasurements() {
        RequestParams params = parseParams();
        if (params == null) return null;
        Location location = new Location(params.latitude, params.longitude);
        Measurements measurements = service.getMeasurements(location, params.range, params.timestamp, params.maxCount);
        return measurements;
    }

    @Get("kml")
    public WriterRepresentation getMeasurementsHeat() {
        RequestParams params = parseParams();
        if (params == null) return null;
        Location location = new Location(params.latitude, params.longitude);
        Measurements measurements = service.getMeasurements(location, params.range, params.timestamp, params.maxCount);
        KmlMapper mapper = getMapper(params);
        Kml kml = mapper.toKml(measurements, params);
        return represent(kml);
    }

    private KmlMapper getMapper(RequestParams params) {
        //that will depend on the span...
        if (params.range < 0.025) {
            return new KmlMarkerMapper();
        }
        return new KmlHeatMapper();
    }

    @Post
    public void save(Measurements measurements) {
        try {
            service.saveMeasurements(measurements);
            getResponse().setStatus(Status.SUCCESS_OK, "Successfully saved " + measurements.getMeasurements().size() + " data points");
        } catch (Exception e) {
            logger.error("error saving measurements", e);
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, "Error saving measurements: " + e.getMessage());
        }
    }

    private WriterRepresentation represent(final Kml kml) {
        return new WriterRepresentation(MediaType.APPLICATION_XML) {
            @Override
            public void write(Writer writer) throws IOException {
                kml.marshal(writer);
            }
        };
    }

    private RequestParams parseParams() {
        String latStr = getStringParam("lat");
        String lngStr = getStringParam("lng");
        String rStr = getStringParam("range");
        String mStr = getStringParam("max_count");
        String timestampStr = getStringParam("timestamp");

        double lat = 0;
        double lng = 0;
        double range = 0;
        int maxCount;
        long timestamp;
        try {
            lat = required(latStr);
            lng = required(lngStr);
        } catch (Exception e) {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "lat and lng parameters are required and must be numbers");
            return null;
        }

        range = optional(rStr, DEFAULT_RANGE);
        maxCount = (int) optional(mStr, DEFAULT_MAX_COUNT);
        timestamp = (long) optional(timestampStr, DEFAULT_TIMESTAMP);
        RequestParams params = new RequestParams(lat, lng, range, timestamp, maxCount);
        return params;
    }

    private String getStringParam(String name) {
        Form form = getQuery();
        if (form == null) return null;
        return form.getFirstValue(name);
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

    public void setService(SignalStrengthService service) {
        this.service = service;
    }
}
