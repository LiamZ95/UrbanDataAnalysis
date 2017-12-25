package liyuz.urbandataanalysis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    private String TAG = getClass().getSimpleName() + ": ";

    private GoogleMap mMap;
    private Button chartBtn;
    private Button mapBtn;
    private ListView detailLv;
    private Capability selectedCap;
    private ArrayList<String> detailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initializing google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_activity_map_fragment);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        selectedCap = (Capability) intent.getSerializableExtra("SelectedCapability");

        chartBtn = (Button) findViewById(R.id.detail_activity_chart_btn);
        mapBtn = (Button) findViewById(R.id.detail_activity_map_btn);
        detailLv = (ListView) findViewById(R.id.detail_activity_list_view);

        Log.i(TAG, selectedCap.capTitle);
        Log.i(TAG, selectedCap.capAbstracts);

        String[] cornerList = selectedCap.capCorners.split(" ");
        Double[] cornerListRounded = new Double[]{roundOff(cornerList[0]), roundOff(cornerList[1]),
                roundOff(cornerList[2]), roundOff(cornerList[3])};
        String cornersContent = Arrays.toString(cornerListRounded);

        String title = "Title#%" + selectedCap.capTitle;
        String org = "Organization#%" + selectedCap.capOrganization;
        String dataType = "Data type#%" + selectedCap.capGeoName;
        String abs = "Abstract#%" + selectedCap.capAbstracts.split("Abstract: ")[1];
        String corners = "Bounding box#%" + cornersContent;

        detailList.add(title);
        detailList.add(org);
        detailList.add(dataType);
        detailList.add(abs);
        detailList.add(corners);

        DetailAdapter detailAdapter = new DetailAdapter(getApplicationContext(), detailList);
        detailLv.setAdapter(detailAdapter);

        chartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Going to chart", Toast.LENGTH_SHORT).show();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Going to map", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                intent.putExtra("SelectedCapabilityForFilter", selectedCap);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Get the capability coordinates
        Double lla = selectedCap.capBbox.getLowerLa();
        Double hla = selectedCap.capBbox.getHigherLa();
        Double llo = selectedCap.capBbox.getLowerLon();
        Double hlo = selectedCap.capBbox.getHigherLon();

        // Calculate the center of capability
        LatLng center = new LatLng((lla+hla)/2.0,(llo+hlo)/2.0);

        // Set zoom ratio
        int zoom = (int) Math.log(320/(hlo - llo)) + 1;

        // Set the map
        mMap.addMarker(new MarkerOptions().position(center).title(selectedCap.capTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        mMap.isMyLocationEnabled();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));

        // Draw polyline to show area of Bbox
        mMap.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(lla, llo))
                .add(new LatLng(hla, llo))
                .add(new LatLng(hla, hlo))
                .add(new LatLng(lla, hlo))
                .add(new LatLng(lla, llo))
        );
    }

    public Double roundOff(String in) {
        return Math.round(Double.parseDouble(in) * 100.0) / 100.0;
    }
}
