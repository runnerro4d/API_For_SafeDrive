package com.example.roadprotector.provider;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reststop")
public class RestStop {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "reststopId")
    private int id;
    @ColumnInfo(name = "reststopNumber")
    private int reststopNumber;
    @ColumnInfo(name = "reststopLatitude")
    private double lat;
    @ColumnInfo(name = "reststopLongitude")
    private double lng;
    @ColumnInfo(name = "disableledToiletFlag")
    private int disabledToilet;
    @ColumnInfo(name = "fastfoodFlag")
    private int fastfood;
    @ColumnInfo(name = "picnictableFlag")
    private int picnictable;
    @ColumnInfo(name = "toiletFlag")
    private int toilet;
    @ColumnInfo(name = "waterFlag")
    private int water;


    public RestStop( int reststopNumber, double lat, double lng, int disabledToilet, int fastfood, int picnictable, int toilet, int water) {
        this.reststopNumber = reststopNumber;
        this.lat = lat;
        this.lng = lng;
        this.disabledToilet = disabledToilet;
        this.fastfood = fastfood;
        this.picnictable = picnictable;
        this.toilet = toilet;
        this.water = water;
    }


    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getReststopNumber() {
        return reststopNumber;
    }

    public void setReststopNumber(int reststopNumber) {
        this.reststopNumber = reststopNumber;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getDisabledToilet() {
        return disabledToilet;
    }

    public void setDisabledToilet(int disabledToilet) {
        this.disabledToilet = disabledToilet;
    }

    public int getFastfood() {
        return fastfood;
    }

    public void setFastfood(int fastfood) {
        this.fastfood = fastfood;
    }

    public int getPicnictable() {
        return picnictable;
    }

    public void setPicnictable(int picnictable) {
        this.picnictable = picnictable;
    }

    public int getToilet() {
        return toilet;
    }

    public void setToilet(int toilet) {
        this.toilet = toilet;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }
}

