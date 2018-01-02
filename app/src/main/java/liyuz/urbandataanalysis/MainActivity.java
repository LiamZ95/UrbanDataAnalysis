package liyuz.urbandataanalysis;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.util.Log;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Web2Fragment.OnFragmentInteractionListener {

    private final String TAG = getClass().getSimpleName();
    private ProgressDialog progressDialog;
    private Boolean homeFlag = false;
    private Boolean hasTransac = false;
    private int capCount = 0;
    private Fragment fragment = null;
    private Fragment homeFragment;
    private Fragment currentShownFragment;
    private FragmentManager mainFragmentManager;
    MaterialSearchView materialSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragment manager
        mainFragmentManager = getSupportFragmentManager();

        // Send http request and parse the received xml data
//        sendRequestWithURLConnection();

        // Setting for progress dialog
        LongOperation myTask = null;
        myTask = new LongOperation();
        myTask.execute();

        // Default settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // For search bar
        getSupportActionBar().setTitle("Urban Data Analysis ");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // For navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Settings for the search bar
        materialSearchView = (MaterialSearchView)findViewById(R.id.search_view);

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                currentShownFragment = getCurrentFragment();
                if (!(currentShownFragment instanceof ListFragment)) {
                    mainFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, homeFragment, homeFragment.getTag())
                            .commit();
                }
            }

            @Override
            public void onSearchViewClosed() {
                // If search view is closed, restore to original list view
                ListFragment searchFragment = (ListFragment) getCurrentFragment();
                searchFragment.restoreList();
            }
        });

        // Search text matching and updating listView in the fragment
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            // This function
            public boolean onQueryTextChange(String newText) {
                ListFragment searchFragment = (ListFragment) getCurrentFragment();
                if (newText != null && !newText.isEmpty()) {
                    String validText = newText.toLowerCase();
                    ArrayList<Capability> allCaps = new ArrayList<>(AllDataSets.capList);
                    ArrayList<Capability> targetCaps = new ArrayList<>();

                    String[] inputKeywordsArray;
                    if (validText.contains(" ")) {
                        inputKeywordsArray = validText.split(" ");
                    } else {
                        inputKeywordsArray = new String[]{validText};
                    }

                    for (Capability cap : allCaps) {
                        Boolean isTarget = false;
                        String capKw = cap.capKeywords.toLowerCase();
                        for (String inputKw : inputKeywordsArray) {
                            if (capKw.contains(inputKw)) {
                                isTarget = true;
                            }
                        }
                        if (isTarget) {
                            targetCaps.add(cap);
                        }
                    }
                    // Update list view
                    searchFragment.changeList(targetCaps);
                } else {
                    // Restore
                    searchFragment.restoreList();
                }
                return true;
            }
        });
    }

    // Defined the behavior when back button is hit
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (materialSearchView.isSearchOpen()) {
                materialSearchView.closeSearch();
            } else {
                if (hasTransac) {
                    homeFlag = false;
                    hasTransac = false;
                    super.onBackPressed();
                }
                else {
                    if (!homeFlag) {
                        homeFlag = true;
                        Toast.makeText(this, "Double click to exit the app", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        super.onBackPressed();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Modified for search bar
        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Define fragments which should change to in the navigation drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Clear fragment stack
        mainFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (id == R.id.nav_list) {
            Toast.makeText(this, "Going to Data List", Toast.LENGTH_SHORT).show();
            fragment = new ListFragment();
        } else if (id == R.id.nav_web) {
            Toast.makeText(this, "Going to AURIN", Toast.LENGTH_SHORT).show();
            fragment = Web2Fragment.newInstance("https://aurin.org.au/");
        } else if (id == R.id.nav_terms) {
            Toast.makeText(this, "Going to terms", Toast.LENGTH_SHORT).show();
            fragment = Web2Fragment.newInstance("https://aurin.org.au/compliance/aurin-terms-of-use/");
        } else if (id == R.id.nav_copyright) {
            Toast.makeText(this, "Going to copyrights", Toast.LENGTH_SHORT).show();
            fragment = Web2Fragment.newInstance("https://aurin.org.au/compliance/copyright-and-attribution/");
        } else if (id == R.id.nav_help) {
            Toast.makeText(this, "Going to help", Toast.LENGTH_SHORT).show();
            fragment = Web2Fragment.newInstance("https://docs.aurin.org.au");
        } else if (id == R.id.nav_issue) {
            Toast.makeText(this, "Going to report issue", Toast.LENGTH_SHORT).show();
            fragment = Web2Fragment.newInstance("https://docs.aurin.org.au/aurin-online-bug-report/");
        } else if (id == R.id.nav_twitter) {
            Toast.makeText(this, "Going to twitter", Toast.LENGTH_SHORT).show();
            fragment = Web2Fragment.newInstance("https://mobile.twitter.com/aurin_org_au");
        } else if (id == R.id.nav_facebook) {
            Toast.makeText(this, "Going to facebook", Toast.LENGTH_SHORT).show();
            fragment = Web2Fragment.newInstance("https://m.facebook.com/aurin.org.au/");
        }

        if (fragment != null) {
            mainFragmentManager.beginTransaction()
                    .add(new ListFragment(), "HomeFragment")
                    .addToBackStack("HomeFragment") // Add home fragment to stack
                    .replace(R.id.fragment_container, fragment, fragment.getTag())
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        hasTransac = true;
        return true;
    }

    // sending http request for all dataset, and then parse the received data
    private void sendRequest() {
        // Create a new thread for HttpURLConnection
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection;
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        // Default API key
                        return new PasswordAuthentication ("student", "dj78dfGF".toCharArray());
                    }
                });
                try{
                    URL url = new URL("http://openapi.aurin.org.au/wfs?service=WFS&version=1.1.0&request=GetCapabilities");
                    Log.i("Main####", "Sent request to AURIN");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    // Set big timeout for testing, was 8000
                    connection.setConnectTimeout(60000);
                    connection.setReadTimeout(60000);

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // Store all data in the string
                    String data = response.toString();
                    Log.i("Main###", "Received data from ARUIN");
                    parseXML(data);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // parsing the XML with pull method
    private void parseXML(String xmlData) {
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();

            String name ="";
            String title = "";
            String abstracts = "";
            String organization = "";
            String geoName = "";
            BBOX bbox = new BBOX();
            String keywordsStr = "";
            String corners = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                // nodeName is the current XML tag
                String nodeName = xmlPullParser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if ("Name".equals(nodeName)) {
                            // get the content after nodeName
                            name = safeNextText(xmlPullParser);
//                            name = xmlPullParser.nextText();
                            Log.i("Name", name);
                        }
                        else if ("Title".equals(nodeName)){
                            title = safeNextText(xmlPullParser);
//                            Log.i("Main###title", title);
                            String[] tempArray = title.split(" Data provider: ");
                            title = tempArray[0];
                            Log.i("Title", title);
                            organization = tempArray[1];
                            Log.i("Organization", organization);
                        }
                        else if ("Abstract".equals(nodeName)){
                            String abstracts1 = safeNextText(xmlPullParser);
                            String [] array1 = abstracts1.split(" Temporal extent start: ");
                            abstracts = array1[0];
                            if (abstracts1.contains("wkb_geometry")){
                                geoName = "wkb_geometry";
                            }
                            else if (abstracts1.contains("the_geom")){
                                geoName = "the_geom";
                            }
                            else if (abstracts1.contains("ogr_geometry")){
                                geoName = "ogr_geometry";
                            }
                            else if (abstracts1.contains("SHAPE")){
                                geoName = "SHAPE";
                            }
                            break;

                        }
                        else if ("ows:Keyword".equals(nodeName)){
                            String keyword = safeNextText(xmlPullParser);
                            keywordsStr += ", ";
                            keywordsStr += keyword;
                        }
                        else if ("ows:LowerCorner".equals(nodeName)){
                            String temp1 = safeNextText(xmlPullParser);
                            corners += temp1;
                            corners += " ";

                            String[] lowerCorner = temp1.split(" ");
                            bbox.setLowerLon(Double.parseDouble(lowerCorner[0]));
                            bbox.setLowerLa(Double.parseDouble(lowerCorner[1]));
                        }
                        else if ("ows:UpperCorner".equals(nodeName)){
                            String temp2 = safeNextText(xmlPullParser);
                            corners += temp2;

                            String[] upperCorner = temp2.split(" ");
                            bbox.setHigherLon(Double.parseDouble(upperCorner[0]));
                            bbox.setHigherLa(Double.parseDouble(upperCorner[1]));
                        }
                        break;
                    }

                    case XmlPullParser.END_TAG: {
                        // If END_TAG is met, then create a new Capability object to store all data get above
                        if ("FeatureType".equals(nodeName)) {
                            Capability cap = new Capability();
                            cap.capName = name;
                            cap.capTitle = title;
                            cap.capOrganization = organization;
                            cap.capAbstracts = abstracts;
                            Log.i("Abstract: ", abstracts);
                            keywordsStr = keywordsStr.substring(2);
                            Log.i("keywords", keywordsStr);
                            cap.capKeywords = keywordsStr;
                            keywordsStr = "";
                            cap.capGeoName = geoName;
                            Log.i("GeoName", geoName);
                            cap.capCorners = corners;
//                            Log.i("Corners", corners);
                            corners = "";

                            cap.capBbox.setHigherLa(bbox.getHigherLa());
                            cap.capBbox.setHigherLon(bbox.getHigherLon());
                            cap.capBbox.setLowerLa(bbox.getLowerLa());
                            cap.capBbox.setLowerLon(bbox.getLowerLon());
                            // set the organization logo for each capability
                            switch (organization){
                                case "Government of New South Wales - Department of Planning and Environment":
                                    cap.image_id=R.drawable.logo_government_of_new_south_wales_department_of_plaaning_and_envirnoment;
                                    break;
                                case "Internode Pty. Ltd.":
                                    cap.image_id=R.drawable.logo_internode_pty_ltd;
                                    break;
                                case "Government of South Australia - RenewalSA":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_renewalsa;
                                    break;
                                case "Government of the Commonwealth of Australia - Geoscience Australia":
                                    cap.image_id=R.drawable.logo_government_of_the_commonwealth_of_australia_geoscience_australia;
                                    break;
                                case "Local Government of Queensland - Brisbane City Council":
                                    cap.image_id=R.drawable.logo_local_government_of_queensland_brisbane_city_council;
                                    break;
                                case "Local Government of South Australia - City of Salisbury":
                                    cap.image_id=R.drawable.logo_local_government_of_south_australia_city_of_salisbury;
                                    break;
                                case "Government of South Australia - Department of Planning, Transport and Infrastructure":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_department_of_planning_transport_and_infrastructure;
                                    break;
                                case "Government of South Australia - Department for Communities and Social Inclusion":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_department_for_communities_and_social_inclusion;
                                    break;
                                case "Government of South Australia - Department of Environment, Water and Natural Resources":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_department_of_environment_water_and_aatural_resources;
                                    break;
                                case "Government of Queensland - Department of Transport and Main Roads - Road Statistics":
                                    cap.image_id=R.drawable.logo_government_of_queensland_department_of_transport_and_main_roads_road_statistics;
                                    break;
                                case "Australian Government - Department of Social Services":
                                    cap.image_id=R.drawable.logo_australian_government_department_of_social_services;
                                    break;
                                case "Government of South Australia - Local Government Association of South Australia":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_local_government_association_of_south_australia;
                                    break;
                                case "Government of the Commonwealth of Australia - Australian Bureau of Statistics":
                                    cap.image_id=R.drawable.logo_government_of_the_commonwealth_of_australia_australian_bureau_of_statistics;
                                    break;
                                case "Government of New South Wales - Department of Education":
                                    cap.image_id=R.drawable.logo_government_of_new_south_wales_department_of_education;
                                    break;
                                case "Government of Queensland - Department of Education and Training":
                                    cap.image_id=R.drawable.logo_government_of_queensland_department_of_education_and_training;
                                    break;
                                case "Government of Queensland - Department of Natural Resources and Mines":
                                    cap.image_id=R.drawable.logo_government_of_queensland_department_of_natural_resources_and_mines;
                                    break;
                                case "Government of South Australia - Attorney-General's Department":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_attorney_generals_department;
                                    break;
                                case "Government of South Australia - Department for Education and Child Development":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_department_for_education_and_child_development;
                                    break;
                                case "Government of South Australia - Department for State Development":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_department_for_state_development;
                                    break;
                                case "Government of South Australia - Department of Environment Water and Natural Resources":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_department_of_environment_water_and_natural_resources;
                                    break;
                                case "Government of South Australia - SA Health":
                                    cap.image_id=R.drawable.logo_government_of_south_australia_sa_health;
                                    break;
                                case "Government of Tasmania - Department Of Primary Industries, Parks, Water And Environment":
                                    cap.image_id=R.drawable.logo_government_of_tasmania_department_of_primary_industries_parks_water_and_environment;
                                    break;
                                case "Government of the Australian Capital Territory - Department of Education and Training":
                                    cap.image_id=R.drawable.logo_government_of_the_australian_capital_territory_department_of_education_and_training;
                                    break;
                                case "Government of the Australian Capital Territory - Environment, Planning and Sustainable Development Directorate":
                                    cap.image_id=R.drawable.logo_government_of_the_australian_capital_territory_environment_planning_long;
                                    break;
                                case "Government of the Commonwealth of Australia - Department of Human Services":
                                    cap.image_id=R.drawable.logo_government_of_the_commonwealth_of_australia_department_of_human_services;
                                    break;
                                case "Government of Victoria - Crime Statistics Agency":
                                    cap.image_id=R.drawable.logo_government_of_victoria_crime_statistics_agency;
                                    break;
                                case "Government of Victoria - Department of Education and Training":
                                    cap.image_id=R.drawable.logo_government_of_victoria_department_of_education_and_training;
                                    break;
                                case "Government of Victoria - Department of Environment, Land, Water and Planning":
                                    cap.image_id=R.drawable.logo_government_of_victoria_department_of_environment_land_water_and_planning;
                                    break;
                                case "Government of Victoria - Department of Health and Human Services":
                                    cap.image_id=R.drawable.logo_government_of_victoria_department_of_health_and_human_services;
                                    break;
                                case "Government of Victoria - Victorian Commission for Gambling and Liquor Regulation":
                                    cap.image_id=R.drawable.logo_government_of_victoria_victorian_commission_for_gambling_and_liquor_regulation;
                                    break;
                                case "NSW Bureau of Crime Statistics and Research":
                                    cap.image_id=R.drawable.logo_nsw_bureau_of_crime_statistics_and_research;
                                    break;
                                case "Victoria State Government - Department of Treasury and Finance":
                                    cap.image_id=R.drawable.logo_victoria_state_government_department_of_treasury_and_finance;
                                    break;
                                case "Melbourne Water Corporation":
                                    cap.image_id=R.drawable.logo_melbourne_water_corporation;
                                    break;
                                default:
                                    cap.image_id=R.drawable.logo_defalut_org_image;

                            }
                            if(! BigData.big_data.contains(cap.capTitle)) {
                                AllDataSets.capList.add(cap);
                                capCount += 1;
                                Log.i(TAG + "Total cap num: ", String.valueOf(capCount));
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

            Log.i(TAG, "Parsing finished!: " + String.valueOf(capCount));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFragmentInteraction(String data) {

    }

    private String safeNextText(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        String result = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.nextTag();
        }
        return result;
    }

    // This class works for receiving data from AURIN and showing pop-up window
    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Receiving data from AURIN");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            // Open the text file containing data from AURIN to reduce the usage of this API
            AssetManager assetManager = getApplicationContext().getAssets();
            try{
                InputStream in = assetManager.open("raw_data2.xml");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String data = stringBuilder.toString();
                Log.i(TAG, "All data read from txt file");
                parseXML(data);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            // Set ListFragment as default fragment shown in MainActivity
            homeFragment = new ListFragment();
            mainFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .commit();
        }
    }

    private Fragment getCurrentFragment() {
        Fragment currentFragment = mainFragmentManager.findFragmentById(R.id.fragment_container);
        return currentFragment;
    }
}

