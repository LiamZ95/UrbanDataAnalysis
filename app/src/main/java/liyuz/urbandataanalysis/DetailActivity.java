package liyuz.urbandataanalysis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    private String TAG = getClass().getSimpleName() + ": ";

    private GoogleMap mMap;
    private Button chartBtn;
    private Button mapBtn;
    private ListView detailLv;
    private Capability selectedCap;
    private FragmentManager detailFragmentManager;
    private Fragment listFragment;
    private boolean movedToFilterFragment = false;

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

        selectedCap = SelectedData.selectedCap;

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
                // Disable button after it is clicked
                chartBtn.setEnabled(false);
                mapBtn.setEnabled(true);
                Fragment chartFragment = new ChartFilterFragment();
                detailFragmentManager.beginTransaction()
                            .add(new DetailInfoFragment(), null)
                            .addToBackStack(null)
                            .replace(R.id.detail_activity_container, chartFragment, chartFragment.getTag())
                            .commit();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Disable button after it is clicked
                mapBtn.setEnabled(false);
                chartBtn.setEnabled(true);
                Fragment mapFragment = new MapFilterFragment();
                detailFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .add(new DetailInfoFragment(), null)
                        .addToBackStack(null)
                        .replace(R.id.detail_activity_container, mapFragment, mapFragment.getTag())
                        .commit();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Get the capability coordinates
        Double lla = selectedCap.capBBox.getLowerLa();
        Double hla = selectedCap.capBBox.getHigherLa();
        Double llo = selectedCap.capBBox.getLowerLon();
        Double hlo = selectedCap.capBBox.getHigherLon();

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
        // Disable navigation and open in google map app icons
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    // This method works for updating the camera view of map fragment
    void updateMap(boolean showDefaultBBox) {
        mMap.clear();
        Double lla, hla, llo, hlo;

        if (showDefaultBBox) {
            BBox defaultBBox = SelectedData.selectedCap.capBBox;
            lla = defaultBBox.getLowerLa();
            hla = defaultBBox.getHigherLa();
            llo = defaultBBox.getLowerLon();
            hlo = defaultBBox.getHigherLon();
        } else {
            BBox customizedBBox = SelectedData.selectedBBox;
            lla = customizedBBox.getLowerLa();
            hla = customizedBBox.getHigherLa();
            llo = customizedBBox.getLowerLon();
            hlo = customizedBBox.getHigherLon();
        }

        // Calculate the center of capability
        LatLng center = new LatLng((lla+hla)/2.0,(llo+hlo)/2.0);

        // Set zoom ratio
        int zoom = (int) Math.log(320/(hlo - llo)) + 1;
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

    @Override
    public void onBackPressed() {
        chartBtn.setEnabled(true);
        mapBtn.setEnabled(true);
        super.onBackPressed();
    }
}
