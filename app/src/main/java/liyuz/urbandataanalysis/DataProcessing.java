package liyuz.urbandataanalysis;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Created by Liyu Zhang on 10/1/18.
 * This class contains methods for sending request to AURIN and parse the received data
 */

public class DataProcessing {
    private static final String TAG = "DataProcessing###: ";
    private static String data;

    // This method send url request to AURIN
    static String sendRequest(final String urlStr) {
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
                    URL url = new URL(urlStr);
                    Log.i(TAG, "Sent request to AURIN");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(12000); // Default value is 8000
                    connection.setReadTimeout(12000);

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // Store all data in the string
                    data = response.toString();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return data;
    }

    static void parseXMLMainActivity(String xmlData) {

    }
}
