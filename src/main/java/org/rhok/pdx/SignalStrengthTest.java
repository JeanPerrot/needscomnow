package org.rhok.pdx;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static junit.framework.Assert.assertEquals;

public class SignalStrengthTest {

    @Test
    public void testJson() throws IOException {
        String toParse = "{\"measurements\":[{\"location\":{\"lat\":0.9,\"lng\":1.9},\"intensity\":0.0}]}";
        Measurements measurements = new SignalStrength().parseFromRequest(new StringReader(toParse));
        assertEquals(1, measurements.getMeasurements().size());

    }

}
