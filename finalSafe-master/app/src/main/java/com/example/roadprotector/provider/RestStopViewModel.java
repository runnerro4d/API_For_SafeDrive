package com.example.roadprotector.provider;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class RestStopViewModel extends AndroidViewModel {
    private RestStopRepository restStopRepository;
    private List<RestStop> allRestStops;

    public RestStopViewModel(@NonNull Application application) {
        super(application);
        restStopRepository = new RestStopRepository(application);
        allRestStops = restStopRepository.getAllRestStops();
    }

    // retrieve all the rest stops
    public List<RestStop> getAllRestStops() {
        return allRestStops;
    }

    // retrieve filtered set of reststops
    public List<RestStop> getFilteredRestStops(int disabledToilet, int fastfood, int picnictable, int toilet, int water){
        List<RestStop> filteredRestStops;
        filteredRestStops = restStopRepository.getFilteredRestStops(disabledToilet, fastfood, picnictable, toilet, water);
        return filteredRestStops;
    }

    // insert rest stop into table
    public void insert(RestStop restStop) {
        restStopRepository.insert(restStop);
    }

    // delete all rest stops
    public void deleteAll() {
        restStopRepository.deleteAll();
    }

}
