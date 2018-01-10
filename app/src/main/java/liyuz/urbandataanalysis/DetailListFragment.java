package liyuz.urbandataanalysis;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailListFragment extends Fragment {
    private ListView detailLv;
    private ArrayList<String> detailList;
    private Capability selectedCap;
    public DetailListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_detail_list, container, false);
        detailLv = (ListView) mView.findViewById(R.id.detail_fragment_list);

        selectedCap = SelectedData.seletedCap;


        String[] cornerList = selectedCap.capCorners.split(" ");
        Double[] cornerListRounded = new Double[]{roundOff(cornerList[0]), roundOff(cornerList[1]),
                roundOff(cornerList[2]), roundOff(cornerList[3])};

//        SelectedData.selectedBbox.setLowerLa();
//        SelectedData.selectedBbox.setLowerLon();
//        SelectedData.selectedBbox.setHigherLa();
//        SelectedData.selectedBbox.setHigherLon();

        String cornersContent = Arrays.toString(cornerListRounded);

        String title = "Title#%" + selectedCap.capTitle;
        String org = "Organization#%" + selectedCap.capOrganization;
        String dataType = "Data type#%" + selectedCap.capGeoName;
        String abs = "Abstract#%" + selectedCap.capAbstracts.split("Abstract: ")[1];
        String corners = "Bounding box#%" + cornersContent;

        detailList = new ArrayList<>();
        detailList.add(title);
        detailList.add(org);
        detailList.add(dataType);
        detailList.add(abs);
        detailList.add(corners);

        DetailAdapter detailAdapter = new DetailAdapter(getActivity().getApplicationContext(), detailList);
        detailLv.setAdapter(detailAdapter);

        return mView;
    }

    public Double roundOff(String in) {
        return Math.round(Double.parseDouble(in) * 100.0) / 100.0;
    }

}
