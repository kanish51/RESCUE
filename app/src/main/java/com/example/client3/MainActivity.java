package com.example.client3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    public Button loc_button;
    public TextView latlon;
    public TextView labelstat;

    public TextView label1;
    public Button sendButton;
    public double lat;
    public double lon;
    public WebView webview1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview1= (WebView) findViewById(R.id.webview1);
        label1 = findViewById(R.id.label1);
        labelstat = findViewById(R.id.labelstat);

        // make send button object+
        sendButton = (Button) findViewById(R.id.sendButton);

        latlon = findViewById(R.id.latlon);
        loc_button = (Button) findViewById(R.id.loc_button);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GPStracker gt = new GPStracker(getApplicationContext());
                Location l = gt.getLocation();
                if( l == null){
                    Toast.makeText(getApplicationContext(),"GPS unable to get value",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),"GPS unable to get Value",Toast.LENGTH_SHORT).show();
                }else {
                    lat = l.getLatitude();
                    lon = l.getLongitude();
                    latlon.setText(lat+" , "+lon);
                    Toast.makeText(getApplicationContext(),"Lat = "+lat+"\n Lon = "+lon,Toast.LENGTH_SHORT).show();
                }

            }
        });
        /*String networkSSID = "test";
        String networkPass = "pass";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.addNetwork(conf);*/
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", "Rescue");
        wifiConfig.preSharedKey = String.format("\"%s\"", "abcdefgh");

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        WifiInfo wifiInfo;
        wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();

        if(ssid=="Rescue")
        {
            loc_button.performClick();
            sendButton.performClick();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String device_id = tm.getDeviceId();
                label1.setText("IMEI NO. "+device_id);
                //webview1.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                //webview1.getSettings().setBlockNetworkLoads(false);
                //webview1.getSettings().setJavaScriptEnabled(true);
                //webview1.loadUrl("https://192.168.137.1/mysite.html");
                //webview1.loadUrl("");
                RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://192.168.137.1/add.php?imei="+device_id+"&lat="+lat+"&longitude="+lon;
                StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //This code is executed if the server responds, whether or not the response contains data.
                        //The String 'response' contains the server's response.
                        //You can test it by printing response.substring(0,500) to the screen.
                    }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                    }
                });
                ExampleRequestQueue.add(ExampleStringRequest);


                labelstat.setText("We have received your location! SENDING HELP!");

            }
        });
    }


}
