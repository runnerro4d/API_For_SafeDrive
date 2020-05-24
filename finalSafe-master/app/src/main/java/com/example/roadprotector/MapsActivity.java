package com.example.roadprotector;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import static com.google.maps.android.PolyUtil.decode;
import static com.google.maps.android.PolyUtil.isLocationOnPath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    Location currentLocation;
    private LatLng startLocation;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    AlertDialog.Builder builder = null;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    //private ArrayList<String> routeCoordinates = null;
    //private ArrayList<String> allRoutePolyLines = null;
    //private ArrayList<ArrayList<String>> allRouteCoordinates  = null;
    //private ArrayList<List<LatLng>>  allRouteLocationCoordinates = null;
    // Instantiated in onMapReady
    private ArrayList<JSONArray> allRouteAccidentData ;
    private boolean downloadDataFlag = false;
    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();
    ArrayList<Polyline> allRoutesPolylines ;
    int routeAccidentDataCount = 0;
    private TextView accidentStatusTV;
    private List<LatLng> selectedPolyline;
    private TextView routeDetailTV;
    private TextView RouteNumber;
    private TextView Risk;
    private TextView Duration;
    private TextView Distance;

    private Button navigate;
    private LatLng destination ;
    private String destinationPlace;
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geofenceHelper;
    private int safestRoute = 0;
    private boolean geofenceSet = false;
    private String accidentDatatTest;
    private ArrayList<String> allRouteSafetyRating;
    private ArrayList<Double> allRouteAccidentRate;
    private ArrayList<Route> allRouteDetails;
    private ArrayList<Circle> routeAllCircles = new ArrayList<>();
    public static int routeDuration;


    //View view = getLayoutInflater().inflate(R.layout.progress);
    Dialog dialogGetRoutes = null;
    Dialog dialogGetSafeRoute = null;

   /** private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
       // final MediaPlayer mpEntering = MediaPlayer.create(MapsActivity.this, R.raw.Entering);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
       // mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
       // makeRequest("");
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeoFenceHelper(this);



        String apiKey = "AIzaSyCYfveclaFp0QlPtfon7Q7pJg35OBAkqrU";

        // Setup Places Client

        Places.initialize(getApplicationContext(), apiKey);

        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder()
                .include(new LatLng(-34.12,141.98))
                .include(new LatLng(-39.03,146.32))
                .include(new LatLng(-36.37,140.98))
                .include(new LatLng(-37.55,149.75));

        RectangularBounds rectangularBounds = RectangularBounds.newInstance(boundsBuilder.build());
        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        PlacesClient placesClient = Places.createClient(this);
//(AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment originFrag = (AutocompleteSupportFragment)   getSupportFragmentManager().findFragmentById(R.id.startLoc);
        //     AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
        //                 getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        originFrag.setLocationRestriction(rectangularBounds);
        originFrag.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        originFrag.setCountry("AU");


// Set up a PlaceSelectionListener to handle the response.
        originFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                startLocation = place.getLatLng();
                destinationPlace = place.getName();

                if(mMap != null)
                {
                    mMap.clear();
                }
                removeGeofence();
                if (selectedPolyline != null)
                    selectedPolyline.clear();

                if (destination != null){
                    startRoute(startLocation, destination, true);
                    Log.i("placess", "Place: " + place.getName() + ", " + place.getId());
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("places", "An error occurred: " + status);
            }
        });

        AutocompleteSupportFragment destFrag = (AutocompleteSupportFragment)   getSupportFragmentManager().findFragmentById(R.id.endLoc);
        destFrag.setLocationRestriction(rectangularBounds);
        destFrag.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        destFrag.setCountry("AU");

        destFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getLatLng();
                destinationPlace = place.getName();

                if(mMap != null)
                {
                    mMap.clear();
                }
                removeGeofence();
                if (selectedPolyline != null)
                    selectedPolyline.clear();

             if (startLocation != null){
                 startRoute(startLocation, destination, true);
                 Log.i("placess", "Place: " + place.getName() + ", " + place.getId());
             }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("places", "An error occurred: " + status);
            }
        });



    }

    public void startRoute(LatLng origin, LatLng destination, boolean placesApi ){

        if(placesApi) {
            mMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_flag)));
        }

        //initialising the array of Accident API responses
        allRouteAccidentData = new ArrayList<JSONArray>();
        routeAccidentDataCount = 0;

        //["-37.349852, 144.548298","-37.417703, 144.995094"]
        String urlParams = "[\"" + origin.latitude + ", " + origin.longitude + "\",\"" + destination.latitude + ", " + destination.longitude + "\"]";

        DownloadAccidentData download = new DownloadAccidentData();
        download.execute(urlParams);

//         LatLngBounds route = new LatLngBounds(
//                origin, destination);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(route, 1));

    }




    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                   // Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }


        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {



        //download.execute(accidentUrl);
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);


        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
        //googleMap.addMarker(markerOptions);



       // LatLng sydney = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));


        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//               // removeGeofence();
//
//
//                if (markerPoints.size() > 1) {
//                    markerPoints.clear();
//                    mMap.clear();
//                }
//
//                // Adding new item to the ArrayList
//                markerPoints.add(latLng);
//
//                // Creating MarkerOptions
//                MarkerOptions options = new MarkerOptions();
//
//                // Setting the position of the marker
//                options.position(latLng).title(latLng.toString());
//
//                if (markerPoints.size() == 1) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//                } else if (markerPoints.size() == 2) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                }
//
//                // Add new marker to the Google Map Android API V2
//                mMap.addMarker(options);
//
//                // Checks, whether start and end locations are captured
//                if (markerPoints.size() >= 2) {
//                    LatLng origin = (LatLng) markerPoints.get(0);
//                    LatLng dest = (LatLng) markerPoints.get(1);
//                    destination = dest;
//
//                    //initialising the array of Accident API responses
//                    allRouteAccidentData = new ArrayList<JSONArray>();
//                    routeAccidentDataCount = 0;
//                    accidentStatusTV = findViewById(R.id.RouteDuration_explore);
//                    accidentStatusTV.setText("");
//
//
//                    //["-37.349852, 144.548298","-37.417703, 144.995094"]
//                    String urlParams = "[\"" + origin.latitude + ", " + origin.longitude + "\",\"" + dest.latitude + ", " + dest.longitude + "\"]";
//
//                    DownloadAccidentData download = new DownloadAccidentData();
//                    download.execute(urlParams);
//                    // Getting URL to the Google Directions API
//                   // String url = getDirectionsUrl(origin, dest);
//// //http://accident-api.eba-rjmccapm.ap-southeast-2.elasticbeanstalk.com/AIzaSyBZqLIP9yoJtQUwL-0vhgl0DBL_PVcQq6s/["-37.349852, 144.548298","-37.417703, 144.995094"]
//                    //DownloadTask downloadTask = new DownloadTask();
//
//                    // Start downloading json data from Google Directions API
//                    //downloadTask.execute(url);
//
//
//                }
//
//            }
//        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
//                Color polyColor = Color.valueOf(polyline.getColor()) ;
//                polyline.setColor(Color.BLUE);



                int routeNo =  Integer.parseInt(polyline.getTag() .toString());
                try {
                    geofenceHandler(routeNo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(MapsActivity.this, tag + " : " + polyColor.toString() , Toast.LENGTH_SHORT).show();

                try {
                    displayRouteSafety(routeNo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MapsActivity.this, destination.toString(), Toast.LENGTH_SHORT).show();

//                polyline.setColor( polyColor.hashCode());






            }
        });


        navigate = findViewById(R.id.navigateId);

//        navigate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//
//            {
//
////                try {
////                    geofenceHandler(safestRoute);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.toString(destination.latitude) + "," + Double.toString(destination.longitude));
//                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                            mapIntent.setPackage("com.google.android.apps.maps");
//                            startActivity(mapIntent);
//            }
//        });

    }


//    //accident jsonObject will be added into allRouteAccidentData Array List
//    protected void getAccidentForRoutes() throws JSONException {
//
//        for (int routeNo = 0; routeNo < allRouteCoordinates.size(); routeNo++ ){
//            ArrayList<String> route = allRouteCoordinates.get(routeNo);
//            DownloadAccidentData download = new DownloadAccidentData();
//            // Start downloading if the flag is false otherwise wait
////            while(downloadDataFlag)
////            {
////
////            }
//            download.execute(route.toString(), routeNo);
//        }
//
//    }




//    protected void printRoutesSafety() throws JSONException {
//
//        ArrayList<Integer> totalAccidentAllRoute = new ArrayList<>();
//        int routeTotalAccidentCount = 0;
//        String status = "";
//        accidentStatusTV = findViewById(R.id.accidentStatusid);
//
//        for(int routeNo =0; routeNo < allRouteAccidentData.size() ; routeNo++ )
//        {
//            routeTotalAccidentCount = Integer.parseInt((String) allRouteAccidentData.get(routeNo).get("totalAccidents")) ;
//            totalAccidentAllRoute.add(routeTotalAccidentCount);
//            status += "Route No : " + (routeNo +1) + "  Accidents: " + routeTotalAccidentCount;
//
//        }
//
//        accidentStatusTV.setText(status);
//        Collections.max(totalAccidentAllRoute);
//        Collections.min(totalAccidentAllRoute);
//
//        int safest = allRouteAccidentData.indexOf(Collections.min(totalAccidentAllRoute));
//        int mostAccidentProne = allRouteAccidentData.indexOf(Collections.max(totalAccidentAllRoute));
//
//         Polyline p = allRoutesPolylines.get(safest) ;
//         p.setColor(Color.GREEN);
//
//         allRoutesPolylines.get(mostAccidentProne).setColor(Color.RED);
//
//
//        Log.i("AccidentStatus", status );
//
//
//    }

    public void displayRouteSafety(int routeNo) throws JSONException {

        Log.i("allRouteDetails", allRouteDetails.toString());
        //Toast.makeText(this, allRouteDetails.toString(), Toast.LENGTH_LONG).show();

        int routeDurationInSeconds = allRouteDetails.get(routeNo).getRouteDurationInSeconds();
        int routeLengthInMeters  = allRouteDetails.get(routeNo).getRouteLengthInMeters();

        // Convert seconds to Hrs:min:ss
//        int secs = routeDurationInSeconds % 60;
        int d2 = routeDurationInSeconds / 60;
        int mins = d2 % 60;
        int hrs = d2 / 60;

        String duration = String.format("%02d hrs %02d mins", hrs, mins);

        //Convert Distance
//        int meters = routeLengthInMeters % 1000;
        int kms = routeLengthInMeters / 1000;

        String distance = String.format("%02d kms", kms);


        String routeDetail = "Route No: " + (routeNo + 1 ) + "\n" + "Safety Rating: " + allRouteSafetyRating.get(routeNo) +
                "\n" + "Duration: " +  duration +
                "\n" + "Distance: " + distance;
        routeDetailTV = findViewById(R.id.routeDetail_exploreId);
        RouteNumber = findViewById(R.id.routeNumber_exploreId);
        Risk = findViewById(R.id.RouteRisk_explore);
        Duration = findViewById(R.id.RouteDuration_explore);
        Distance = findViewById(R.id.RouteDistance_explore);

        routeDetailTV.setText("Route Details ");
        RouteNumber.setText("Route No: " + routeNo);
        Risk.setText("Risk: " + allRouteSafetyRating.get(routeNo));
        Duration.setText("Duration: " + duration);
        Distance.setText("Distance: " + distance);



    }

    private void geofenceHandler(int routeNo) throws JSONException {
        //Removing existing geofence
        removeGeofence();
        // Removing circles for previously tapped route
        for(Circle circle:  routeAllCircles )
        {
            circle.remove();
        }

        routeAllCircles.clear();
        String routeStr = String.valueOf(routeNo);
       // JSONObject routeObject = allRouteAccidentData.get(routeNo);
        JSONArray  routeArray = allRouteAccidentData.get(routeNo);
                //routeObject.getJSONArray(routeStr);
        double Latitude = 0;
        double Longitude = 0;
        double radiusInKM = 0.0;
        String geofenceId = "";
        List routePolyline = allRouteDetails.get(routeNo).getRouteLocationCoordinates();



        //geofence expiry
        int routeDuration = allRouteDetails.get(routeNo).getRouteDurationInSeconds();

        // add geofence if the center lies on the route or at least in 500 meters proximity to the route
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                for (int clusterNo = 0; clusterNo < routeArray.length(); clusterNo++ ){
                    Latitude = (Double) routeArray.getJSONObject(clusterNo).get("Latitude");
                    Longitude = (Double) routeArray.getJSONObject(clusterNo).get("Longitude");
                    radiusInKM = (Double) routeArray.getJSONObject(clusterNo).get("RadiusInKM");

                    geofenceId =  clusterNo+ "@" + routeStr;
                    LatLng center = new LatLng(Latitude, Longitude);

                    // add geofence if the center lies on the route or at least in 500 meters proximity to the route
                    if(isLocationOnPath(center,routePolyline, true, 500)) {
                        if (radiusInKM == 0) {
                            radiusInKM = 0.5;
                        }

                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(center);
                        circleOptions.radius(radiusInKM * 1000);
                        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
                        circleOptions.strokeWidth(3);
                        circleOptions.zIndex(7);
                        Circle circle = mMap.addCircle(circleOptions);
                        routeAllCircles.add(circle);

                        //addCircle(center, radiusInKM);
                        addGeofence(geofenceId, center, radiusInKM * 1000);
                    }
                }

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        }
        else{
            for (int clusterNo = 0; clusterNo < routeArray.length(); clusterNo++ ){
                Latitude = (Double) routeArray.getJSONObject(clusterNo).get("Latitude");
                Longitude = (Double) routeArray.getJSONObject(clusterNo).get("Longitude");
                radiusInKM = (Double) routeArray.getJSONObject(clusterNo).get("RadiusInKM");

                geofenceId =  clusterNo+ "@" + routeStr;
                LatLng center = new LatLng(Latitude, Longitude);
                // add geofence if the center lies on the route or at least in 500 meters proximity to the route
                if(isLocationOnPath(center,routePolyline, true, 1000)) {
                    if (radiusInKM == 0) {
                        radiusInKM = 0.5;
                    }

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(center);
                    circleOptions.radius(radiusInKM * 1000);
                    circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                    circleOptions.fillColor(Color.argb(64, 255, 0, 0));
                    circleOptions.strokeWidth(3);
                    circleOptions.zIndex(7);
                    Circle circle = mMap.addCircle(circleOptions);
                    routeAllCircles.add(circle);


                    //addCircle(center, radiusInKM);
                    addGeofence(geofenceId, center, radiusInKM * 1000);
                }
            }


        }

        Log.i("routeAllCircles",routeAllCircles.toString() );




    }

    private void addGeofence(String geofenceId, LatLng latLng, double radius) {

        Geofence geofence = geofenceHelper.getGeofence(geofenceId, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();


        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Geofence", "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d("geofence", "onFailure: " + errorMessage);
                    }
                });
    }

    private void removeGeofence() {

        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.removeGeofences(pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Geofence", "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d("geofence", "onFailure: " + errorMessage);
                    }
                });
    }

    private void addCircle(LatLng latLng, double radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(3);
        mMap.addCircle(circleOptions);
    }


/**
    private class DownloadTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Download routes Task", e.toString());
            }
            return data;
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // for one route
         //ParserTask parserTask = new ParserTask();
           // parserTask.execute(result);

         // for multiple routes
         RoutesGenerator routeGen = new RoutesGenerator();
         routeGen.execute(result);


        }
    }
*/

    // plottig one route
/**
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            // List<List<HashMap<String, String>>> routes = null;
            //overview poly
            List<HashMap<String,String>> route = null;


            try {
                jObject = new JSONObject(jsonData[0]);
                //  DirectionsJSONParser parser = new DirectionsJSONParser();

                DirectionParser parser = new DirectionParser();

                //routes = parser.parse(jObject);
                route = parser.parser(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return route;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            final ArrayList points = new ArrayList();

            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {

                lineOptions = new PolylineOptions();

                HashMap<String, String> path = result.get(i);



                    double lat = Double.parseDouble((String) path.get("lat"));
                    double lng = Double.parseDouble((String) path.get("long"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

            }

            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);
// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions).setClickable(true);

            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                @Override
                public void onPolylineClick(Polyline polyline) {
                    polyline.setColor(Color.BLUE);

                    LatLng origin = (LatLng) points.get(0 );
                    LatLng dest = (LatLng) points.get(points.size() -1 );


                    Toast.makeText(MapsActivity.this,  dest.toString(), Toast.LENGTH_SHORT).show();

                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + origin.latitude + origin.longitude + "," + dest.latitude  + "," +  dest.longitude );
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        }
    }



//plotting multiple routes
/**
    protected class RoutesGenerator extends AsyncTask<String, Integer,  ArrayList<List<LatLng>>> {

    // Parsing the data in non-ui thread

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setView(R.layout.progress_layout);
        dialogGetRoutes = builder.create();
        dialogGetRoutes.show();
    }

    @Override
    protected  ArrayList<List<LatLng>> doInBackground(String... jsonData) {

        JSONObject jObject;
         ArrayList<List<HashMap<String, String>>> routes = null;
        ArrayList<List<LatLng>> allRoutes = new ArrayList<List<LatLng>>();
        //overview poly
        List<HashMap<String,String>> route = null;


        try {
            jObject = new JSONObject(jsonData[0]);
            //MultipleDirectionParser parse = new MultipleDirectionParser();
            MultiRouteparser routParse = new MultiRouteparser();

            //routes = parser.parse(jObject);
            //multiple route direction parser
            //routes = parse.parser(jObject);
            allRoutes = routParse.parser(jObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return allRoutes;
    }

    @Override
    protected void onPostExecute(ArrayList<List<LatLng>>  allRoutes) {


        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();
        allRouteCoordinates = new ArrayList<ArrayList<String>>();
        allRoutesPolylines = new ArrayList<>();

        //draw polyline for All the routes
        for(int routeNo = 0; routeNo < allRoutes.size(); routeNo++  ){

            List<LatLng> route = allRoutes.get(routeNo);
            ArrayList  singleRoutePoints = new ArrayList();
            routeCoordinates = new ArrayList<String>();



           // singleRoutePoints
            //draw polyline for one route
            for (int i = 0; i < route.size(); i++) {

                lineOptions = new PolylineOptions();

                LatLng position = route.get(i);
                //fetching 50% coordinate to plot polyline. (reduce the processing)
                if (i % 2 == 0) {
                  //  double lat = Double.parseDouble((String) path.get("lat"));
                   // double lng = Double.parseDouble((String) path.get("long"));
                    {
                        if (i % 10 == 0) {
                            String lat =  Double.toString( position.latitude);
                          String lng =  Double.toString( position.longitude);

                          routeCoordinates.add("\"" + lat + ", "  + lng + "\"" ) ;




                          //["-36.878, 144.21337","-36.87774, 144.21347","-36.87705, 144.21378"]


                        }

                    }

                    singleRoutePoints.add(position);
                }
            }

                    allRouteCoordinates.add(routeCoordinates);
                   // destination = (LatLng) singleRoutePoints.get(singleRoutePoints.size() - 1);
                    lineOptions.addAll(singleRoutePoints);
                    lineOptions.width(12);
                    lineOptions.color(Color.YELLOW);
                    lineOptions.zIndex(1);
                    lineOptions.geodesic(true);

//  On Google Map Drawing polyline for the  route
                   Polyline polyline =  mMap.addPolyline(lineOptions);
                   polyline.setClickable(true);
                   polyline.setTag(routeNo);
                   polyline.setWidth(7);
                   allRoutesPolylines.add(polyline);


//                    mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
//                        @Override
//                        public void onPolylineClick(Polyline polyline) {
//                            polyline.setColor(Color.BLUE);
//
//
//
//                            Toast.makeText(MapsActivity.this, destination.toString(), Toast.LENGTH_SHORT).show();
//
//
//
//                        }
//                    });

        }

        dialogGetRoutes.dismiss();
        try {
            getAccidentForRoutes();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
 */
//Accident API

    private class DownloadAccidentData extends AsyncTask<Object, Void, JSONObject  >{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setView(R.layout.accidentdata_download_progress);
            dialogGetSafeRoute = builder.create();
            dialogGetSafeRoute.setCancelable(false);
             dialogGetSafeRoute.show();

        }
        @Override
        protected JSONObject  doInBackground(Object... accidentUrl) {
            JSONObject routesData = null;
            HashMap<String,Object> accidentRouteData = new HashMap<>();
            try {
                //accidentData = downloadUrl(accidentUrl[0]);

                routesData = getRoutesData((String) accidentUrl[0]);


            } catch (Exception e) {
                Log.d("Background Download routes Task", e.toString());
            }

         return routesData;
        }

        @Override
        protected void onPostExecute(JSONObject  routesData) {

            super.onPostExecute(routesData);

            Route route ;
            double routeDurationInHours;
            double routeLengthInKMs;
            int totalAccidentsForRoute;
            List<LatLng> routeLocationCoordinates;
            double accidentRate;
            String safetyRating;
            PolylineOptions lineOptions = null;
            ArrayList<PolylineOptions> allLineOptions = new ArrayList<>();
            allRouteSafetyRating = new ArrayList<>();
            allRouteAccidentRate = new ArrayList<>();
            allRoutesPolylines = new ArrayList<>();
            allRouteDetails = new ArrayList<>();

            MultiRouteparser routeParser = new MultiRouteparser();
            ArrayList<Route> allRoutes = new ArrayList<>();
            allRoutes = routeParser.parser(routesData);
            allRouteDetails = allRoutes;

            int totalRoutes = allRoutes.size();



            //processing all routes data to get each attribute
            for (int routeNo = 0; routeNo < allRoutes.size(); routeNo++){
                route = allRoutes.get(routeNo);

                routeLocationCoordinates = route.getRouteLocationCoordinates();
                routeDurationInHours =  route.getRouteDurationInSeconds()/(60.0 * 60.0);
                routeLengthInKMs = route.getRouteLengthInMeters()/1000.0 ;
                totalAccidentsForRoute = route.getTotalAccidents();
                accidentRate = totalAccidentsForRoute/routeLengthInKMs  * 10;
                allRouteAccidentRate.add(accidentRate);



                // accident data json array for a route
                allRouteAccidentData.add(route.getRouteAccidentData());
                lineOptions = new PolylineOptions();


                //adding all coordinates to get a polyline for a route
                lineOptions.addAll(routeLocationCoordinates);

                allLineOptions.add(lineOptions);

//                //processing location Coordinate for one route to get polyline
//                for(int index = 0; index < routeLocationCoordinates.size(); index++ )
//                {
//                    LatLng position = routeLocationCoordinates.get(index);
//
//                }


//                lineOptions.width(12);
//                lineOptions.color(Color.YELLOW);
//                lineOptions.zIndex(1);
//                lineOptions.geodesic(true);
//
//
//
//                //  On Google Map Drawing polyline for the  route
//                Polyline polyline =  mMap.addPolyline(lineOptions);
//                polyline.setClickable(true);
//                polyline.setTag(routeNo);
//                polyline.setWidth(7);
//                allRoutesPolylines.add(polyline);


            }


            int safest = allRouteAccidentRate.indexOf(Collections.min(allRouteAccidentRate)) ;
            int mostUnsafe  = allRouteAccidentRate.indexOf(Collections.max(allRouteAccidentRate));



            //PolylineOptions line ;
            for(int routeNo = 0; routeNo < allLineOptions.size(); routeNo ++ ){

                PolylineOptions line = allLineOptions.get(routeNo);

                if(routeNo == safest){
                    line.width(12);
                    line.zIndex(5);
                    line.color(0xFF50c878);  //Green
                    allRouteSafetyRating.add("Low Risk");
                }
                else if(routeNo == mostUnsafe ){
                    line.width(12);
                    line.zIndex(3);
                    line.color(0xFFED2939);  //Red    0xFFED2939
                    allRouteSafetyRating.add("High Risk");
                }
                else {
                    line.width(12);
                    line.zIndex(1);
                    line.color(0xFFF9A602);  //Amber
                    allRouteSafetyRating.add("Medium Risk");

                }

                line.geodesic(false);
                Polyline polyline =  mMap.addPolyline(line);
                polyline.setClickable(true);
                polyline.setTag(routeNo);
                allRoutesPolylines.add(polyline);
                //Toast.makeText(MapsActivity.this, "Polyline Num: " + String.valueOf(routeNo) , Toast.LENGTH_LONG).show();




            }
            Log.i("allRoutesPolylines:   ", allRoutesPolylines.toString());
            //Toast.makeText(MapsActivity.this, "allRoutesPolylines:   " +  allRoutesPolylines.toString(), Toast.LENGTH_LONG).show();



            /**
           // allRouteAccidentData.add(accidentRouteData);
            JSONObject accidentData = (JSONObject) accidentRouteData.get("accident Data");
            allRouteAccidentData.add(accidentData);
            routeAccidentDataCount += 1;
            String routeNumber = "";
            routeNumber =  accidentRouteData.get("Route No").toString();

            int route = Integer.parseInt(routeNumber) + 1;

            if(allRouteAccidentData.size() != allRoutesPolylines.size())
            {
                dialogGetSafeRoute.dismiss();
                Toast.makeText(MapsActivity.this, "dismissing...   " + allRouteAccidentData.size(), Toast.LENGTH_LONG).show();
            }

            Log.i("allRouteAccidentData download",allRouteAccidentData.toString());

            JSONArray accidentDataArray = null;
            String totalAccidentOnRoute = "";
            double lat = 0;
            double lng = 0;


            try {
                totalAccidentOnRoute =  accidentData.get("totalAccidents").toString();
                accidentDataArray = accidentData.getJSONArray(routeNumber);
                lat = (Double) accidentDataArray.getJSONObject(0).get("Latitude");
                lng = (Double) accidentDataArray.getJSONObject(0).get("Longitude");

            } catch (JSONException e) {
                e.printStackTrace();
            }

           if(totalAccidentOnRoute.equals(""))
           {
               totalAccidentOnRoute = "No Accidents";
           }
           String title = "Route No: "  + route + ", Accidents: " + totalAccidentOnRoute;
            String toast = "Route Number: " + route + "\n" + "Total Accident Count: " + totalAccidentOnRoute;
//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(lat,lng))
//                    .title(title));
           Toast.makeText(MapsActivity.this, toast, Toast.LENGTH_LONG ) .show();
           Toast.makeText(MapsActivity.this, "allRouteAccidentData: " + String.valueOf(allRouteAccidentData.size()), Toast.LENGTH_LONG).show();
            Toast.makeText(MapsActivity.this,"allRoutesPolylines: " +  String.valueOf(allRoutesPolylines.size()), Toast.LENGTH_LONG).show();

           if(allRouteAccidentData.size() == allRoutesPolylines.size() || totalAccidentOnRoute.equals("") )
           {
               try {

                   ArrayList<Integer> totalAccidentAllRoute = new ArrayList<>();
                   int routeTotalAccidentCount = 0;
                   String status = "";
                   accidentStatusTV = findViewById(R.id.accidentStatusid);

                   for(int routeNo =0; routeNo < allRouteAccidentData.size() ; routeNo++ )
                   {
                       routeTotalAccidentCount = Integer.parseInt((String) allRouteAccidentData.get(routeNo).get("totalAccidents")) ;
                       totalAccidentAllRoute.add(routeTotalAccidentCount);
                       status += "Route No : " + (routeNo +1) + "  Accidents: " + routeTotalAccidentCount;

                   }

                   accidentStatusTV.setText(status);
                   Collections.max(totalAccidentAllRoute);
                   Collections.min(totalAccidentAllRoute);

                   int safest = totalAccidentAllRoute.indexOf(Collections.min(totalAccidentAllRoute));
                   int mostAccidentProne = totalAccidentAllRoute.indexOf(Collections.max(totalAccidentAllRoute));
                   Polyline notSafe = allRoutesPolylines.get(mostAccidentProne);
                   notSafe.setColor(Color.RED);
                   notSafe.setZIndex(3);
                   notSafe.setWidth(8);
                   Polyline p = allRoutesPolylines.get(safest) ;
                   p.setColor(Color.GREEN);
                   p.setZIndex(5);
                   p.setWidth(11);
                  // safestRoute = safest;
                   //geofenceHandler(safest);
                   dialogGetSafeRoute.dismiss();


               } catch (JSONException e) {
                   e.printStackTrace();
               }


           }

           */
            dialogGetSafeRoute.dismiss();

           //
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=AIzaSyBZqLIP9yoJtQUwL-0vhgl0DBL_PVcQq6s";
        String alternatives = "alternatives=true";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key + "&" + alternatives;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters ;

//AIzaSyBZqLIP9yoJtQUwL-0vhgl0DBL_PVcQq6s
        //https://maps.googleapis.com/maps/api/directions/json?origin=-37.908544,145.136821&destination=-37.873888,145.044948&key=AIzaSyBZqLIP9yoJtQUwL-0vhgl0DBL_PVcQq6s
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    public JSONObject getRoutesData(String urlParams) {

        final String BASE_URL = "http://accident-api.eba-rjmccapm.ap-southeast-2.elasticbeanstalk.com/";
        String APIKey = "AIzaSyBZqLIP9yoJtQUwL-0vhgl0DBL_PVcQq6s";
        String getURL = BASE_URL + APIKey + "/" + urlParams;
//        JSONArray rArray = null;

        JSONObject accidentJSON = null;
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
            accidentJSON = jsonObject;
            accidentDatatTest = accidentJSON.toString();

            Log.i("accidentJSON", accidentJSON.toString() );


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        System.out.println(second);

        return accidentJSON;

    }

}

