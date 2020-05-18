import 'package:flutter/cupertino.dart';
import 'package:flutter_polyline_points/flutter_polyline_points.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'dart:async';
import 'package:geolocator/geolocator.dart';
import 'package:flutter/material.dart';
import 'requests/google_maps_requests.dart';
import 'package:flutter_google_places/flutter_google_places.dart';



void main() => runApp(HomePage());


class HomePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => HomeState();
}


class HomeState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        body: Map()
      ),
    );
  }
}



class Map extends StatefulWidget{
  @override
  _MapState createState() => _MapState();
}

class _MapState extends State<Map>{
  GoogleMapController mapController;
  GoolgeMapsServices _goolgeMapsServices = GoolgeMapsServices();
  TextEditingController locationController = TextEditingController();
  TextEditingController destinationController = TextEditingController();
  static LatLng _initialPosition;
  LatLng _lastPosition = _initialPosition;
  final Set<Marker> _markers = {};
  final Set<Polyline> _polylines = {};

  @override
  void initState(){
    super.initState();
    _getUserLocation();
  }
  
  @override
  Widget build(BuildContext context) {
    return _initialPosition == null? Container(
      alignment: Alignment.center,
      child: Center(
        child: CircularProgressIndicator()
      ),
    ) : Stack(
      children: <Widget>[
        GoogleMap(
          initialCameraPosition: CameraPosition(target: _initialPosition,
          zoom:10.0),
          onMapCreated: onCreated,
          myLocationEnabled: true,
          myLocationButtonEnabled: true,
          mapType: MapType.normal,
          compassEnabled: true,
          markers: _markers,
          onCameraMove: _onCameraMove,
          polylines: _polylines,
        ),
        Positioned(
          top: 50,
          right: 15,
          left: 15,
          child: Container(
            height: 50.0,
            width: double.infinity,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(3.0),
              color: Colors.white,
              boxShadow: [
                BoxShadow(
                  color: Colors.grey,
                  offset: Offset(1.0,5.0),
                  blurRadius: 10,
                  spreadRadius: 3
                )
              ]
            ),
            child: TextField(
              cursorColor: Colors.blue.shade900,
              controller: locationController,
              decoration: InputDecoration(
                icon: Container(margin: EdgeInsets.only(left: 20,top: 5),
                width: 10,
                height: 10,
                child: Icon(Icons.location_on,color: Colors.blue.shade900,),),
                hintText: "Start Location",
                border: InputBorder.none,
                contentPadding: EdgeInsets.only(left: 15.0,top:16.0)
              ),
            ),
          )
        ),
        Positioned(
            top: 105,
            right: 15,
            left: 15,
            child: Container(
              height: 50.0,
              width: double.infinity,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(3.0),
                  color: Colors.white,
                  boxShadow: [
                    BoxShadow(
                        color: Colors.grey,
                        offset: Offset(1.0,5.0),
                        blurRadius: 10,
                        spreadRadius: 3
                    )
                  ]
              ),
              child: TextField(
                cursorColor: Colors.blue.shade900,
                controller: destinationController,
                textInputAction: TextInputAction.go,
                onSubmitted: (value){
                  sendRequest(value);
                },
                decoration: InputDecoration(
                    icon: Container(margin: EdgeInsets.only(left: 20,top: 5),
                      width: 10,
                      height: 10,
                      child: Icon(Icons.local_taxi,color: Colors.blue.shade900,),),
                    hintText: "End Location",
                    border: InputBorder.none,
                    contentPadding: EdgeInsets.only(left: 15.0,top:16.0)
                ),
              ),
            )
        ),


      ]
    );
    throw UnimplementedError();
  }

  void onCreated(GoogleMapController controller) {
    setState(() {
      mapController = controller;
    });
  }

  void _onCameraMove(CameraPosition position) {
    setState(() {
      _lastPosition = position.target;
    });
  }

  void _addMarker(LatLng location, String address) {
      setState(() {
        _markers.add(Marker(markerId: MarkerId(_lastPosition.toString()),
        position: location,
          infoWindow: InfoWindow(
            title: address,
            snippet: "go here"
          ),
          icon: BitmapDescriptor.defaultMarker
        ));
      });
  }

  void createRoute(String encodedPolyline){
    List<LatLng> polylineCoordinates = [];
    PolylinePoints p = PolylinePoints();
    List<PointLatLng> result = p.decodePolyline(encodedPolyline);
    int i = 0;
    result.forEach((PointLatLng point){
      if (i%2 == 0){
        polylineCoordinates.add(
            LatLng(point.latitude, point.longitude));
      }
      i+=1;
    });
    setState(() {
      _polylines.add(Polyline(polylineId: PolylineId(_lastPosition.toString()),
      width: 10,
      points: polylineCoordinates,
      color: Colors.lightGreen));
    });
  }

  void _getUserLocation() async{
    Position position = await Geolocator().getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    List<Placemark> placemark = await Geolocator().placemarkFromCoordinates(position.latitude, position.longitude);
    setState(() {
      _initialPosition = LatLng(position.latitude,position.longitude);
      locationController.text = placemark[0].name;
    });
  }

  void sendRequest(String intendedLocation) async{
    List<Placemark> placemark = await Geolocator().placemarkFromAddress(intendedLocation);
    double latitude = placemark[0].position.latitude;
    double longitude = placemark[0].position.longitude;
    LatLng destination = LatLng(latitude,longitude);
    _addMarker(destination,intendedLocation);
    String route = await _goolgeMapsServices.getRouteCoordinates(_initialPosition,destination);
    createRoute(route);
  }
}