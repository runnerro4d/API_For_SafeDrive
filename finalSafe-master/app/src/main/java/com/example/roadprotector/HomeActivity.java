package com.example.roadprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.roadprotector.provider.RestStop;
import com.example.roadprotector.provider.RestStopViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity {

    static final String FIRST_INSTALL_NAVIGATION = "firstInstallNavigation";
    static final String FIRST_INSTALL_EXPLORE = "firstInstallExplore";
    static final String FIRST_INSTALL_DATABASE = "firstInstallDatabase";
    private RestStopViewModel restStopViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        restStopViewModel = new ViewModelProvider(this).get(RestStopViewModel.class);

//        Button navigateBtn = findViewById(R.id.navigateId);
//        navigateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (HomeActivity.this, NavigationActivity.class);
////                intent.putExtra("Activity","NavigationActivity");
//                startActivity(intent);
//            }
//        });




        Button navigateBtn = findViewById(R.id.navigateId);
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getPreferences(0).getBoolean(FIRST_INSTALL_NAVIGATION, true)) {
                    getPreferences(0).edit().putBoolean(FIRST_INSTALL_NAVIGATION,false).commit();
                    Intent intent = new Intent (HomeActivity.this, OnboardingActivity.class);
                    intent.putExtra("Activity","NavigationActivity");
                    startActivity(intent);


                }
               else
                {
                    Intent intent = new Intent (HomeActivity.this, NavigationActivity.class);
                    startActivity(intent);
                }

            }
        });

//        Button exploreBtn = findViewById(R.id.exploreId);
//        exploreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (HomeActivity.this, MapsActivity.class);
//                startActivity(intent);
//            }
//        });


        Button exploreBtn = findViewById(R.id.exploreId);
        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getPreferences(0).getBoolean(FIRST_INSTALL_EXPLORE, true)) {
                    getPreferences(0).edit().putBoolean(FIRST_INSTALL_EXPLORE, false).commit();
                    Intent intent = new Intent(HomeActivity.this, OnboardingActivity.class);
                    intent.putExtra("Activity", "ExploreActivity");
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent (HomeActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });

//        Button restStopBtn = findViewById(R.id.restprefId);
//        restStopBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (HomeActivity.this, RestStopPrefActivity.class);
//                startActivity(intent);
//            }
//        });

//        Button statistics = findViewById(R.id.statsid);
//        statistics.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (HomeActivity.this, RestStopActivity.class);
//                startActivity(intent);
//            }
//        });

        Button OnboardBtn = findViewById(R.id.statsid);
        OnboardBtn.setOnClickListener((v) -> {
                Intent intent = new Intent (HomeActivity.this, OnboardingActivity.class);
                startActivity(intent);

        });

        if(getPreferences(0).getBoolean(FIRST_INSTALL_DATABASE, true)){
            GetRestStops getData = new GetRestStops();
            getData.execute();
        }

    }


    // Asynchronously download rest stop data
    class GetRestStops extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... arg) {
            JSONObject restStopData = getRoutesData();
            return restStopData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.i("onPostExecReststop", jsonObject.toString());

//            Toast.makeText(HomeActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();

            getPreferences(0).edit().putBoolean(FIRST_INSTALL_DATABASE, false).commit();
            JSONArray dataArray = null;
            try {
                dataArray = jsonObject.getJSONArray("data");

                //int reststopNumber, double lat, double lng, int disabledToilet, int fastfood, int picnictable, int toilet, int water
//                "DISABLEDTOILETS": 1,
//                        "FASTFOOD": 1,
//                        "LATITUDE": -37.869202,
//                        "LONGITUDE": 144.762177,
//                        "PICNICTABLES": 0,
//                        "RESTAREAID": 1000001,
//                        "ROADNAME": "PRINCES HIGHWAY WEST",
//                        "SUBURB": "HOBSONS BAY",
//                        "TOILETS": 1,
//                        "WATER": 1
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject reststopOBJ =  dataArray.getJSONObject(i);

                    RestStop restStop = new RestStop(reststopOBJ.getInt("RESTAREAID"), reststopOBJ.getDouble("LATITUDE"),
                            reststopOBJ.getDouble("LONGITUDE"), reststopOBJ.getInt("DISABLEDTOILETS"), reststopOBJ.getInt("FASTFOOD"),
                            reststopOBJ.getInt("PICNICTABLES"), reststopOBJ.getInt("TOILETS"), reststopOBJ.getInt("WATER"));
                    restStopViewModel.insert(restStop);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    // API connection to retrieve rest stop data from webservice
    public JSONObject getRoutesData() {
        final String BASE_URL = "http://accident-api.eba-rjmccapm.ap-southeast-2.elasticbeanstalk.com/rest";
        String getURL = BASE_URL;

        JSONObject restStopJSON = null;
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        String second="";
//Making HTTP request

        try {
            url = new URL(getURL);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000000);
            conn.setConnectTimeout(15000000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.connect();
//Read the response
            int i = conn.getResponseCode();
            Scanner inStream;
            if (i==200 || i==204)
                inStream = new Scanner(conn.getInputStream());
            else
                inStream = new Scanner(conn.getErrorStream());
//read the input stream and store it as string
            while(inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            JSONObject jsonObject = new JSONObject(textResult);
            restStopJSON = jsonObject;
            Log.i("reststopdata", restStopJSON.toString() );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        System.out.println(second);

        return restStopJSON;

    }
}
