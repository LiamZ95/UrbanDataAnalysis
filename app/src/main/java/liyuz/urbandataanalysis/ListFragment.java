package liyuz.urbandataanalysis;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();
    private ListView lv;
    private ArrayList<Capability> fragmentCapList;
    private CapAdapter mainAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_list, container, false);
        lv = mView.findViewById(R.id.list_fragment_list);
//        Log.i(TAG + "caps in list fragment", String.valueOf(AllDataSets.capList.size()));
        fragmentCapList = new ArrayList<>(AllDataSets.capList);
//        Log.i(TAG + "cap num after add: ", String.valueOf(fragmentCapList.size()));

        mainAdapter = new CapAdapter(this.getActivity().getApplicationContext(), fragmentCapList);
        lv.setAdapter(mainAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Capability selectedCap = fragmentCapList.get(i);

                SelectedData.selectedCap = selectedCap;

                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra("SelectedCapability", selectedCap);
                startActivity(intent);


            }
        });
        return mView;
    }

    void changeList(ArrayList<Capability> filteredList) {
//        CapAdapter newCapAdapter = new CapAdapter(this.getActivity().getApplicationContext(), filteredList);
//        lv.setAdapter(newCapAdapter);
        mainAdapter.refresh(filteredList);
    }

    void restoreList() {
//        lv.setAdapter(mainAdapter);
        mainAdapter.restore();
    }

}
