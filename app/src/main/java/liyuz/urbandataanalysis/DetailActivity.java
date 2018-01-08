package liyuz.urbandataanalysis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    private String TAG = getClass().getSimpleName() + ": ";

    private GoogleMap mMap;
    private Button chartBtn;
    private Button mapBtn;
    private ListView detailLv;
    private Capability selectedCap;
    private FragmentManager detailFragmentManager;
    private Fragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initializing google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_activity_map_fragment);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();

        detailFragmentManager = getSupportFragmentManager();

//        selectedCap = (Capability) intent.getSerializableExtra("SelectedCapability");
        selectedCap = SelectedCap.seletedCap;

        chartBtn = (Button) findViewById(R.id.detail_activity_chart_btn);
        mapBtn = (Button) findViewById(R.id.detail_activity_map_btn);

        // Set default fragment in the activity
        listFragment = new DetailInfoFragment();
        detailFragmentManager.beginTransaction()
                .replace(R.id.detail_activity_container, listFragment)
                .commit();

        chartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment chartFragment = new FilterChartFragment();
                detailFragmentManager.beginTransaction()
                        .add(new DetailListFragment(), "previousFragment")
                        .addToBackStack("previousFragment")
                        .replace(R.id.detail_activity_container, chartFragment, chartFragment.getTag())
                        .commit();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
//                intent.putExtra("SelectedCapabilityForFilter", selectedCap);
//                startActivity(intent);


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
}
