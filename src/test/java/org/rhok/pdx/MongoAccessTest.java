package org.rhok.pdx;

import com.mongodb.*;
import org.junit.Ignore;
import org.junit.Test;
import org.rhok.pdx.dao.MongoAccess;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MongoAccessTest {

    @Test
    @Ignore("playground for testing access")
    public void testUrl() throws UnknownHostException, URISyntaxException {
//        String url = "mongodb://heroku:c4c2456aeb4cde611002d26834329dc1@staff.mongohq.com:10077/app1930625";
        String url = "mongodb://RhokPDX2011:rhok@staff.mongohq.com:10082/measurements";
        Mongo mongo = new Mongo(new MongoURI(url));
        String debug=mongo.debugString();
        boolean locked = mongo.isLocked();
//        List<String> names = mongo.getDatabaseNames();
//        DB signalstrength = Mongo.connect(new DBAddress(url));
        DB signalstrength = mongo.getDB("signalstrength");
        signalstrength.authenticate("RhokPDX2011","rhok".toCharArray());
        DBCollection measurements = signalstrength.getCollection("measurements");
        measurements.find().count();
    }

    @Test
    @Ignore("this ends up testing access to a locally running Mongod, which is already tested by other classes")
    public void testMongoHQ() {
        new MongoAccess();
    }

}
