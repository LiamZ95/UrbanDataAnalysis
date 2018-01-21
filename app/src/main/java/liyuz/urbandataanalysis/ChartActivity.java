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
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
    private final String TAG = getClass().getSimpleName();
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

    private float averageValue = 0;

    private boolean showType = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_FLAG) {
                String rawData = (String) msg.obj;
                parseJSON(rawData);
                visualizeChartPlus(barChart);
            }
        }
    };

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
                    // Expand the layout process in the background
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
//            InputStream in = assetManager
//                    .open("2_cap_bssid_latitude_chart.json");
            InputStream in = assetManager
                    .open("ad_free_wifi_type_longi.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String data = stringBuilder.toString();
            Log.d(TAG, "All data read from local json file");

            // Parse json data
            Message message = new Message();
            message.what = HANDLER_FLAG;
            message.obj = data;
            handler.sendMessage(message);
//            dataSetSize = objectIdList.size();
//            Log.i("DataSetSize", String.valueOf(dataSetSize));
//
//            for (Float f: objectIdList) {
//                Log.i("objectListItem", String.valueOf(f));
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = new JSONArray((jsonObject.getString("features")));

            String selectedAttribute = ChartSettings.selectedChartAttribute;
            String selectedClassifier = ChartSettings.selectedChartClassifier;
            float classifierValueSum = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                String properties = jObject.getString("properties");
                JSONObject propertyObj = new JSONObject(properties);

                String attributeData = propertyObj.getString(selectedAttribute);
                String classifierData = propertyObj.getString(selectedClassifier);
                float classifierValueTemp = Float.parseFloat(classifierData);
                classifierValueSum += classifierValueTemp;
                attributes.add(attributeData);
                classifiers.add(classifierValueTemp);
                // Use object id as X-Axis value
                objectIdList.add(Float.parseFloat(propertyObj.getString("objectid")));
            }

            averageValue = classifierValueSum / classifiers.size();

        } catch (JSONException e) {
            Toast.makeText(this, "JSON parse error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // All setting about bar chart
    private void visualizeChart(BarChart b, boolean showType) {
        if (showType) {
            String descStr = "Grouped data, type(X-Axis) against its total amount(Y-Axis)";
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
            b.animateXY(3000, 3000);

            // What will shown on xAxis is the item from classifier, like object
            XAxis xAxis = b.getXAxis();
            // Customize XAxis
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(true);
            xAxis.setDrawLabels(true);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setTextSize(10f);
            xAxis.setCenterAxisLabels(true);

            for (int i = 0; i < attributes.size(); i++) {

            }
        }
        else {
            String descStr = "objectid(X-Axis) against " + classifierTitle + "(Y-Axis), " +
                    "tap each bar to check its attribute";
            Description description = new Description();
            description.setText(descStr);
            b.setDescription(description);

            // Customize bar chart
            b.setDrawValueAboveBar(true);
            b.setDrawBarShadow(false);
            b.setPinchZoom(false);

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
            BarDataSet barDataSet = new BarDataSet(entryList, ChartSettings.selectedChartAttribute);

            // Set the bar color
            if (ChartSettings.selectedChartColor.equals("Material colors")) {
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            } else {
                barDataSet.setColors(ColorValues.getRandomColor(ChartSettings.selectedChartColor));
            }

            // Show value above each bar
            barDataSet.setDrawValues(true);
            BarData data = new BarData(barDataSet);

            b.setData(data);
            // Show bar chart
            b.invalidate();
        }
    }

    private void visualizeChartPlus(BarChart b) {
        String descStr = attributeTitle + "(X-Axis) against " + classifierTitle + "(Y-Axis), " +
                "tap each bar to check detail";
        Description description = new Description();
        description.setText(descStr);
        b.setDescription(description);

        // Customize bar chart
        b.setDrawValueAboveBar(true);
        b.setDrawBarShadow(false);
        b.setPinchZoom(false);

        b.setTouchEnabled(true);
        b.setDragEnabled(true);
        b.setScaleEnabled(true);

        LimitLine ll = new LimitLine(averageValue, "Average： " + String.valueOf(averageValue));
        ll.setLineColor(Color.RED);
        ll.setLineWidth(2f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
        b.setHighlightFullBarEnabled(true);

        for (int i = 0; i < classifiers.size(); i++) {
            entryList.add(new BarEntry(objectIdList.get(i), classifiers.get(i)));
        }

        YAxis leftYAxis = b.getAxisLeft();
//        leftYAxis.setDrawLimitLinesBehindData(true);
        leftYAxis.addLimitLine(ll);

        BarDataSet barDataSet = new BarDataSet(entryList, ChartSettings.selectedChartAttribute);

        // Set the bar color
        if (ChartSettings.selectedChartColor.equals("Material colors")) {
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        } else {
            barDataSet.setColors(ColorValues.getColorInt(ChartSettings.selectedChartColor));
        }

        // Show value above each bar
        barDataSet.setDrawValues(true);
        BarData data = new BarData(barDataSet);

        b.setData(data);
        // Show bar chart
        b.animateXY(3000, 3000);

    }

}
