package org.rhok.pdx;

import com.google.gson.Gson;
import org.rhok.pdx.dao.MeasurementsDAOImpl;
import org.rhok.pdx.dao.MongoAccess;
import org.rhok.pdx.model.Measurements;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DataUtil {

    //create mock data and save it to mongo
    public void mockData() {
        MongoAccess mongoAccess = new MongoAccess();
        MeasurementsDAOImpl dao = new MeasurementsDAOImpl();
        dao.setMongoAccess(mongoAccess);

        Measurements measurements = new Measurements();

        double beginLng = -122.40;
        double beginLat = 45.31;

        double range = 1;

        int intervals = 100;

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                double lat = beginLat + ((double) i) / intervals * range;
                double lng = beginLng + ((double) j) / intervals * range;

                double max = 100;
                double radius = 0.1;
                double distance = Math.sqrt((lat - beginLat) * (lat - beginLat) + (lng - beginLng) * (lng - beginLng));
                int signal = (int) (1 / (1 / max * max) + 10 * distance / radius);
                int wifi = 0;

                measurements.add(lat, lng, signal, wifi, System.currentTimeMillis());
            }
        }
        dao.saveMeasurements(measurements);
    }

    //read the data.json file and save to mongo
    public void jsonData() {
        MongoAccess mongoAccess = new MongoAccess();
        MeasurementsDAOImpl dao = new MeasurementsDAOImpl();
        dao.setMongoAccess(mongoAccess);

        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/data.json")));
        Measurements measurements = new Gson().fromJson(reader, Measurements.class);
        dao.saveMeasurements(measurements);
    }

    public static void main(String[] args) {
        new DataUtil().jsonData();
    }


}
