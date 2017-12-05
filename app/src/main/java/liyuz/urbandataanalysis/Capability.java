package liyuz.urbandataanalysis;

import java.io.Serializable;

/**
 * Created by Administrator on 12/3/2017.
 */

// A object containing info about each data set
public class Capability implements Serializable{
    String capName = "No name";
    String capTitle = "No title";
    String capAbstracts = "No abstract";
    String capOrganization = "No organization";
    String capGeoName = "No geo type";
    String capKeywords;
    BBOX capBbox = new BBOX();
    String capCorners = "No bounding box";
//    BBOX capBbox = "No bounding box";
    int image_id;
}
