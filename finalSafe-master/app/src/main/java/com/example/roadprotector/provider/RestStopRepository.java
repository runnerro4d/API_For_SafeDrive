package com.example.roadprotector.provider;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class RestStopRepository {
    private RestStopDao restStopDao;
    private List<RestStop> allRestStops;
    //private List<RestStop> filteredRestStops;

    RestStopRepository(Application application) {
        RestStopDatabase db=RestStopDatabase.getDatabase(application);
        restStopDao = db.restStopDao();
        allRestStops = restStopDao.getAllReststops();
    }

    List<RestStop> getAllRestStops(){
        return allRestStops;
    }

    // retrieve filtered set of reststops
    List<RestStop> getFilteredRestStops(int disabledToilet, int fastfood, int picnictable, int toilet, int water){
        List<RestStop> filteredRestStops;
        filteredRestStops = restStopDao.getReststops(disabledToilet, fastfood, picnictable, toilet, water);

        return filteredRestStops;
    }

    // insert rest stop into table
    void insert(RestStop restStop){
        RestStopDatabase.databaseWriteExecutor.execute(() -> restStopDao.addReststop(restStop));
    }

    void deleteAll(){
        RestStopDatabase.databaseWriteExecutor.execute(()->{
            restStopDao.deleteAllReststops();
        });
    }

    // delete specific rest stop
    void delete(int restStopNumber){
        RestStopDatabase.databaseWriteExecutor.execute(()->{
            restStopDao.deleteReststop(restStopNumber);
        });

    }









}
