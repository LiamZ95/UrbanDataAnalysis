package liyuz.urbandataanalysis;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.net.ContentHandlerFactory;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailMapFilterFragment extends Fragment {

    private Button areaBtn;
    private Button attrBtn;
    private Button classBtn;
    private Button lvlBtn;
    private Button colorBtn;
    private SeekBar seekBar;
    private Button showBtn;
    private TextView progressTv;

    private TextView attributeTv, classifierTv, lvlTv, colorTv;

    private ProgressDialog progressDialog;

    private ArrayList<String> attributes = new ArrayList<>();
    private ArrayList<String> classifiers = new ArrayList<>();
    private static String[] levels = {"1","2","3","4","5","6"};
    private static String[] colors = {"Material colors", "Red","Blue","Green","Gray","Purple"};
    private String selectedState;

    private String selectedAttribute;
    private String selectedClassifier;
    private String selectedLevel;
    private String selectedColor;
    private int selectedOpacity = 70;

    private int attrCheckedItem = 0;
    private int classCheckedItem = 0;
    private int lvlCheckedItem = 0;
    private int colorCheckedItem = 0;

    private Boolean hasSelectedOtherBBox = false;
    private final int HANDLERFLAG = 0;

    private String TAG = getClass().getSimpleName() + "###";

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLERFLAG) {
                // Update UI
                selectedAttribute = attributes.get(0);
                selectedClassifier = classifiers.get(0);
                selectedLevel = levels[0];
                selectedColor = colors[0];

                String attributeTvStr = "Selected Attribute: " + selectedAttribute;
                attributeTv.setText(attributeTvStr);
                String classifierTvStr = "Selected Classifier: " + selectedClassifier;
                classifierTv.setText(classifierTvStr);
                String lvlTvStr = "Selected Class Level: " + selectedLevel;
                lvlTv.setText(lvlTvStr);
                String colorTvStr = "Selected Color Collection: " + selectedColor;
                colorTv.setText(colorTvStr);
            }
        }
    };

    public DetailMapFilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_detail_map_filter, container, false);

        // Load data
        // Show progress dialog
        LongOperation myTask = null;
        myTask = new LongOperation();
        myTask.execute();

        // Initializing
        attributeTv = (TextView) mView.findViewById(R.id.map_filter_attr_tv);
        classifierTv = (TextView) mView.findViewById(R.id.map_filter_class_tv);
        lvlTv = (TextView) mView.findViewById(R.id.map_filter_lvl_tv);
        colorTv = (TextView) mView.findViewById(R.id.map_filter_color_tv);

        areaBtn = (Button) mView.findViewById(R.id.filter_map_area_btn);
        attrBtn = (Button) mView.findViewById(R.id.filter_map_attribute_btn);
        classBtn = (Button) mView.findViewById(R.id.filter_map_classifier_btn);
        lvlBtn = (Button) mView.findViewById(R.id.filter_map_level_btn);
        colorBtn = (Button) mView.findViewById(R.id.filter_map_color_btn);
        seekBar = (SeekBar) mView.findViewById(R.id.filter_map_seek_bar);
        progressTv = (TextView) mView.findViewById(R.id.map_seek_progress_tv);
        showBtn = (Button) mView.findViewById(R.id.filter_map_show_btn);

//        String typeName = SelectedData.selectedCap.capName;
//        String urlStr = "http://openapi.aurin.org.au/wfs?request=" +
//                "DescribeFeatureType&service=WFS&version=1.1.0&TypeName="+typeName;
//        Log.i(TAG, urlStr);


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

        // Setting the pop up window for class level selection
        lvlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select a class level");

                builder.setSingleChoiceItems(levels, lvlCheckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedLevel = levels[i];
                        lvlCheckedItem = i;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handles actions to perform when user confirm their operations
                        String temp = "Select Class Level: " + selectedLevel;
                        lvlTv.setText(temp);
                        lvlBtn.setText(selectedLevel);

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

                MapSettings.selectedMapAttribute = selectedAttribute;
                MapSettings.selectedMapClassifier = selectedClassifier;
                MapSettings.selectedMapLevel = selectedLevel;
                MapSettings.selectedMapColor = selectedColor;
                MapSettings.selectedMapOpacity = selectedOpacity;

//                Log.i(TAG + "attr: ", selectedChartAttribute);
//                Log.i(TAG + "class", selectedChartClassifier);
//                Log.i(TAG + "lvl", selectedChartLevel);
//                Log.i(TAG + "color", selectedChartColor);
//                Log.i(TAG + "op", String.valueOf(selectedChartOpacity));

                if (!hasSelectedOtherBBox) {
                    SelectedData.selectedBBox = SelectedData.selectedCap.capBBox;
                }

                // Show chart in a new activity
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });

        return mView;
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
            sendRequest();
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
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
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
                    message.what = HANDLERFLAG;
                    myHandler.sendMessage(message);

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
                                attributes.add(attribute);
                            }
                            else if (type.equals("xsd:double")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
                                classifiers.add(classifier);
                            }
                            else if (type.equals("xsd:float")){
                                classifier = xmlPullParser.getAttributeValue(null, "name");
                                classifiers.add(classifier);
                            }
                            else if (type.equals("xsd:int")){
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

}
