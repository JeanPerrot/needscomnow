package org.rhok.pdx;

import com.mongodb.*;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

public class MongoAccessTest {

    @Test
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
    public void testMongoHQ() {
        new MongoAccess();
    }

}
