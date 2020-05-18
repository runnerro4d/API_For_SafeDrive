from flask import Flask, request
from flask_restful import Resource, Api
from flask_mysqldb import MySQL
from flask_jsonpify import jsonify
import pandas as pd
import os
import json
import googlemaps
from geopy.geocoders import GoogleV3
from polyline.codec import PolylineCodec

application = Flask(__name__)

if 'RDS_HOSTNAME' in os.environ:
   application.config['MYSQL_HOST'] = os.environ['RDS_HOSTNAME']
   application.config['MYSQL_USER'] = os.environ['RDS_USERNAME']
   application.config['MYSQL_PASSWORD'] = os.environ['RDS_PASSWORD']
   application.config['MYSQL_DB'] = os.environ['RDS_DB_NAME']

mysql = MySQL(application)

api = Api(application)

class Accident_Info(Resource):
    def get(self):
        cur = mysql.connection.cursor()
        df = pd.read_sql('SELECT * from AccidentCluster limit 10', con=mysql.connection)
        val = {'data':df.to_dict(orient='records')}
        return jsonify(val)

class Accident_Centroid(Resource):
    def get(self,apnk,datano,RoadData):
        # Adding Geolocation
        geolocation = GoogleV3(api_key=apnk)
        # Loading the coordinates received from the app
        RoadInfo = json.loads(RoadData)
        postcodes = []
        roadnames = []
        for i in RoadInfo:
            # Perform reverse geolocation for the point received in the API call
            newVal = geolocation.reverse(i)
            # Splitting the response based on commas
            roadstuff= newVal[1].address.split(', ')
            print(roadstuff)
            # If we get road names along with post codes follows format [road, suburb, Australia]
            if len(roadstuff) == 3:
                # Split road names based on spaces as roads may have format [number, roadname, roadtype]
                roadie = roadstuff[0].split(' ')
                if len(roadie) == 1:
                    roadie = roadie[0]
                elif len(roadie) == 2: 
                    if roadie[0].isdigit():
                        roadie = roadie[1]
                    else:
                
                        roadie = roadie[0]+' '+roadie[1]
                else:
                    roadie = roadie[1]+ ' '+roadie[2]
                areaa = roadstuff[1][-4:]
                postcodes.append(areaa)
                roadnames.append(roadie)
        roadset = set(roadnames)
        postset = set(postcodes)
        Roads = list(roadset)
        Postcodes = list(postset)

        print(*Roads)
        print(*Postcodes)
        cur = mysql.connection.cursor()
        df = pd.read_sql('select Longitude, Latitude, RadiusInKM, PostcodeNo, AccidentCount,concat(RoadName,\' \', RoadType) as Road from AccidentCluster;', con=mysql.connection)
        newdf = df[(df['Road'].isin(Roads)) & (df['PostcodeNo'].isin(Postcodes))] 
        val = {str(datano): newdf.to_dict(orient='records'),
        'totalAccidents': str(newdf['AccidentCount'].sum())}
        return jsonify(val)


class Accident_Data_All_LatLong(Resource):
    def get(self,apnk,AllRoadData):
        # Adding Geolocation
        geolocation = GoogleV3(api_key=apnk)
        RoadInfo = json.loads(AllRoadData)
        val = {}
        cur = mysql.connection.cursor()
        df = pd.read_sql('select Longitude, Latitude, RadiusInKM, PostcodeNo, AccidentCount,concat(RoadName,\' \', RoadType) as Road from AccidentCluster;', con=mysql.connection)
        for key in RoadInfo:
            RoadData = RoadInfo[key]
            postcodes = []
            roadnames = []
            for i in RoadData:
                # Perform reverse geolocation for each point received in the API call
                newVal = geolocation.reverse(i)
                # Splitting the response based on commas
                roadstuff= newVal[1].address.split(', ')
                # If we get road names along with post codes follows format [road, suburb, Australia]
                if len(roadstuff) == 3:
                    # Split road names based on spaces as roads may have format [number, roadname, roadtype]
                    roadie = roadstuff[0].split(' ')
                    if len(roadie) == 1:
                        roadie = roadie[0]
                    elif len(roadie) == 2: 
                        if roadie[0].isdigit():
                            roadie = roadie[1]
                        else:
                            roadie = roadie[0]+' '+roadie[1]
                    else:
                        roadie = roadie[1]+ ' '+roadie[2]
                    areaa = roadstuff[1][-4:]
                    postcodes.append(areaa)
                    roadnames.append(roadie)
            roadset = set(roadnames)
            postset = set(postcodes)
            Roads = list(roadset)
            Postcodes = list(postset)
            newdf = df[(df['Road'].isin(Roads)) & (df['PostcodeNo'].isin(Postcodes))] 
            val[key] = {'data': newdf.to_dict(orient='records'),
            'totalAccidents': str(newdf['AccidentCount'].sum())}
        return jsonify(val)

class All_Data(Resource):

    def get(self,apnk,LatLongs):
        # Adding Geolocation
        gmaps = googlemaps.Client(key=apnk)
        val = []
        cur = mysql.connection.cursor()
        df = pd.read_sql('select Longitude, Latitude, RadiusInKM, PostcodeNo, AccidentCount,concat(RoadName,\' \', RoadType) as Road from AccidentCluster;', con=mysql.connection)
        geolocation = GoogleV3(api_key=apnk)
        RoadInfo = json.loads(LatLongs)
        directions = gmaps.directions(RoadInfo[0],RoadInfo[1],mode="driving",alternatives=True)
        polylines = [i["overview_polyline"]["points"] for i in directions]
        routeLengths = []
        routeDurations = []
        for i in directions:
            totalLength = 0
            totalTime = 0
            for j in i["legs"]:
                totalLength += j["distance"]["value"]
                totalTime += j["duration"]["value"]
            routeLengths.append(totalLength)
            routeDurations.append(totalTime)

        for i in range(0,len(polylines)):
            list1 = PolylineCodec().decode(polylines[i])
            list1 = list1[0::10]
            coordinates = [str(i[0])+", "+ str(i[1]) for i in list1]
            postcodes = []
            roadnames = []
            for k in coordinates:
                # Perform reverse geolocation for each point received in the API call
                newVal = geolocation.reverse(k)
                # Splitting the response based on commas
                roadstuff= newVal[1].address.split(', ')
                # If we get road names along with post codes follows format [road, suburb, Australia]
                if len(roadstuff) == 3:
                    # Split road names based on spaces as roads may have format [number, roadname, roadtype]
                    roadie = roadstuff[0].split(' ')
                    if len(roadie) == 1:
                        roadie = roadie[0]
                    elif len(roadie) == 2: 
                        if roadie[0].isdigit():
                            roadie = roadie[1]
                        else:
                            roadie = roadie[0]+' '+roadie[1]
                    else:
                        roadie = roadie[1]+ ' '+roadie[2]
                    areaa = roadstuff[1][-4:]
                    postcodes.append(areaa)
                    roadnames.append(roadie)
            roadset = set(roadnames)
            postset = set(postcodes)
            Roads = list(roadset)
            Postcodes = list(postset)
            newdf = df[(df['Road'].isin(Roads)) & (df['PostcodeNo'].isin(Postcodes))] 
            val.append({"RouteNo":i,"polyline": polylines[i],
            'routeLengthInMeters':routeLengths[i],
            'routeDurationInSeconds':routeDurations[i],
            'data':newdf.to_dict(orient='records'),
            'totalAccidents': str(newdf['AccidentCount'].sum())})
        finalVal = {'routes':val}
        return jsonify(finalVal)

class Faster_Data(Resource):
    def get(self,apnk,LatLongs):
        # Adding Geolocation
        gmaps = googlemaps.Client(key=apnk)
        val = []
        geolocation = GoogleV3(api_key=apnk)
        RoadInfo = json.loads(LatLongs)
        directions = gmaps.directions(RoadInfo[0],RoadInfo[1],mode="driving",alternatives=True)
        polylines = []
        routeLengths = []
        routeDurations = []
        minLats = []
        minLongs = []
        maxLats = []
        maxLongs = []
        for i in directions:
            totalLength = 0
            totalTime = 0
            for j in i["legs"]:
                totalLength += j["distance"]["value"]
                totalTime += j["duration"]["value"]
            routeLengths.append(totalLength)
            routeDurations.append(totalTime)
            polylines.append(i["overview_polyline"]["points"])
            minLats.append(i["bounds"]["northeast"]["lat"])
            minLongs.append(i["bounds"]["northeast"]["lng"])
            maxLats.append(i["bounds"]["southwest"]["lat"])
            maxLongs.append(i["bounds"]["southwest"]["lng"])
        minestLat = str(min(minLats))
        maxestLat = str(max(maxLats))
        minestLong = str(min(minLongs))
        maxestLong = str(max(maxLongs))
        cur = mysql.connection.cursor()
        df = pd.read_sql('select Longitude, Latitude, RadiusInKM, PostcodeNo, AccidentCount,concat(RoadName,\' \', RoadType) as Road from AccidentCluster where (Latitude between %(minestLat)s and %(maxestLat)s) and (Longitude between %(minestLong)s and %(maxestLong)s);',params={'minestLat':minestLat,'maxestLat':maxestLat,'minestLong':minestLong,'maxestLong':maxestLong} ,index_col=['Latitude','Longitude'],con=mysql.connection)

        for i in range(0,len(polylines)):
            list1 = PolylineCodec().decode(polylines[i])
            list1 = list1[0::10]
            coordinates = [str(i[0])+", "+ str(i[1]) for i in list1]
            postcodes = []
            roadnames = []
            for k in coordinates:
                # Perform reverse geolocation for each point received in the API call
                newVal = geolocation.reverse(k)
                # Splitting the response based on commas
                roadstuff= newVal[1].address.split(', ')
                # If we get road names along with post codes follows format [road, suburb, Australia]
                if len(roadstuff) == 3:
                    # Split road names based on spaces as roads may have format [number, roadname, roadtype]
                    roadie = roadstuff[0].split(' ')
                    if len(roadie) == 1:
                        roadie = roadie[0]
                    elif len(roadie) == 2: 
                        if roadie[0].isdigit():
                            roadie = roadie[1]
                        else:
                            roadie = roadie[0]+' '+roadie[1]
                    else:
                        roadie = roadie[1]+ ' '+roadie[2]
                    areaa = roadstuff[1][-4:]
                    postcodes.append(areaa)
                    roadnames.append(roadie)
            roadset = set(roadnames)
            postset = set(postcodes)
            Roads = list(roadset)
            Postcodes = list(postset)
            newdf = df[(df['Road'].isin(Roads)) & (df['PostcodeNo'].isin(Postcodes))] 
            val.append({"RouteNo":i,"polyline": polylines[i],
            'routeLengthInMeters':routeLengths[i],
            'routeDurationInSeconds':routeDurations[i],
            'data':newdf.to_dict(orient='records'),
            'minLat': minLats[i],
            'minLong':minLongs[i],
            'maxLat':maxLats[i],
            'maxLong':maxLongs[i],
            'totalAccidents': str(newdf['AccidentCount'].sum())})
        finalVal = {'routes':val}
        return jsonify(finalVal)

class Rest_Stops(Resource):
    def get(self):
        cur = mysql.connection.cursor()
        df = pd.read_sql('select * from RestStop;',con=mysql.connection)
        val = {'data':df.to_dict(orient='records')}
        return jsonify(val)


api.add_resource(Accident_Info, '/')
api.add_resource(Accident_Centroid, '/<apnk>/<datano>/<RoadData>')
api.add_resource(Accident_Data_All_LatLong, '/all/<apnk>/<AllRoadData>')
api.add_resource(All_Data, '/<apnk>/<LatLongs>')
api.add_resource(Faster_Data, '/diff/<apnk>/<LatLongs>')
api.add_resource(Rest_Stops, '/rest')


if __name__ == '__main__':
     application.run()
