package liyuz.urbandataanalysis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import com.google.common.collect.Lists;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap;
    private ProgressDialog progressDialog;

    private final int HANDLER_FLAG = 0;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_FLAG) {
                String rawData = (String) msg.obj;
                parseJSON(rawData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        Message message = new Message();
//        message.what = HANDLER_FLAG;
//        handler.sendMessage(message);

        // Show progress dialog
        LongOperation myTask = null;
        myTask = new LongOperation();
        myTask.execute();

//        final String typeName = SelectedData.selectedCap.capName;
//        final String geoName = SelectedData.selectedCap.capGeoName;
//
//        final double lla = MapSettings.selectedBBox.getLowerLa();
//        final double llo = MapSettings.selectedBBox.getLowerLon();
//        final double hla = MapSettings.selectedBBox.getHigherLa();
//        final double hlo = MapSettings.selectedBBox.getHigherLon();
//
//        String url = "http://openapi.aurin.org.au/wfs?" +
//                "request=GetFeature&service=WFS&version=1.1.0&" +
//                "TypeName="+ typeName+ "&" +
//                "MaxFeatures=1000&outputFormat=json&CQL_FILTER=BBOX" +
//                "("+geoName+","+lla+","+llo+","+hla+","+hlo+")";
//
//        Log.i(TAG, url);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Capability selectedCap = SelectedData.selectedCap;
        BBox selectedBBox = MapSettings.selectedBBox;

        // Get the capability coordinates
        Double lla = selectedBBox.getLowerLa();
        Double hla = selectedBBox.getHigherLa();
        Double llo = selectedBBox.getLowerLon();
        Double hlo = selectedBBox.getHigherLon();

        // Calculate the center of capability
        LatLng center = new LatLng((lla+hla)/2.0,(llo+hlo)/2.0);

        // Set zoom ratio
        int zoom = (int) Math.log(320/(hlo - llo)) + 1;

        // Move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));

        // Set what to do when polygon is clicked
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {

            }
        });

    }


    private void openLocalFile() {
        AssetManager assetManager = getApplicationContext().getAssets();

        try{
            InputStream in = assetManager
                    .open("2_cap_map.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String data = stringBuilder.toString();
            Log.d(TAG, "All data read from local json file");

            // Parse json data
            parseJSON(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest() {
        final Capability selectedCap = SelectedData.selectedCap;
        final BBox selectedBBox = MapSettings.selectedBBox;
        final String typeName = selectedCap.capName;
        final String geoName = selectedCap.capGeoName;
        final Double lla = selectedBBox.getLowerLa();
        final Double llo = selectedBBox.getLowerLon();
        final Double hla = selectedBBox.getHigherLa();
        final Double hlo = selectedBBox.getHigherLon();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Authenticator.setDefault (new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication ("student", "dj78dfGF".toCharArray());
                    }
                });
                try{
                    URL url = new URL("http://openapi.aurin.org.au/wfs?" +
                            "request=GetFeature&service=WFS&version=1.1.0&" +
                            "TypeName="+ typeName+ "&" +
                            "MaxFeatures=1000&outputFormat=json&CQL_FILTER=BBOX" +
                            "("+geoName+","+lla+","+llo+","+hla+","+hlo+")");
                    Log.i(TAG, url.toString());

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(12000);
                    connection.setReadTimeout(12000);

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    String data = response.toString();

                    // Parse JSON
//                    parseJSON(data);
                    Message message = new Message();
                    message.what = HANDLER_FLAG;
                    message.obj = data;
                    handler.sendMessage(message);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseJSON(String rawData) {
        try {

            JSONObject jsonObject = new JSONObject(rawData);
            GeoJsonLayer geoJsonLayer = new GeoJsonLayer(mMap, jsonObject);

            visualizeMap(geoJsonLayer);

            Log.d(TAG, "All data loaded on map!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void setMarker(GeoJsonFeature feature) {
        String bBoxStr = feature.getProperty("bbox");
        Log.d(TAG, bBoxStr);
        String location = bBoxStr.substring(1, bBoxStr.length()-1);
        String[] bBox = location.split(",");
        double llo = Double.parseDouble(bBox[0]);
        double lla = Double.parseDouble(bBox[1]);
        double hlo = Double.parseDouble(bBox[2]);
        double hla = Double.parseDouble(bBox[3]);
        double centerLo = (llo+hlo)/2.0;
        double centerLa = (lla+hla)/2.0;

        LatLng markerAtPoint = new LatLng(centerLa,centerLo);
        String title = MapSettings.selectedMapAttribute.concat(": ").concat(feature.getProperty(MapSettings.selectedMapAttribute));
        String value = MapSettings.selectedMapClassifier.concat(": ").concat(feature.getProperty(MapSettings.selectedMapClassifier));

        Marker marker = mMap.addMarker(new MarkerOptions().position(markerAtPoint).title(title).snippet(value));

        marker.setAlpha(1);

    }

    // Shown progress dialog
    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapsActivity.this);
            progressDialog.setTitle("Receiving data from AURIN");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            openLocalFile();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "All data loaded!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    private void visualizeMap(GeoJsonLayer geoJsonLayer) {

        ArrayList<GeoJsonFeature> featuresList = Lists.newArrayList(geoJsonLayer.getFeatures());

        for (final GeoJsonFeature feature : featuresList) {
            String type = feature.getGeometry().getGeometryType();

            switch (type) {
                case "MultiPolygon": {
                    // Color
                    GeoJsonPolygonStyle style = new GeoJsonPolygonStyle();
                    // Set polygon color
                    style.setFillColor(Color.RED);
                    style.setStrokeWidth(2);
                    style.toPolygonOptions().clickable(true);
                    feature.setPolygonStyle(style);
                    geoJsonLayer.addLayerToMap();
                }

                break;
                case "Point": {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setMarker(feature);
                        }
                    });
                    break;
                }

                case "Polygon": {
                    GeoJsonPolygonStyle style = new GeoJsonPolygonStyle();
                    style.setFillColor(Color.RED);
                    style.setStrokeWidth(2);
                    style.toPolygonOptions().clickable(true);
                    feature.setPolygonStyle(style);
                    geoJsonLayer.addFeature(feature);
                    geoJsonLayer.addLayerToMap();
                    break;
                }

                case "LineString": {
                    GeoJsonLineStringStyle style = new GeoJsonLineStringStyle();
                    style.setColor(Color.BLUE);
                    style.setWidth(20);
                    feature.setLineStringStyle(style);
                    //getPosition(feature);
                    //layer.addFeature(feature);
                    geoJsonLayer.addLayerToMap();
                    break;
                }

                case "MultiLineString": {
                    GeoJsonLineStringStyle style = new GeoJsonLineStringStyle();
                    style.setColor(Color.BLUE);
                    style.setWidth(10);
                    feature.setLineStringStyle(style);
                    geoJsonLayer.addLayerToMap();
                    break;
                }

                default:
                    Toast.makeText(this, "Error during visualizing map!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
