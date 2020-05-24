package com.example.roadprotector;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.maps.android.PolyUtil.decode;

public class MultipleDirectionParser {

    public ArrayList<List<HashMap<String,String>>> parser(JSONObject jObject) {



        ArrayList<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;





       ArrayList<HashMap<String,String>> route = new ArrayList<HashMap<String,String>>();
        JSONArray jsonRoutesArray = null;
        String overViewPolyLine = "";

        try {

            jsonRoutesArray = jObject.getJSONArray("routes");
            //Going through routes array to get overviewplylines for all the routes
            for(int routeNo = 0; routeNo < jsonRoutesArray.length(); routeNo ++ )
            {
                overViewPolyLine = (String)( jsonRoutesArray.getJSONObject(routeNo).getJSONObject("overview_polyline").get("points") );
                List decodedOverviewPolyline = decode(overViewPolyLine);

                for(int i = 0; i < decodedOverviewPolyline.size(); i++){


                    HashMap<String, String> locationPoint = new HashMap<String, String>();
                    locationPoint.put("lat",  Double.toString(((LatLng)decodedOverviewPolyline.get(i)).latitude) );
                    locationPoint.put("long", Double.toString(((LatLng)decodedOverviewPolyline.get(i)).longitude) );
                    route.add(locationPoint);
                }

                routes.add(route);
            }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
    }
}
