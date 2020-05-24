package com.example.roadprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.roadprotector.provider.RestStop;
import com.example.roadprotector.provider.RestStopViewModel;

import java.util.ArrayList;
import java.util.List;

public class RestStopActivity extends AppCompatActivity {

    Button deleteAll;
    Button getAll;
    Button getFiltered;
    TextView allRestStops;
    TextView filteretedRestStop;
    RestStopViewModel restStopViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_stop);

        deleteAll = findViewById(R.id.deleteallid);
        getAll = findViewById(R.id.getallid);
        getFiltered = findViewById(R.id.getfilteredid);

        filteretedRestStop = findViewById(R.id.filteredrestid);
        allRestStops = findViewById(R.id.allrestid);

        restStopViewModel = new ViewModelProvider(this).get(RestStopViewModel.class);

        getAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displayAll();
            }
        });

        getFiltered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtered();
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restStopViewModel.deleteAll();
            }
        });



    }




    public void filtered(){

        SharedPreferences sp = getSharedPreferences(RestStopPrefActivity.FILE_NAME, 0);
        int dt= sp.getInt(RestStopPrefActivity.DISABLED_TOILET, -999);
        int ff= sp.getInt(RestStopPrefActivity.FAST_FOOD, -999);
        int pt= sp.getInt(RestStopPrefActivity.PICNIC_TABLE, -999);
        int t= sp.getInt(RestStopPrefActivity.TOILET, -999);
        int w= sp.getInt(RestStopPrefActivity.WATER, -999);

         List<RestStop> filteredList = restStopViewModel.getFilteredRestStops(dt, ff, pt, t, w);

        String str = "";
        for(int i =0; i< filteredList.size(); i++){
            str += filteredList.get(i).getLat() ;
            str += "  ";
            str += filteredList.get(i).getLng() + "\n";

        }


        filteretedRestStop.setText(str);


    }

//    public void displayAll(){
//
//        restStopViewModel.getAllRestStops().observe(this, newData -> {
//            allRestStops.setText(newData.toString());
//
//        });
//    }


}
