package com.example.roadprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.roadprotector.provider.RestStop;
import com.example.roadprotector.provider.RestStopViewModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RestStopPrefActivity extends AppCompatActivity {
    static  final String FILE_NAME = "myRestStopPref";
    static final String FIRST_INSTALL = "firstInstall";
    static final String DISABLED_TOILET = "disabledToilet";
    static final String FAST_FOOD = "fastFood";
    static final String PICNIC_TABLE = "picnicTable";
    static final String TOILET = "toilet";
    static final String WATER = "water";
    private Switch disabledToiletSwitch;
    private Switch fastFoodSwitch;
    private Switch picnicTableSwitch;
    private Switch toiletSwitch;
    private Switch waterSwitch;
    private Button save;
    private Button clearAll;
    private RestStopViewModel restStopViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_stop_pref);

        //disableledToiletFlag
        //fastfoodFlag
        //picnictableFlag
        //toiletFlag
        //waterFlag

        // UI components for the screen
        disabledToiletSwitch = findViewById(R.id.disabledtoiletid);
        fastFoodSwitch = findViewById(R.id.fastfoodid);
        picnicTableSwitch = findViewById(R.id.picnictableid);
        toiletSwitch = findViewById(R.id.toiletid);
        waterSwitch = findViewById(R.id.waterid);
        save = findViewById(R.id.saveid);
        clearAll = findViewById(R.id.clearid);

        restStopViewModel = new ViewModelProvider(this).get(RestStopViewModel.class);



        //edit.putBoolean(FIRST_INSTALL, )



        // save rest stop preferences
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences restStopPref = getSharedPreferences(FILE_NAME,0);
                SharedPreferences.Editor edit =  restStopPref.edit();

                if(disabledToiletSwitch.isChecked())
                    edit.putInt(DISABLED_TOILET, 1 );
                else
                    edit.putInt(DISABLED_TOILET, 0);

                if(fastFoodSwitch.isChecked())
                    edit.putInt(FAST_FOOD, 1 );
                else
                    edit.putInt(FAST_FOOD, 0);

                if(picnicTableSwitch.isChecked())
                    edit.putInt(PICNIC_TABLE, 1 );
                else
                    edit.putInt(PICNIC_TABLE, 0);

                if(toiletSwitch.isChecked())
                    edit.putInt(TOILET, 1 );
                else
                    edit.putInt(TOILET, 0);

                if(waterSwitch.isChecked())
                    edit.putInt(WATER, 1 );
                else
                    edit.putInt(WATER, 0);

                edit.apply();

                String toast = "DISABLED_TOILET: " + restStopPref.getInt(DISABLED_TOILET, -999) + "\n"
                        + "FAST_FOOD: " + restStopPref.getInt(FAST_FOOD, -999) + "\n"
                        + "PICNIC_TABLE: " + restStopPref.getInt(PICNIC_TABLE, -999) + "\n"
                        + "TOILET: " + restStopPref.getInt(TOILET, -999) + "\n"
                        + "WATER: " + restStopPref.getInt(WATER, -999) + "\n";
                Toast.makeText(RestStopPrefActivity.this, toast, Toast.LENGTH_LONG).show();
            }
        });

        // clear all rest stop preferences
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences restStopPref = getSharedPreferences(FILE_NAME,0);
                SharedPreferences.Editor edit =  restStopPref.edit();

                edit.putInt(DISABLED_TOILET, 0);
                edit.putInt(FAST_FOOD, 0);
                edit.putInt(PICNIC_TABLE, 0);
                edit.putInt(TOILET, 0);
                edit.putInt(WATER, 0);
                edit.apply();

                disabledToiletSwitch.setChecked(false);
                fastFoodSwitch.setChecked(false);
                picnicTableSwitch.setChecked(false);
                toiletSwitch.setChecked(false);
                waterSwitch.setChecked(false);

                String toast = "DISABLED_TOILET: " + restStopPref.getInt(DISABLED_TOILET, -999) + "\n"
                        + "FAST_FOOD: " + restStopPref.getInt(FAST_FOOD, -999) + "\n"
                        + "PICNIC_TABLE: " + restStopPref.getInt(PICNIC_TABLE, -999) + "\n"
                        + "TOILET: " + restStopPref.getInt(TOILET, -999) + "\n"
                        + "WATER: " + restStopPref.getInt(WATER, -999) + "\n";
                Toast.makeText(RestStopPrefActivity.this, toast, Toast.LENGTH_LONG).show();

            }
        });

        // Only download rest stop data when app is installed for first time
        if(getPreferences(0).getBoolean(FIRST_INSTALL, true)) {
            GetRestStops getData = new GetRestStops();
            getData.execute();



        }


//        GetRestStops getData = new GetRestStops();
//        getData.execute();
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

            Toast.makeText(RestStopPrefActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();

             getPreferences(0).edit().putBoolean(FIRST_INSTALL,false).commit();
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
