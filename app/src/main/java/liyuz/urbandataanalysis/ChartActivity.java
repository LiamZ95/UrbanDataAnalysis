package liyuz.urbandataanalysis;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName() + "###";
    private BarChart barChart;
    private Button btn;
    private ArrayList<String> attributes;
    private ArrayList<Double> classifiers;
    private ProgressDialog progressDialog;
    final int CHARTMSGId = 3;

    private Handler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = (BarChart) findViewById(R.id.chart_chart);
        btn = (Button) findViewById(R.id.chart_btn);

        // Display progress dialog when receiving data from AURIN
//        LongOperation myTask = null;
//        myTask = new LongOperation();
//        myTask.execute();


        final String typeName = SelectedData.selectedCap.capName;
        final String geoName = SelectedData.selectedCap.capGeoName;

        final double lla = SelectedData.selectedBBox.getLowerLa();
        final double llo = SelectedData.selectedBBox.getLowerLon();
        final double hla = SelectedData.selectedBBox.getHigherLa();
        final double hlo = SelectedData.selectedBBox.getHigherLon();

        String url = "http://openapi.aurin.org.au/wfs?" +
                "request=GetFeature&service=WFS&version=1.1.0&" +
                "TypeName="+ typeName+ "&" +
                "MaxFeatures=1000&outputFormat=json&CQL_FILTER=BBox" +
                "("+geoName+","+lla+","+llo+","+hla+","+hlo+")&PropertyName="
                + ChartSettings.selectedAttribute+","+ ChartSettings.selectedClassifier;

        Log.i(TAG, url);

    }

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
        final double lla = SelectedData.selectedBBox.getLowerLa();
        final double llo = SelectedData.selectedBBox.getLowerLon();
        final double hla = SelectedData.selectedBBox.getHigherLa();
        final double hlo = SelectedData.selectedBBox.getHigherLon();
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
                            + ChartSettings.selectedAttribute+","+ ChartSettings.selectedClassifier);

                    Log.i("ChartUrl->", url.toString());
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(8000);
//                    connection.setReadTimeout(8000);
//                    InputStream in = connection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    String data = response.toString();
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

    private void parseJson(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = new JSONArray((jsonObject.getString("features")));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                String properties = jObject.getString("properties");
                JSONObject jObject2 = new JSONObject(properties);

                String selectedAttribute = ChartSettings.selectedAttribute;
                String selectedClassifier = ChartSettings.selectedClassifier;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // This handler solves memory leaks by using weak reference
    private static class MyHandler extends Handler {
        private final int CHARTMSGID = 3;
        private final WeakReference<ChartActivity> mActivity;

        public MyHandler(ChartActivity activity) {
            mActivity = new WeakReference<ChartActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
//            if (msg.what == CHARTMSGID) {
//                String jsonData = (String) msg.obj;
//                try {
//                    parseJson(jsonData);
//
//                }
//            }
        }
    }
}
