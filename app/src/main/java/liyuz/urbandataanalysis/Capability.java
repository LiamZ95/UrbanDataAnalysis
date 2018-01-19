package liyuz.urbandataanalysis;

import java.io.Serializable;


// A object containing info about each data set
public class Capability implements Serializable{
    String capName = "No name";
    String capTitle = "No title";
    String capAbstracts = "No abstract";
    String capOrganization = "No organization";
    String capGeoName = "No geo type";
    String capKeywords;
    BBox capBBox = new BBox();
    String capCorners = "No bounding box";
//    BBox capBBox = "No bounding box";
    int image_id;
}
