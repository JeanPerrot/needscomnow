package org.rhok.pdx;

import org.junit.Test;

public class DatasetSetup {

    @Test
    public void setup() {
        MongoAccess mongoAccess = new MongoAccess();
        MeasurementsDAOImpl dao = new MeasurementsDAOImpl();
        dao.setMongoAccess(mongoAccess);

        Measurements measurements = new Measurements();

        double beginLng = 0;
        double beginLat = 45;

        double range = 0.05;

        int intervals = 30;

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                double lat = beginLat + ((double) i) / intervals * range;
                double lng = beginLng + ((double) j) / intervals * range;


                int signal = (int) (1/Math.sqrt(0.1+(lat - beginLat) * (lat - beginLat) + (lng - beginLng) * (lng - beginLng)));
                int wifi = 0;

                measurements.add(lat, lng, signal, wifi, System.currentTimeMillis());
            }
        }

        dao.saveMeasurements(measurements);


    }

}
