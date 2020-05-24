package com.example.roadprotector;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadprotector.provider.RestStop;
import com.example.roadprotector.provider.RestStopViewModel;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

import static com.google.maps.android.PolyUtil.isLocationOnPath;

public class NavigationActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private AlertDialog.Builder alertbuilder;
    private  AlertDialog alertDialog;
    Location currentLocation;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    AlertDialog.Builder builder = null;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private ArrayList<JSONArray> allRouteAccidentData ;
    private boolean downloadDataFlag = false;
    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();
    ArrayList<Polyline> allRoutesPolylines ;
    int routeAccidentDataCount = 0;
    private LatLng routeDestination;
    private String destnationName;
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
    private LocationManager locationManager;
    private List<LatLng> selectedPolyline;
    private MediaPlayer mediaPlayer;
    private RestStopViewModel restStopViewModel;
    private ArrayList<LatLng> selectedRestStop;
    private ArrayList<LatLng> rejectedRestStops;
    private ArrayList<Marker> restStopMarker = new ArrayList<>();
    private String destinationPlace = "";
    private TextView accidentStatusTV;
    private TextView routeDetailTV;
    private TextView routeHeaderTV;
    private TextView routeNumberTV;
    private TextView routeRiskTV;
    private TextView routeDistanceTV;
    private TextView routeDurationTV;
    private TextView restStopID;
    private TextView toilet;
    private TextView water;
    private TextView wheelChair;
    private TextView fastFood;
    private TextView picnicTables;
    private Button navigate;
    private Button showRestStop;
    private FloatingActionButton restStopfab;
    private FloatingActionButton disabledToiletfab;
    private FloatingActionButton fastFoodfab;
    private FloatingActionButton picnicTablefab;
    private FloatingActionButton toiletfab;
    private FloatingActionButton waterfab;
    static  final String FILE_NAME = "myRestStopPref";
    static final String FIRST_INSTALL = "firstInstall";
    static final String DISABLED_TOILET = "disabledToilet";
    static final String FAST_FOOD = "fastFood";
    static final String PICNIC_TABLE = "picnicTable";
    static final String TOILET = "toilet";
    static final String WATER = "water";
    private boolean disabledToiletState = false;
    private boolean fastFoodState = false;
    private boolean picnicTableState = false;
    private boolean toiletState = false;
    private boolean waterState = false;








    //View view = getLayoutInflater().inflate(R.layout.progress);
    Dialog dialogGetRoutes = null;
    Dialog dialogGetSafeRoute = null;

    ConstraintLayout constraintLayout;
    BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        constraintLayout = (ConstraintLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);

        restStopfab = findViewById(R.id.Rest0);
        disabledToiletfab = findViewById(R.id.Rest1);
        waterfab = findViewById(R.id.Rest2);
        toiletfab = findViewById(R.id.Rest3);
        fastFoodfab = findViewById(R.id.Rest4);
        picnicTablefab = findViewById(R.id.Rest5);

        disabledToiletfab.hide();
        waterfab.hide();
        toiletfab.hide();
        fastFoodfab.hide();
        picnicTablefab.hide();

        disabledToiletfab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));
        waterfab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));
        toiletfab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));
        fastFoodfab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));
        picnicTablefab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));

        disabledToiletState = false;
        fastFoodState = false;
        picnicTableState = false;
        toiletState = false;
        waterState = false;


        SharedPreferences restStopPref = getSharedPreferences(FILE_NAME,0);
        SharedPreferences.Editor edit =  restStopPref.edit();
        edit.putInt(DISABLED_TOILET, 0);
        edit.putInt(FAST_FOOD, 0);
        edit.putInt(TOILET, 0);
        edit.putInt(PICNIC_TABLE, 0);
        edit.putInt(WATER, 0);
        edit.commit();





        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        // makeRequest("");
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeoFenceHelper(this);


        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeoFenceHelper(this);

        restStopViewModel = new ViewModelProvider(this).get(RestStopViewModel.class);


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
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)   getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        //     AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
        //                 getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setLocationRestriction(rectangularBounds);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setCountry("AU");


// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                routeDestination = place.getLatLng();
                destinationPlace = place.getName();

                if(mMap != null)
                {
                    mMap.clear();
                }
                removeGeofence();
                if (selectedPolyline != null)
                    selectedPolyline.clear();

                LatLng currentLocLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                startRoute(currentLocLatLng, routeDestination, true);
                Log.i("placess", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("places", "An error occurred: " + status);
            }
        });


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
                    supportMapFragment.getMapAsync(NavigationActivity.this);
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

        LatLng currentLocLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
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
//                 removeGeofence();
//
//                    markerPoints.clear();
//                    mMap.clear();
//                    if (selectedPolyline != null)
//                          selectedPolyline.clear();
//
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
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                    options.snippet("Destination")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_flag));
//                }
//
//                // Add new marker to the Google Map Android API V2
//                mMap.addMarker(options);
//
//
//                // Checks, whether start and end locations are captured
//                if (markerPoints.size() >= 1) {
//                    LatLng origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());;
//                    LatLng dest = (LatLng) markerPoints.get(0);
//                    routeDestination = dest;
//                    //destination = dest;
//
//                    startRoute(origin, routeDestination, false);
//
//
////                    //initialising the array of Accident API responses
////                    allRouteAccidentData = new ArrayList<JSONArray>();
////                    routeAccidentDataCount = 0;
////                    accidentStatusTV = findViewById(R.id.accidentStatusid);
////                    accidentStatusTV.setText("");
////
////
////                    //["-37.349852, 144.548298","-37.417703, 144.995094"]
////                    String urlParams = "[\"" + origin.latitude + ", " + origin.longitude + "\",\"" + dest.latitude + ", " + dest.longitude + "\"]";
////
////                    DownloadAccidentData download = new DownloadAccidentData();
////                    download.execute(urlParams);
//                    // Getting URL to the Google Directions API
//                    // String url = getDirectionsUrl(origin, dest);
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


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String s = markerOptions.getSnippet();

                restStopID = findViewById(R.id.RestStopID);

                restStopID.setText(s);


                return true;
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
//                Color polyColor = Color.valueOf(polyline.getColor()) ;
//                polyline.setColor(Color.BLUE);
               // selectedPolyline = polyline;



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


//                polyline.setColor( polyColor.hashCode());


            }
        });

        // Set up navigation action
        navigate = findViewById(R.id.navigateId);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {

                if(routeDestination == null )
                {
                    Toast.makeText(NavigationActivity.this, "Please select the destination", Toast.LENGTH_LONG).show();
                }

//                try {
//                    geofenceHandler(safestRoute);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                if(routeDestination != null) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    updateLocationChange();

//                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.toString(destNavigate.latitude) + "," + Double.toString(destNavigate.longitude));
//                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                    mapIntent.setPackage("com.google.android.apps.maps");
//                    startActivity(mapIntent);
                }
            }
        });

        //setup rest stop display action
        showRestStop = findViewById(R.id.getreststopibuttonid);
        showRestStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(routeDestination == null )
                {
                    Toast.makeText(NavigationActivity.this, "Please select the destination!", Toast.LENGTH_LONG).show();
                }

                if(selectedPolyline == null)
                {
                    Toast.makeText(NavigationActivity.this, "Please select the route!", Toast.LENGTH_LONG).show();
                }
                else if(selectedPolyline.size() == 0){
                    Toast.makeText(NavigationActivity.this, "Please select the route!", Toast.LENGTH_LONG).show();
                }
                else {
                    showRestStops();
                }

            }
        });


        restStopfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

          /*      saveFields2Storage();
                Snackbar.make(view, "Item Saved!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            if(!disabledToiletfab.isShown())
                 disabledToiletfab.show();
            else
                 disabledToiletfab.hide();

            if(!fastFoodfab.isShown())
                fastFoodfab.show();
            else
                fastFoodfab.hide();

            if(!toiletfab.isShown())
                toiletfab.show();
            else
                toiletfab.hide();

            if(!picnicTablefab.isShown())
                picnicTablefab.show();
            else
                picnicTablefab.hide();

            if(!waterfab.isShown())
                waterfab.show();
            else
                waterfab.hide();
            }
        });

        disabledToiletfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "";
                SharedPreferences restStopPref = getSharedPreferences(FILE_NAME,0);
                SharedPreferences.Editor edit =  restStopPref.edit();
                if(disabledToiletState)
                {
                    disabledToiletState = false;
                    edit.putInt(DISABLED_TOILET,0);
                    status = "No";

                    //grey
                    disabledToiletfab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));

                }
                else{
                    disabledToiletState = true;
                    edit.putInt(DISABLED_TOILET,1);
                    status = "Yes";

                    //quantum
                    disabledToiletfab.setBackgroundTintList(ColorStateList.valueOf(0xffffe082));

                }
                edit.commit();
                Snackbar.make(view, "preference Saved! Disabled Toilet - " + status, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        fastFoodfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "";
                SharedPreferences restStopPref = getSharedPreferences(FILE_NAME,0);
                SharedPreferences.Editor edit =  restStopPref.edit();
                if(fastFoodState)
                {
                    fastFoodState = false;
                    edit.putInt(FAST_FOOD,0);
                    status = "No";

                    //grey
                    fastFoodfab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));

                }
                else{
                    fastFoodState = true;
                    edit.putInt(FAST_FOOD,1);
                    status = "Yes";

                    //quantum
                    fastFoodfab.setBackgroundTintList(ColorStateList.valueOf(0xffffe082));

                }
                edit.commit();
                Snackbar.make(view, "preference Saved! Fast Food - " + status, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        toiletfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "";
                SharedPreferences restStopPref = getSharedPreferences(FILE_NAME,0);
                SharedPreferences.Editor edit =  restStopPref.edit();
                if(toiletState)
                {
                    toiletState = false;
                    edit.putInt(TOILET,0);
                    status = "No";

                    //grey
                    toiletfab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));

                }
                else{
                    toiletState = true;
                    edit.putInt(TOILET,1);
                    status = "Yes";

                    //quantum
                    toiletfab.setBackgroundTintList(ColorStateList.valueOf(0xffffe082));

                }
                edit.commit();
                Snackbar.make(view, "preference Saved! Toilet - " + status, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        waterfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "";
                SharedPreferences restStopPref = getSharedPreferences(FILE_NAME,0);
                SharedPreferences.Editor edit =  restStopPref.edit();
                if(waterState)
                {
                    waterState = false;
                    edit.putInt(WATER,0);
                    status = "No";

                    //grey
                    waterfab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));

                }
                else{
                    waterState = true;
                    edit.putInt(WATER,1);
                    status = "Yes";

                    //quantum
                    waterfab.setBackgroundTintList(ColorStateList.valueOf(0xffffe082));

                }
                edit.commit();
//                Snackbar.make(view, "preference Saved! Water - " + status, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Toast.makeText(NavigationActivity.this, "preference Saved! Water - " + status, Toast.LENGTH_SHORT).show();



            }
        });

        picnicTablefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "";
                SharedPreferences restStopPref = getSharedPreferences(FILE_NAME,0);
                SharedPreferences.Editor edit =  restStopPref.edit();
                if(picnicTableState)
                {
                    picnicTableState = false;
                    edit.putInt(PICNIC_TABLE,0);
                    status = "No";

                    //grey
                    picnicTablefab.setBackgroundTintList(ColorStateList.valueOf(0xffb1afaf));

                }
                else{
                    picnicTableState = true;
                    edit.putInt(PICNIC_TABLE,1);
                    status = "Yes";

                    //quantum
                    picnicTablefab.setBackgroundTintList(ColorStateList.valueOf(0xffffe082));

                }
                edit.commit();
                Snackbar.make(view, "preference Saved: Picnic Table - " + picnicTableState, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

    public void showRestStops(){

        for(Marker marker:  restStopMarker )
        {
            marker.remove();
        }
        restStopMarker.clear();

        selectedRestStop = new ArrayList<>();
        rejectedRestStops = new ArrayList<>();
        final List<RestStop> filteredListRestStop;
        SharedPreferences sharedPref = getSharedPreferences(FILE_NAME, 0);
        int disabledToilets = sharedPref.getInt(DISABLED_TOILET, -999);
        int fastFood = sharedPref.getInt(FAST_FOOD, -999);
        int picnicTable = sharedPref.getInt(PICNIC_TABLE, -999);
        int toilet = sharedPref.getInt(TOILET, -999);
        int water = sharedPref.getInt(WATER, -999);
        String disabled = "";
        String FastFood = "";
        String picnic = "";
        String toiletpresent = "";
        String waterpresent = "";


        if((disabledToilets == 0) && fastFood == 0  && picnicTable == 0 && toilet == 0 && water == 0){
            filteredListRestStop = restStopViewModel.getAllRestStops();
            Toast.makeText(NavigationActivity.this, filteredListRestStop.size()+ "", Toast.LENGTH_LONG).show();

        }
        else
            filteredListRestStop = restStopViewModel.getFilteredRestStops(disabledToilets, fastFood, picnicTable, toilet, water);

        if(filteredListRestStop.size() == 0){
            Toast.makeText(this,"Ops! no rest stops for set the preference.", Toast.LENGTH_LONG).show();
            // dialog for no rest areas
//            alertbuilder = new AlertDialog.Builder(this);
//            alertbuilder.setMessage("Ops! No rest stops present for selected preferences.") .setTitle("Alert!")
//            .setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            //Creating dialog box
//            AlertDialog alertdg = alertbuilder.create();
//            alertdg.cancel();
        }
        else

        {
           // Toast.makeText(this, filteredListRestStop.size() + "", Toast.LENGTH_LONG).show();
            // fetching rest stop location
            for(int i =0; i< filteredListRestStop.size(); i++){
                LatLng restStop = new LatLng(filteredListRestStop.get(i).getLat(), filteredListRestStop.get(i).getLng());
                String restStopNum = "No: " + String.valueOf(filteredListRestStop.get(i).getReststopNumber());


                if (filteredListRestStop.get(i).getDisabledToilet() == 1){
                    disabled = "Disabled Toilet: Y," ;
                } else if (filteredListRestStop.get(i).getDisabledToilet() == 0)
                {
                    disabled = "Disabled Toilet: N,"  ;
                }

                if (filteredListRestStop.get(i).getFastfood() == 1){
                    FastFood = "FastFood: Y,";
                } else if (filteredListRestStop.get(i).getFastfood() == 0)
                {
                    FastFood = "FastFood: N,";
                }

                if (filteredListRestStop.get(i).getPicnictable() == 1){
                    picnic = "Picnic Table: Y,";
                } else if (filteredListRestStop.get(i).getPicnictable() == 0)
                {
                    picnic = "Picnic Table: N,";
                }

                if (filteredListRestStop.get(i).getToilet() == 1){
                    toiletpresent = "Toilet: Y,";
                } else if (filteredListRestStop.get(i).getToilet() == 0)
                {
                    toiletpresent = "Toilet: N,";
                }

                if (filteredListRestStop.get(i).getWater() == 1){
                    waterpresent = "Water: Y";
                } else if (filteredListRestStop.get(i).getWater() == 0)
                {
                    waterpresent = "Water: N";
                }

                if(isLocationOnPath(restStop, selectedPolyline, false, 1000)){

//                    mMap.addMarker(new MarkerOptions()
//                            .position(restStop)
//                            .title("Rest Stop").snippet(disabled + FastFood + picnic + toiletpresent + waterpresent).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    selectedRestStop.add(restStop);
                }
                else{
                    rejectedRestStops.add(restStop);
//                    alertbuilder = new AlertDialog.Builder(this);
//                    alertbuilder.setMessage("Ops! No rest stops present on the selected route.") .setTitle("Alert!")
//                            .setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });

                    //Creating dialog box
//                    AlertDialog alertdg = alertbuilder.create();
//                    alertdg.show();
                   // Toast.makeText(this,"No rest stops for selected route!", Toast.LENGTH_LONG).show();
                }
            }
        }

        if(selectedRestStop.size()==0){
            Toast.makeText(this,"No rest stops for selected route!", Toast.LENGTH_LONG).show();
        }

        if(selectedRestStop.size()!=0){
            for(int i = 0; i< selectedRestStop.size(); i++){
                Marker restMarker = mMap.addMarker(new MarkerOptions()
                        .position(selectedRestStop.get(i))
                        .title("Rest Stop").snippet(disabled + FastFood + picnic + toiletpresent + waterpresent).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                restStopMarker.add(restMarker);
            }

        }

        Log.i("selctedstops"  , selectedRestStop.toString());
        Log.i("rejectedStops", rejectedRestStops.toString());
    }

    // update user current location on map when user navigates on the route
    protected  void updateLocationChange(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // time miliseconds distance meters
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //updating current location
                currentLocation = location;

                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());


                updateCameraPosition(mMap, location.getBearing(),position);

                // If user goes away from slected route notify
                if(selectedPolyline != null) {
                    if (!isLocationOnPath(position, selectedPolyline, true, 1000)) {
                        mediaPlayer = MediaPlayer.create(NavigationActivity.this, R.raw.not_on_route);
                        mediaPlayer.start();
                        Toast.makeText(NavigationActivity.this, "Caution! not on selected route", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


    }

    private void updateCameraPosition(GoogleMap map, float bearing,LatLng position) {
        if ( map == null) return;
        CameraPosition cameraPosition = CameraPosition
                .builder(
                        map.getCameraPosition() // current Camera
                )
                .target(position)
                .zoom(18)
                .bearing(bearing)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }




    public void displayRouteSafety(int routeNo) throws JSONException {
        selectedPolyline = allRouteDetails.get(routeNo).getRouteLocationCoordinates();
        bottomSheetBehavior.setPeekHeight(160, true);
        Log.i("allRouteDetails", allRouteDetails.toString());
        //Toast.makeText(this, allRouteDetails.toString(), Toast.LENGTH_LONG).show();

        int routeDurationInSeconds = allRouteDetails.get(routeNo).getRouteDurationInSeconds();
        int routeLengthInMeters  = allRouteDetails.get(routeNo).getRouteLengthInMeters();




        // Convert seconds to Hrs:min:ss
        int secs = routeDurationInSeconds % 60;
        int d2 = routeDurationInSeconds / 60;
        int mins = d2 % 60;
        int hrs = d2 / 60;

        String duration = String.format("%02d hrs %02d mins", hrs, mins);

        //Convert Distance
        int meters = routeLengthInMeters % 1000;
        int kms = routeLengthInMeters / 1000;

        String distance = String.format("%02d kms", kms);




        String routeDetail = "Route No: " + (routeNo + 1 ) + "\n" + "Safety Rating: " + allRouteSafetyRating.get(routeNo) +
                "\n" + "Duration: " +  duration +
                "\n" + "Distance: " + distance;

        routeHeaderTV = findViewById(R.id.routeDetailId);
        routeNumberTV = findViewById(R.id.routeNumberId);
        routeRiskTV = findViewById(R.id.RouteRisk);
        routeDistanceTV = findViewById(R.id.RouteDistance);
        routeDurationTV = findViewById(R.id.RouteDuration);

        routeHeaderTV.setText("Destination: "  + destinationPlace );
        routeNumberTV.setText("Route No: " + (routeNo + 1 ));
        routeRiskTV.setText("Risk: " +  allRouteSafetyRating.get(routeNo));
        routeDistanceTV.setText("Distance: " + distance);
        routeDurationTV.setText("Duration: " + duration);



        //routeDetailTV.setText(routeDetail);

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
                            radiusInKM = 1.0;
                        }

                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(center);
                        circleOptions.radius(radiusInKM * 1000);
                        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                        circleOptions.fillColor(Color.argb(64, 255, 89, 0));
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
                        radiusInKM = 1.0;
                    }

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(center);
                    circleOptions.radius(radiusInKM * 1000);
                    circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                    circleOptions.fillColor(Color.argb(64, 255, 89, 0));
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



    private class DownloadAccidentData extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            builder = new AlertDialog.Builder(NavigationActivity.this);
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
                    allRouteSafetyRating.add("Lowest");
                }
                else if(routeNo == mostUnsafe ){
                    line.width(12);
                    line.zIndex(3);
                    line.color(0xFFED2939);  //Red    0xFFED2939
                    allRouteSafetyRating.add("Highest");
                }
                else {
                    line.width(12);
                    line.zIndex(1);
                    line.color(0xFFF9A602);  //Amber
                    allRouteSafetyRating.add("Medium");

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

                     dialogGetSafeRoute.dismiss();
            // Display details of safest route
            try {
                displayRouteSafety(safest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
        String key = "key=AIzaSyCYfveclaFp0QlPtfon7Q7pJg35OBAkqrU";
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
        String APIKey = "AIzaSyCYfveclaFp0QlPtfon7Q7pJg35OBAkqrU";
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
