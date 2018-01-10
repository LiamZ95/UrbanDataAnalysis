package liyuz.urbandataanalysis;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailInfoFragment extends Fragment {

    private TextView infoTv11, infoTv12, infoTv21, infoTv22, infoTv31, infoTv32, infoTv41, infoTv42, infoTv51, infoTv52;
    private Capability selectedCap;

    public DetailInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_detail_info, container, false);

        infoTv11 = mView.findViewById(R.id.detail_info_tv1_1);
        infoTv12 = mView.findViewById(R.id.detail_info_tv1_2);
        infoTv21 = mView.findViewById(R.id.detail_info_tv2_1);
        infoTv22 = mView.findViewById(R.id.detail_info_tv2_2);
        infoTv31 = mView.findViewById(R.id.detail_info_tv3_1);
        infoTv32 = mView.findViewById(R.id.detail_info_tv3_2);
        infoTv41 = mView.findViewById(R.id.detail_info_tv4_1);
        infoTv42 = mView.findViewById(R.id.detail_info_tv4_2);
        infoTv51 = mView.findViewById(R.id.detail_info_tv5_1);
        infoTv52 = mView.findViewById(R.id.detail_info_tv5_2);

        selectedCap = SelectedData.selectedCap;
        String[] cornerList = selectedCap.capCorners.split(" ");
        Double[] cornerListRounded = new Double[]{roundOff(cornerList[0]), roundOff(cornerList[1]),
                roundOff(cornerList[2]), roundOff(cornerList[3])};
        String cornersContent = Arrays.toString(cornerListRounded);

        infoTv11.setText("Title");
        infoTv12.setText(selectedCap.capTitle);
        infoTv21.setText("Organization");
        infoTv22.setText(selectedCap.capOrganization);
        infoTv31.setText("Data type");
        infoTv32.setText(selectedCap.capGeoName);
        infoTv41.setText("Abstracts");
        infoTv42.setText(selectedCap.capAbstracts);
        infoTv51.setText("Bounding box");
        infoTv52.setText(cornersContent);

        return mView;
    }

    public Double roundOff(String in) {
        return Math.round(Double.parseDouble(in) * 100.0) / 100.0;
    }

}
