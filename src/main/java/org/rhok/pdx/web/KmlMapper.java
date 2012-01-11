package org.rhok.pdx.web;

import de.micromata.opengis.kml.v_2_2_0.Kml;
import org.rhok.pdx.model.Measurements;
import org.rhok.pdx.model.RequestParams;

public interface KmlMapper {
    Kml toKml(Measurements measurements, RequestParams params);
}
