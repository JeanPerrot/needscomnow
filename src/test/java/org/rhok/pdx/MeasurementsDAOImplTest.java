package org.rhok.pdx;

import com.mongodb.DBCursor;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rhok.pdx.dao.MeasurementsDAOImpl;
import org.rhok.pdx.dao.MongoAccess;
import org.rhok.pdx.dao.MongoLauncher;
import org.rhok.pdx.model.DataPoint;
import org.rhok.pdx.model.Location;
import org.rhok.pdx.model.Measurements;

import static junit.framework.Assert.assertEquals;

//this assumes that MongoDB is running in the background on localhost.
public class MeasurementsDAOImplTest {

    private static MongoLauncher mongoLauncher;
    private MeasurementsDAOImpl dao;
    private MongoAccess access;

    @BeforeClass
    public static void beforeClass() {
        mongoLauncher = new MongoLauncher();
        mongoLauncher.launchMongo();
    }

    @AfterClass
    public static void afterClass() {
        mongoLauncher.stopMongo();
    }

    @Before
    public void setup() {
        access = new MongoAccess();
        access.getCollection().drop();
        access.setupDB();
        dao = new MeasurementsDAOImpl();
        dao.setMongoAccess(access);
        mongoLauncher = new MongoLauncher();
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
