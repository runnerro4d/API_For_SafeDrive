package com.example.roadprotector;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.maps.android.PolyUtil.decode;

public class MultiRouteparser {

    public ArrayList<Route> parser(JSONObject jObject) {

        ArrayList<Route> allRoutes = new ArrayList<>();

        JSONArray jsonRoutesArray = null;

        String overViewPolyLine = "";

        try {

            jsonRoutesArray = jObject.getJSONArray("routes");
            //Going through routes array to get overviewplylines for all the routes
            for(int routeNo = 0; routeNo < jsonRoutesArray.length(); routeNo ++ )
            {
                JSONObject jsonrouteObject = jsonRoutesArray.getJSONObject(routeNo);

                overViewPolyLine = jsonrouteObject.getString("polyline");

                List decodedOverviewPolyline = decode(overViewPolyLine);

                Route route = new Route( jsonrouteObject.getInt("RouteNo"), jsonrouteObject.getJSONArray("data"), decodedOverviewPolyline,
                        jsonrouteObject.getInt("routeDurationInSeconds"), jsonrouteObject.getInt("routeLengthInMeters"),
                        jsonrouteObject.getInt("totalAccidents")
                );

                allRoutes.add(route);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return allRoutes;
    }
}

