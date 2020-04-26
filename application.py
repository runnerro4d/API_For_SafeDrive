from flask import Flask, request
from flask_restful import Resource, Api
from flask_mysqldb import MySQL
from flask_jsonpify import jsonify
import pandas as pd
import os
import json
from geopy.geocoders import GoogleV3

application = Flask(__name__)

if 'RDS_HOSTNAME' in os.environ:
   application.config['MYSQL_HOST'] = os.environ['RDS_HOSTNAME']
   application.config['MYSQL_USER'] = os.environ['RDS_USERNAME']
   application.config['MYSQL_PASSWORD'] = os.environ['RDS_PASSWORD']
   application.config['MYSQL_DB'] = os.environ['RDS_DB_NAME']

mysql = MySQL(application)

api = Api(application)


#df2 = df.head(100)

class Accident_Info(Resource):
    def get(self):
        cur = mysql.connection.cursor()
        df = pd.read_sql('SELECT * from AccidentCluster limit 10', con=mysql.connection)
        val = {'data':df.to_dict(orient='records')}
        return jsonify(val)

class Accident_Centroid(Resource):
    def get(self,apnk,RoadData):
        geolocation = GoogleV3(api_key=apnk)
        RoadInfo = json.loads(RoadData)
        postcodes = []
        roadnames = []
        for i in RoadInfo:
            newVal = geolocation.reverse(i)
            roadstuff= newVal[1].address.split(', ')
            print(roadstuff)
            if len(roadstuff) == 3:
                roadie = roadstuff[0].split(' ')
                if len(roadie) == 1:
                    roadie = roadie
                elif len(roadie) == 2: 
                    if roadie[0].isdigit():
                        roadie = roadie[1]
                    else:
                        roadie = roadie[0]
                else:
                    roadie = roadie[1]
                areaa = roadstuff[1][-4:]
                postcodes.append(areaa)
                roadnames.append(roadie)
        roadset = set(roadnames)
        postset = set(postcodes)
        Roads = list(roadset)
        Postcodes = list(postset)

        print(Roads)
        print(Postcodes)
        cur = mysql.connection.cursor()
        df = pd.read_sql('SELECT * from AccidentCluster', con=mysql.connection)
        newdf = df[(df['RoadName'].isin(Roads)) & (df['PostcodeNo'].isin(Postcodes))] 
        newdf.head()
        val = {'data': newdf.to_dict(orient='records')}
        return jsonify(val)
api.add_resource(Accident_Info, '/')
api.add_resource(Accident_Centroid, '/<apnk>/<RoadData>')

if __name__ == '__main__':
     application.run()
