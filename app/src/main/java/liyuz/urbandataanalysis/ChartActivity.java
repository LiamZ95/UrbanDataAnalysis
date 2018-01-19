package liyuz.urbandataanalysis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
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

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private final String TAG = getClass().getSimpleName() + "###";
    private BarChart barChart;
    private ArrayList<String> attributes = new ArrayList<>();
    private ArrayList<Float> classifiers = new ArrayList<>();
    private ArrayList<Float> objectIdList = new ArrayList<>();
    private ArrayList<BarEntry> entryList = new ArrayList<>();
    private ProgressDialog progressDialog;

    private TextView xTv, yTv, attrTv;
    private SwitchCompat switchCompat;
    private ExpandableLayout expandableLayout;

    private String attributeTitle = ChartSettings.selectedChartAttribute;
    private String classifierTitle = ChartSettings.selectedChartClassifier;
    private final int HANDLER_FLAG = 0;

    private int dataSetSize;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_FLAG) {
                visualizeChart(barChart);
            }
        }
    };


//    private Handler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = (BarChart) findViewById(R.id.chart_chart);

        // Display progress dialog when receiving data from AURIN
        LongOperation myTask = null;
        myTask = new LongOperation();
        myTask.execute();

        // Instantiate
        xTv = (TextView)findViewById(R.id.chart_x_content_tv);
        yTv = (TextView)findViewById(R.id.chart_y_content_tv);
        attrTv = (TextView)findViewById(R.id.chart_attr_content_tv);
        switchCompat = (SwitchCompat) findViewById(R.id.chart_switch);
        expandableLayout = (ExpandableLayout) findViewById(R.id.chart_expandable_lo);

        xTv.setText("objectid");
        yTv.setText(classifierTitle);
        attrTv.setText(attributeTitle);

        // Set chart behaviour when bars are selected
        barChart.setOnChartValueSelectedListener(this);
        // Set the action when switch is changed
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    expandableLayout.expand();
                } else {
                    expandableLayout.collapse();
                }
            }
        });

    }

    // Define task to execute when barChart item is selected
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int i = objectIdList.indexOf(e.getX());
//        Toast.makeText(this, "Entry touched", Toast.LENGTH_SHORT).show();

        Log.i("index: ", String.valueOf(i));
        String xTvStr = "objectid: " + String.valueOf(Math.round(objectIdList.get(i)));
        xTv.setText(xTvStr);
        String yTvStr = classifierTitle + ": " + String.valueOf(classifiers.get(i));
        yTv.setText(yTvStr);
        String attributeStr = attributeTitle + ": " + attributes.get(i);
        attrTv.setText(attributeStr);

    }

    @Override
    public void onNothingSelected() {
        attrTv.setText("No entry selected");
    }


    // Shown progress dialog
    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ChartActivity.this);
            progressDialog.setTitle("Receiving data from AURIN");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
//            sendRequest();
            openLocalFile();
            Message message = new Message();
            message.what = HANDLER_FLAG;
            handler.sendMessage(message);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "All data loaded!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void sendRequest() {

        final String typeName = SelectedData.selectedCap.capName;
        final String geoName = SelectedData.selectedCap.capGeoName;
        final double lla = ChartSettings.selectedBBox.getLowerLa();
        final double llo = ChartSettings.selectedBBox.getLowerLon();
        final double hla = ChartSettings.selectedBBox.getHigherLa();
        final double hlo = ChartSettings.selectedBBox.getHigherLon();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection;
                Authenticator.setDefault (new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication ("student", "dj78dfGF".toCharArray());
                    }
                });
                try{
                    URL url = new URL("http://openapi.aurin.org.au/wfs?" +
                            "request=GetFeature&service=WFS&version=1.1.0&" +
                            "TypeName="+ typeName+ "&" +
                            "MaxFeatures=1000&outputFormat=json&CQL_FILTER=BBox" +
                            "("+geoName+","+lla+","+llo+","+hla+","+hlo+")&PropertyName="
                            + ChartSettings.selectedChartAttribute +","+ ChartSettings.selectedChartClassifier);

                    Log.i("ChartUrl->", url.toString());

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String data = response.toString();
//                    Message message = new Message();
//                    message.what = CHARTMSGId;
//                    message.obj = data;
//                    myHandler.sendMessage(message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openLocalFile() {
        AssetManager assetManager = getApplicationContext().getAssets();

        final String typeName = SelectedData.selectedCap.capName;
        final String geoName = SelectedData.selectedCap.capGeoName;

        final double lla = ChartSettings.selectedBBox.getLowerLa();
        final double llo = ChartSettings.selectedBBox.getLowerLon();
        final double hla = ChartSettings.selectedBBox.getHigherLa();
        final double hlo = ChartSettings.selectedBBox.getHigherLon();

        String url = "http://openapi.aurin.org.au/wfs?" +
                "request=GetFeature&service=WFS&version=1.1.0&" +
                "TypeName="+ typeName+ "&" +
                "MaxFeatures=1000&outputFormat=json&CQL_FILTER=BBox" +
                "("+geoName+","+lla+","+llo+","+hla+","+hlo+")&PropertyName="
                + ChartSettings.selectedChartAttribute +","+ ChartSettings.selectedChartClassifier;

        Log.i(TAG + "Open local file", url);

        try{
            InputStream in = assetManager
                    .open("2_cap_bssid_latitude_chart.json");
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
            dataSetSize = objectIdList.size();
            Log.i("DataSetSize", String.valueOf(dataSetSize));

            for (Float f: objectIdList) {
                Log.i("objectListItem", String.valueOf(f));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = new JSONArray((jsonObject.getString("features")));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                String properties = jObject.getString("properties");
                JSONObject propertyObj = new JSONObject(properties);

                String selectedAttribute = ChartSettings.selectedChartAttribute;
                String selectedClassifier = ChartSettings.selectedChartClassifier;

                String attributeData = propertyObj.getString(selectedAttribute);
                String classifierData = propertyObj.getString(selectedClassifier);

                attributes.add(attributeData);
                classifiers.add(Float.parseFloat(classifierData));
                // Use object id as X-Axis value
                objectIdList.add(Float.parseFloat(propertyObj.getString("objectid")));

//                Log.i(TAG, String.valueOf(i));

            }
        } catch (JSONException e) {
            Toast.makeText(this, "JSON parse error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // This handler solves memory leaks by using weak reference
//    private static class MyHandler extends Handler {
//        private final int CHARTMSGID = 3;
//        private final WeakReference<ChartActivity> mActivity;
//
//        public MyHandler(ChartActivity activity) {
//            mActivity = new WeakReference<ChartActivity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
////            if (msg.what == CHARTMSGID) {
////                String jsonData = (String) msg.obj;
////                try {
////                    parseJson(jsonData);
////
////                }
////            }
//        }
//    }


    // All setting about bar chart
    private void visualizeChart(BarChart b) {
        // Set background color
//        b.setBackgroundColor(Color.parseColor("#00673d"));
        // Set description for the chart
        String descStr = "objectid(X-Axis) against " + classifierTitle + "(Y-Axis), " +
                "tap each bar to check its attribute";
        Description description = new Description();
        description.setText(descStr);
        b.setDescription(description);

        // Customize bar chart
        b.setDrawValueAboveBar(true);
        b.setDrawBarShadow(false);

        b.setTouchEnabled(true);
        b.setDragEnabled(true);
        b.setScaleEnabled(true);

        b.setHighlightFullBarEnabled(true);
        b.animateXY(1000, 1000);

        // What will shown on xAxis is the item from classifier, like object
        XAxis xAxis = b.getXAxis();
        // Customize XAxis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(10f);
        xAxis.setCenterAxisLabels(true);

        for (int i = 0; i < classifiers.size(); i++) {
            entryList.add(new BarEntry(objectIdList.get(i), classifiers.get(i)));
        }
        Log.i(TAG, "All entry added");
        BarDataSet barDataSet = new BarDataSet(entryList, "Tap each entry to view attribute");

        // Set the bar color
        if (ChartSettings.selectedChartColor != "Material colors") {
            switch (ChartSettings.selectedChartColor) {
                case "Red":
                    barDataSet.setColors(Color.parseColor("#ff0000"));
                    break;
                case "Blue":
                    barDataSet.setColors(Color.parseColor("##33ccff"));
                    break;
                case "Green":
                    barDataSet.setColors(Color.parseColor("##009900"));
                    break;
                case "Gray":
                    barDataSet.setColors(Color.parseColor("#c2c2d6"));
                    break;
                case "Purple":
                    barDataSet.setColors(Color.parseColor("#9900cc"));
                    break;
                default:
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    break;
            }
        }
        else {
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        }

        // Show value above each bar
        barDataSet.setDrawValues(true);
        BarData data = new BarData(barDataSet);

        b.setData(data);
        // Show bar chart
        b.invalidate();

    }

}
