package liyuz.urbandataanalysis;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Button chartBtn;
    private Button mapBtn;
    private ListView detailLv;
    private ArrayList<String> detailList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Capability mParam1;


    public DetailFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(Capability param1) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Capability) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Remove view of all other fragments
        if (container != null) {
            container.removeAllViews();
        }
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_detail, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.detail_map_fragment);
        mapFragment.getMapAsync(this);

        chartBtn = (Button) mView.findViewById(R.id.detail_chart_btn);
        mapBtn = (Button) mView.findViewById(R.id.detail_map_btn);
        detailLv = (ListView) mView.findViewById(R.id.detail_list_view);

        String title = "Title/" + mParam1.capTitle;
        String org = "Organization/" + mParam1.capOrganization;
        String dataType = "Data Type/" + mParam1.capGeoName;
        String abs = "Abstract/" + mParam1.capAbstracts;
        String corners = "Bounding box/" + mParam1.capCorners;

        detailList.add(title);
        detailList.add(org);
        detailList.add(dataType);
        detailList.add(abs);
        detailList.add(corners);

        DetailAdapter detailAdapter = new DetailAdapter(this.getActivity().getApplicationContext(), detailList);
        detailLv.setAdapter(detailAdapter);

        return mView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
