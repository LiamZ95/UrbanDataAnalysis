package liyuz.urbandataanalysis;

/**
 * Created by Administrator on 12/3/2017.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CapAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Capability> capList;
    TextView capTitle;
    TextView capOrg;
    ImageView capImage;
    TextView capKeywords;

    public CapAdapter(Context context, ArrayList<Capability> capabilitiesArrayList) {
        this.mContext = context;
        this.capList = capabilitiesArrayList;
    }

    @Override
    public int getCount() {
        return capList.size();
    }

    @Override
    public Object getItem(int i) {
        return capList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mView = View.inflate(mContext, R.layout.list_view_sub, null);

        capTitle = (TextView) mView.findViewById(R.id.cap_title);
        capOrg = (TextView) mView.findViewById(R.id.cap_org);
        capImage = (ImageView) mView.findViewById(R.id.cap_image);
        capKeywords = (TextView) mView.findViewById(R.id.cap_keywords);

        capTitle.setText(capList.get(i).capTitle);
        capOrg.setText(capList.get(i).capOrganization);
        String keywordsText = "Keywords: " + capList.get(i).capKeywords;
        capKeywords.setText(keywordsText);
        capImage.setImageResource(capList.get(i).image_id);

        return mView;
    }
}
