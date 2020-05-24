package com.example.roadprotector;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.maps.android.PolyUtil.decode;

public class DirectionParser {

    public List<HashMap<String,String>> parser(JSONObject jObject) {



        List<HashMap<String,String>> route = new ArrayList<HashMap<String,String>>();
          JSONArray jsonRoutes = null;
          String overViewPolyLine = "";

        try {

            jsonRoutes = jObject.getJSONArray("routes");
            overViewPolyLine = (String)( jsonRoutes.getJSONObject(0).getJSONObject("overview_polyline").get("points") );

            List decodedOverviewPolyline = decode(overViewPolyLine);

            for(int i = 0; i < decodedOverviewPolyline.size(); i++){

                HashMap<String, String> locationPoint = new HashMap<String, String>();
                locationPoint.put("lat",  Double.toString(((LatLng)decodedOverviewPolyline.get(i)).latitude) );
                locationPoint.put("long", Double.toString(((LatLng)decodedOverviewPolyline.get(i)).longitude) );
                route.add(locationPoint);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return route;
    }
}
