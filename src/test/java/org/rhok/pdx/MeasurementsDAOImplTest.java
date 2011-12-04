package org.rhok.pdx;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

//this assumes that MongoDB is running in the background on localhost.
public class MeasurementsDAOImplTest {

    private MeasurementsDAOImpl dao;
    private MongoAccess access;

    @Before
    public void setup() {
        access = new MongoAccess("test");
        access.getCollection().drop();
        access.setupDB();
        dao = new MeasurementsDAOImpl();
        dao.setMongoAccess(access);
    }

    @Test
    public void testSave() {
        saveOne();
        DBCursor dbCursor = access.getCollection().find();
        assertEquals(1, dbCursor.count());
        while (dbCursor.hasNext()) {
            System.out.println(JSON.toString(dbCursor.next()));
        }
    }

    @Test
    public void testRead() {
        saveOne();
        dao.getMeasurements(new Location(1, 2), 3, 0, -1);
    }


    private void saveOne() {
        Measurements measurements = new Measurements();
        measurements.add(new DataPoint(new Location(1, 2), 3, 4, System.currentTimeMillis()));
        dao.saveMeasurements(measurements);
    }

}
