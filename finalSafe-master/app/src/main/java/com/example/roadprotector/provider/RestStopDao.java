package com.example.roadprotector.provider;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface RestStopDao {

    @Query("select * from reststop")
    List<RestStop> getAllReststops();

    @Query("select * from reststop where disableledToiletFlag= :disabledToilet and fastfoodFlag= :fastfood " +
            "and picnictableFlag= :picnictable and toiletFlag= :toilet and waterFlag =:water" )
    List<RestStop> getReststops(int disabledToilet, int fastfood, int picnictable, int toilet, int water);

    @Insert
    void addReststop(RestStop restStop);

    @Query("delete from reststop where reststopNumber= :number")
    void deleteReststop(int number);

    @Query("delete FROM reststop")
    void deleteAllReststops();

}
