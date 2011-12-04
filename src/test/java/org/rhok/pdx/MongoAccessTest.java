package org.rhok.pdx;

import com.mongodb.Mongo;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MongoAccessTest {

    @Test
    public void testUrl() throws UnknownHostException, URISyntaxException {
        String url = "mongodb://heroku:c4c2456aeb4cde611002d26834329dc1@staff.mongohq.com:10077/app1930625";
        URI uri = new URI(url);
        url = "staff.mongohq.com:10077";
        Mongo mongo = new Mongo(uri.getHost(), uri.getPort());
        mongo.getDB("test");
    }
}
