package com.example.roadprotector;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private int routeNo;
    private JSONArray routeAccidentData;
    private List<LatLng> routeLocationCoordinates;
    private int routeDurationInSeconds;
    private int routeLengthInMeters;
    private int totalAccidents;

    public Route(int routeNo, JSONArray routeAccidentData, List<LatLng> routeLocationCoordinates, int routeDurationInSeconds, int routeLengthInMeters, int totalAccidents) {
        this.routeNo = routeNo;
        this.routeAccidentData = routeAccidentData;
        this.routeLocationCoordinates = routeLocationCoordinates;
        this.routeDurationInSeconds = routeDurationInSeconds;
        this.routeLengthInMeters = routeLengthInMeters;
        this.totalAccidents = totalAccidents;
    }

    public int getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(int routeNo) {
        this.routeNo = routeNo;
    }

    public JSONArray getRouteAccidentData() {
        return routeAccidentData;
    }

    public void setRouteAccidentData(JSONArray routeAccidentData) {
        this.routeAccidentData = routeAccidentData;
    }

    public List<LatLng> getRouteLocationCoordinates() {
        return routeLocationCoordinates;
    }

    public void setRouteLocationCoordinates(List<LatLng> routeLoctionCoordinates) {
        this.routeLocationCoordinates = routeLoctionCoordinates;
    }

    public int getRouteDurationInSeconds() {
        return routeDurationInSeconds;
    }

    public void setRouteDurationInSeconds(int routeDurationInSeconds) {
        this.routeDurationInSeconds = routeDurationInSeconds;
    }

    public int getRouteLengthInMeters() {
        return routeLengthInMeters;
    }

    public void setRouteLengthInMeters(int routeLengthInMeters) {
        this.routeLengthInMeters = routeLengthInMeters;
    }

    public int getTotalAccidents() {
        return totalAccidents;
    }

    public void setTotalAccidents(int totalAccidents) {
        this.totalAccidents = totalAccidents;
    }
}
