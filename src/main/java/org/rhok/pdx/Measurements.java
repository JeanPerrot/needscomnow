package org.rhok.pdx;

import java.util.ArrayList;
import java.util.List;

public class Measurements {
    private List<DataPoint> measurements = new ArrayList<DataPoint>();

    public Measurements() {
    }

    public void add(DataPoint d) {
        measurements.add(d);
    }

    public void add(double lat, double lng, int measurement, int wifi, long timestamp) {
        measurements.add(new DataPoint(new Location(lat, lng), measurement, wifi, timestamp));
    }

    public Measurements(List<DataPoint> measurements) {
        this.measurements = measurements;
    }

    public List<DataPoint> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<DataPoint> measurements) {
        this.measurements = measurements;
    }
}
