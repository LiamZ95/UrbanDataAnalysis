package liyuz.urbandataanalysis;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;


public class FilterChartFragment extends Fragment {

//    private Button areaBtn;
    private Button areaBtn;
    private Button attrBtn;
    private Button clasBtn;
    private Button lvlBtn;
    private Button colorBtn;

    private ProgressDialog progressDialog;

    private ArrayList<String> attributes = new ArrayList<>();
    private ArrayList<String> classifiers = new ArrayList<>();
    private static String[] colors = {"Red","Blue","Green","Gray","Purple"};
    private static String[] level = {"1","2","3","4","5","6"};
    private String selectedState;

    private String selectedAttribute;
    private String selectedClassifier;
    private String seletedLevel;
    private String selectedColor;

    public static final int SHOW_RESPONSE = 0;

    private int checkedItem = 0;

    private String TAG = getClass().getSimpleName() + "### ";

//    private Handler chartFragmentHandler = new Handler(){
//        public void handleMessage(Message msg){
//            switch (msg.what) {
//                case SHOW_RESPONSE:
//                    String response = (String) msg.obj;
//                    parseXML(response);
//            }
//        }
//    };

    public FilterChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_filter_chart, container, false);

        openLocalFile();

        areaBtn = (Button) mView.findViewById(R.id.filter_chart_area_btn);
        attrBtn = (Button) mView.findViewById(R.id.filter_chart_attribute_btn);
        clasBtn = (Button) mView.findViewById(R.id.filter_chart_classifier_btn);
        lvlBtn = (Button) mView.findViewById(R.id.filter_chart_level_btn);
        colorBtn = (Button) mView.findViewById(R.id.filter_chart_color_btn);

//        areaBtn.setOnClickListener(this);
//        attrBtn.setOnClickListener(this);
//        clasBtn.setOnClickListener(this);
//        lvlBtn.setOnClickListener(this);
//        colorBtn.setOnClickListener(this);

        // Get data


        // Show progress dialog
//        FilterProgressOperation myTask = null;
//        myTask = new FilterProgressOperation();
//        myTask.execute();

        // Setting the pop up window for attributes selection

        final String[] animals = {"horse", "cow", "camel", "sheep", "goat"};

        attrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select an attribute");

                builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Selected an attribute", Toast.LENGTH_SHORT).show();
                        selectedAttribute = animals[i];
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handles actions to perform when user confirm their operations

                    }
                });

                builder.setNegativeButton("Cancel", null);

                // Create and show alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
//
//        clasBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                builder.setTitle("Select an classifier");
//                builder.setSingleChoiceItems(classifiers.toArray(new String[classifiers.size()]), checkedItem, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getActivity(), "Selected an classifier", Toast.LENGTH_SHORT).show();
//                        selectedClassifier = classifiers.get(i);
//                        clasBtn.setText(selectedClassifier);
//                    }
//                });
//            }
//        });
        return  mView;
    }

//    @Override
//    public void onClick(View view) {
//
//        builder = new AlertDialog.Builder(getActivity());
//
//        switch (view.getId()) {
//            case R.id.filter_chart_area_btn:
//                Toast.makeText(getActivity(), "Area button clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.filter_chart_attribute_btn:
//                Toast.makeText(getActivity(), "Attribute button clicked", Toast.LENGTH_SHORT).show();
////                builder.setTitle("Choose an attribute");
//                break;
//            case R.id.filter_chart_classifier_btn:
//                Toast.makeText(getActivity(), "Classifier button clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.filter_chart_color_btn:
//                Toast.makeText(getActivity(), "Color button clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.filter_chart_level_btn:
//                Toast.makeText(getActivity(), "Level button clicked", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }


    private class FilterProgressOperation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity());
            Log.d(TAG, "progressDialog getActivity");
            progressDialog.setTitle("Receiving and data from AURIN");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            attributes.add("No attributes");
            classifiers.add("No classifier");
        }

        @Override
        protected String doInBackground(String... strings) {
            sendRequest();
            Log.d(TAG, "Sent request");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }

    // sending the http request for type descriptions of certain data set
    private void sendRequest() {
        final String typename = SelectedCap.seletedCap.capName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Authenticator.setDefault (new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication ("student", "dj78dfGF".toCharArray());
                    }
                });
                try{
                    URL url = new URL("http://openapi.aurin.org.au/wfs?request=" +
                            "DescribeFeatureType&service=WFS&version=1.1.0&TypeName="+typename);
                    Log.d(TAG, url.toString());

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
//                    parseXML(data);

//                    Message message = new Message();
//                    message.what = SHOW_RESPONSE;
//                    message.obj = data;
//                    chartFragmentHandler.sendMessage(message);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    // paring the xml with pull methods.
    private void parseXML (String xmlData) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();

            String attribute;
            String classifier;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if ("xsd:element".equals(nodeName)) {
                            String type = xmlPullParser.getAttributeValue(null,"type");
                            if (type.equals("xsd:string")){
                                attribute = xmlPullParser.getAttributeValue(null,"name");
                                Log.d("attribute", attribute);
                                attributes.add(attribute);
                            }
                            else if (type.equals("xsd:double")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
                                Log.d("classifier", classifier);
                                classifiers.add(classifier);
                            }
                            else if (type.equals("xsd:float")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
                                Log.d("classifier", classifier);
                                classifiers.add(classifier);
                            }
                            else if (type.equals("xsd:int")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
                                Log.d("classifier", classifier);
                                classifiers.add(classifier);
                            }
                            else {
                                classifier = xmlPullParser.getAttributeValue(null, "name");
                                Log.d("classifier", classifier);
                                classifiers.add(classifier);
                            }
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if ("xsd:element".equals(nodeName))
                            break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openLocalFile() {
        AssetManager assetManager = getActivity().getApplicationContext().getAssets();
        try{
            InputStream in = assetManager
                    .open("aurin-datasource-SA_Govt_RenewalSA-UA_WISeR_renewalsa_age_household_ref_person_2011.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String data = stringBuilder.toString();
            Log.d(TAG, "All data read from local xml file");
            parseXML(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
