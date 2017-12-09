package liyuz.urbandataanalysis;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by liam on 5/12/17.
 */

public class DetailAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> detailList;
    TextView detailAdapterTitle;
    TextView detailAdapterContent;

    public DetailAdapter(Context mContext, ArrayList<String> detailList) {
        this.mContext = mContext;
        this.detailList = detailList;
    }

    @Override
    public int getCount() {
        return detailList.size();
    }

    @Override
    public Object getItem(int i) {
        return detailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mView = View.inflate(mContext, R.layout.detail_list_view, null);

        detailAdapterTitle = (TextView) mView.findViewById(R.id.detail_lv_layout_tv1);
        detailAdapterContent = (TextView) mView.findViewById(R.id.detail_lv_layout_tv2);

        String[] tempList = detailList.get(i).split("#%");

        detailAdapterTitle.setText(tempList[0]);
        detailAdapterContent.setText(tempList[1]);

        return mView;
    }
}
