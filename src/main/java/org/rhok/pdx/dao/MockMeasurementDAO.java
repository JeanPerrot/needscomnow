package org.rhok.pdx.dao;

import org.rhok.pdx.model.Location;
import org.rhok.pdx.model.Measurements;

public class MockMeasurementDAO implements MeasurementsDAO {


//0.1 degree ~ 5 miles (for the mock only)

    //ignore r and return generated data 0.1 degree in each direction. 900 datapoints returned.
    @Override
    public Measurements getMeasurements(Location l, double r, long timestamp, int maxCount) {
        Measurements retValue = new Measurements();
        double radius = 0.1;

        double beginLat = l.getLat() - radius;
        double beginLng = l.getLng() - radius;
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                double lat = beginLat + i / 30 * radius;
                double lng = beginLng + i / 30 * radius;


                int signal = i + j;
                int wifi = 30 - i + j;

                retValue.add(lat, lng, signal, wifi, System.currentTimeMillis());
            }
        }
        return retValue;

    }

    @Override
    public void saveMeasurements(Measurements ms) {

    }
}
