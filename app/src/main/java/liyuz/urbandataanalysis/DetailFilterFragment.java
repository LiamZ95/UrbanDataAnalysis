package liyuz.urbandataanalysis;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
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


public class DetailFilterFragment extends Fragment {

    private Button attrBtn;
    private Button classBtn;
    private Button colorBtn;
    private SeekBar seekBar;
    private Button showBtn;
    private Button stateBtn, cityBtn;
    private TextView progressTv;
    private SwitchCompat BBoxSwitch;

    private TextView attributeTv, classifierTv, colorTv, stateTv, cityTv;

    private ProgressDialog progressDialog;

    private ArrayList<String> attributes = new ArrayList<>();
    private ArrayList<String> classifiers = new ArrayList<>();
    private static String[] colors = {"Material colors", "Red","Blue","Green","Gray","Purple"};

    private String selectedAttribute;
    private String selectedClassifier;
    private String selectedColor;
    private String selectedState, selectedCity;
    private String preSelectedState = GeoInfo.states[0];
    private int selectedOpacity = 70;

    private int attrCheckedItem = 0, classCheckedItem = 0, colorCheckedItem = 0, stateCheckedItem = 0,
            cityCheckedItem = 0;

//    private int preStateIndex = 0, preCityIndex = 0;
    private boolean changedLocation = false;
    private boolean useDefaultBBox = true;

    private String TAG = getClass().getSimpleName();
    private final int HANDLER_FLAG = 0;

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == HANDLER_FLAG) {

                // Update UI
                // Set the pre-selected item
                selectedState = GeoInfo.states[0];
                selectedCity = GeoInfo.act[0];
                selectedAttribute = attributes.get(0);
                selectedClassifier = classifiers.get(0);
                selectedColor = colors[0];

//                String stateTvStr = "Select State: " + selectedState;
                String stateTvStr = "Select State: Default";
                stateTv.setText(stateTvStr);
//                String cityTvStr = "Select City: " + selectedCity;
                String cityTvStr = "Select City: Default";
                cityTv.setText(cityTvStr);

                String attributeTvStr = "Selected Attribute: " + selectedAttribute;
                attributeTv.setText(attributeTvStr);
                String classifierTvStr = "Selected Classifier: " + selectedClassifier;
                classifierTv.setText(classifierTvStr);
                String colorTvStr = "Selected Color Collection: " + selectedColor;
                colorTv.setText(colorTvStr);
            }
        }
    };

    public DetailFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_filter_chart, container, false);

        // Values to be shown in textViews when the fragment is started

        // Show progress dialog
        LongOperation myTask = null;
        myTask = new LongOperation();
        myTask.execute();

        attributeTv = (TextView) mView.findViewById(R.id.chart_filter_attr_tv);
        classifierTv = (TextView) mView.findViewById(R.id.chart_filter_class_tv);
        colorTv = (TextView) mView.findViewById(R.id.chart_filter_color_tv);

        attrBtn = (Button) mView.findViewById(R.id.filter_chart_attribute_btn);
        classBtn = (Button) mView.findViewById(R.id.filter_chart_classifier_btn);
        colorBtn = (Button) mView.findViewById(R.id.filter_chart_color_btn);

        seekBar = (SeekBar) mView.findViewById(R.id.filter_chart_seek_bar);
        progressTv = (TextView) mView.findViewById(R.id.seek_progress_tv);
        showBtn = (Button) mView.findViewById(R.id.filter_chart_show_btn);

        stateTv = (TextView) mView.findViewById(R.id.chart_filter_state_tv);
        cityTv = (TextView) mView.findViewById(R.id.chart_filter_city_tv);
        stateBtn = (Button) mView.findViewById(R.id.filter_chart_state_btn);
        cityBtn = (Button) mView.findViewById(R.id.filter_chart_city_btn);

        BBoxSwitch = (SwitchCompat) mView.findViewById(R.id.filter_chart_switch);
        BBoxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    useDefaultBBox = true;
                    Toast.makeText(getContext(), "Use default bounding box", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    useDefaultBBox = false;
                    Toast.makeText(getContext(), "Use customized bounding box", Toast.LENGTH_SHORT)
                            .show();
                    String stateTvStr = "Select State: " + selectedState;
                    String cityStr = "Select City: " + selectedCity;
                    stateTv.setText(stateTvStr);
                    cityTv.setText(cityStr);
                }
            }
        });

        cityBtn.setEnabled(false);

//        String typeName = SelectedData.selectedCap.capName;
//        String urlStr = "http://openapi.aurin.org.au/wfs?request=" +
//                "DescribeFeatureType&service=WFS&version=1.1.0&TypeName="+typeName;
//        Log.i(TAG, urlStr);

        // Alert dialog for select city
        stateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select a state");

                final String[] stateArray = GeoInfo.states;
                builder.setSingleChoiceItems(stateArray, stateCheckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedState = stateArray[i];
                        Log.i(TAG, selectedState);
                        stateCheckedItem = i;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handles actions to perform when user confirm their operations

                        if (selectedState != preSelectedState) {
//                            warnState = true;
                            preSelectedState = selectedState;
                            Toast.makeText(getContext(), "You have changed state, please select a city in the new state.", Toast.LENGTH_LONG)
                                    .show();
                        }

                        stateBtn.setText(selectedState);
                        String tempStr = "Selected State: " + selectedState;
                        stateTv.setText(tempStr);

                        cityBtn.setEnabled(true);
                        final String[] cityArray = GeoInfo.getCities(selectedState);
                        selectedCity = cityArray[0];
                        cityBtn.setText(selectedCity);
                        String cityTempStr = "Selected City: " + selectedCity;
                        cityTv.setText(cityTempStr);
                        useDefaultBBox = false;

                    }
                });

                builder.setNegativeButton("Cancel", null);

                // Create and show alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Alert dialog for select city
        cityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select a city");
                // Get a array of cities of selected state
                final String[] cityArray = GeoInfo.getCities(selectedState);
                selectedCity = cityArray[0];

                builder.setSingleChoiceItems(cityArray, cityCheckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedCity = cityArray[i];
                        Log.i(TAG, selectedCity);
                        cityCheckedItem = i;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handles actions to perform when user confirm their operations
                        cityBtn.setText(selectedCity);
                        String tempStr = "Selected City: " + selectedCity;
                        cityTv.setText(tempStr);
//                        if (warnState) {
//                            warnState = false;
//                        }
                        Log.i(TAG, selectedCity);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                // Create and show alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Setting the pop up window for attributes selection
        attrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select an attribute");

                final String[] attrArray = attributes.toArray(new String[attributes.size()]);
                builder.setSingleChoiceItems(attrArray, attrCheckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedAttribute = attrArray[i];
                        attrCheckedItem = i;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handles actions to perform when user confirm their operations
                        attrBtn.setText(selectedAttribute);
                        String tempStr = "Selected Attribute: " + selectedAttribute;
                        attributeTv.setText(tempStr);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                // Create and show alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Setting the pop up window for classifier selection
        classBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select a classifier");

                final String[] classArray = classifiers.toArray(new String[classifiers.size()]);
                builder.setSingleChoiceItems(classArray, classCheckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedClassifier = classArray[i];
                        classCheckedItem = i;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handles actions to perform when user confirm their operations
                        String temp = "Select Classifier: " + selectedClassifier;
                        classBtn.setText(selectedClassifier);
                        classifierTv.setText(temp);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                // Create and show alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Setting the pop up window for color selection
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select a color");

                builder.setSingleChoiceItems(colors, colorCheckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedColor = colors[i];
                        colorCheckedItem = i;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handles actions to perform when user confirm their operations
                        String temp = "Select Color Collection: " + selectedColor;
                        colorTv.setText(temp);
                        colorBtn.setText(selectedColor);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                // Create and show alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String tvContent = "Select Opacity: " + i + "%";
                selectedOpacity = i;
                progressTv.setText(tvContent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (warnState) {
//                    Toast.makeText(getContext(), "You have changed state, please select a city in the new state.", Toast.LENGTH_LONG)
//                            .show();
//                    Log.i(TAG + " Selected State", selectedState);
//                    Log.i(TAG + " Selected City", selectedCity);
//
//                } else {
                ChartSettings.selectedChartAttribute = selectedAttribute;
                ChartSettings.selectedChartClassifier = selectedClassifier;
                ChartSettings.selectedChartColor = selectedColor;
                ChartSettings.selectedChartOpacity = selectedOpacity;

                Log.i(TAG + " Selected State", selectedState);
                Log.i(TAG + " Selected City", selectedCity);

//                Log.i(TAG + "attr: ", selectedChartAttribute);
//                Log.i(TAG + "class", selectedChartClassifier);
//                Log.i(TAG + "color", selectedChartColor);
//                Log.i(TAG + "op", String.valueOf(selectedChartOpacity));
                if (useDefaultBBox) {
                    Log.i(TAG, "Use default BBox");
                    ChartSettings.selectedBBox = SelectedData.selectedCap.capBBox;
                    Log.i(TAG + "Selected BBox", String.valueOf(ChartSettings.selectedBBox.getHigherLa()));
                } else {
                    Log.i(TAG, "Use other BBox");
                    ChartSettings.selectedBBox = GeoInfo.cityBBox.get(selectedCity);
                    Log.i(TAG + "Selected BBox", String.valueOf(ChartSettings.selectedBBox.getHigherLa()));
                }

                // Show chart in a new activity
//                Intent intent = new Intent(getActivity(), ChartActivity.class);
//                startActivity(intent);

            }
        });

        return  mView;
    }


    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Receiving and data from AURIN");
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

    // sending the http request for type descriptions of certain data set
    private void sendRequest() {
        final String typename = SelectedData.selectedCap.capName;
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
                    parseXML(data);

                    Message message = new Message();
                    message.what = HANDLER_FLAG;
                    myHandler.sendMessage(message);

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
//                                Log.d("attribute", attribute);
                                attributes.add(attribute);
                            }
                            else if (type.equals("xsd:double")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
//                                Log.d("classifier", classifier);
                                classifiers.add(classifier);
                            }
                            else if (type.equals("xsd:float")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
//                                Log.d("classifier", classifier);
                                classifiers.add(classifier);
                            }
                            else if (type.equals("xsd:int")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
//                                Log.d("classifier", classifier);
                                classifiers.add(classifier);
                            }
                            else if (type.equals("xsd:decimal")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
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
                    .open("aurin-datasource-INODE-UA_WISeR_internode_adelaide_free_wireless.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String data = stringBuilder.toString();
//            Log.d(TAG, "All data read from local xml file");
            parseXML(data);


            Message message = new Message();
            message.what = HANDLER_FLAG;
            myHandler.sendMessage(message);


            for (String classifier : classifiers) {
                Log.i(TAG, classifier);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
