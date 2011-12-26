package org.rhok.pdx;

import org.junit.Test;

public class DatasetSetup {

    //TODO - this should spin MongoDB up and down.

    @Test
    public void setup() {
        MongoAccess mongoAccess = new MongoAccess();
        MeasurementsDAOImpl dao = new MeasurementsDAOImpl();
        dao.setMongoAccess(mongoAccess);

        Measurements measurements = new Measurements();

        double beginLng = 0;
        double beginLat = 45;

        double range = 0.0005;

        int intervals = 30;

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                double lat = beginLat + ((double) i) / intervals * range;
                double lng = beginLng + ((double) j) / intervals * range;

                double max = 100;
                double radius = 0.0001;
                double distance = Math.sqrt((lat - beginLat) * (lat - beginLat) + (lng - beginLng) * (lng - beginLng));
                int signal = (int) (1 / (1 / max * max) + 10 * distance / radius);
                int wifi = 0;

                measurements.add(lat, lng, signal, wifi, System.currentTimeMillis());
            }
        }

        dao.saveMeasurements(measurements);


    }

}
