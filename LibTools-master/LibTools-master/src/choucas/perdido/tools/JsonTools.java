/*
 * Copyright (C) 2018 Ludovic Moncla <ludovic.moncla@ecole-navale.fr>
 * 
 * This file is part of LibTools - Perdido project <http://erig.univ-pau.fr/PERDIDO/>
 *
 * LibTools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LibTools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LibTools. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package choucas.perdido.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.stream.JsonReader;

import choucas.perdido.elements.Toponyme;
import choucas.perdido.elements.Verb;

/**
 * FileTools class, provides some methods to create or modify files
 * @author Ludovic Moncla
 * @version 1.0
 */
public class JsonTools  {

	
	
	
	public static void main(String[] args)
	{
		
		List<Toponyme> toponyms = null;
		
		String jsonString = "[\n" + 
				"{\"gid\": \"6\",\"id\": \"1\",\"iid\": \"0\",\"idPath\": \"9\",\"title\": \"vanoise\",\"findName\": \"Vanoise, Dahu, Tignes, Albertville, Savoie, Rhône-Alpes, France métropolitaine, 73320, France\",\"country\": \"\",\"continent\": \"\",\"feature\": \"chair_lift\",\"featureText\": \"hôtel\",\"lat\": \"6.8910196\",\"lng\": \"45.4238029\",\"elevation\": \"0.0\",\"clc\": \"\",\"perception\": \"0\",\"negation\": \"0\",\"src\": \"OpenStreetMap\",\"nb\": \"1\",\"cluster\": \"4\",\"isBest\": \"true\",\"verbe\": \"suivre\",\"vlemma\": \"suivre\",\"polarite\": \"median\",\"localise\": \"1\",\"relSpatial\": \"\",\"relTemporal\": \"-1\",\"description\": \"Verbe : suivre(motion)\"},\n" + 
				"{\"gid\": \"13\",\"id\": \"2\",\"iid\": \"1\",\"idPath\": \"-1\",\"title\": \"petit mont blanc\",\"findName\": \"petit mont blanc\",\"country\": \"FR\",\"continent\": \"EU\",\"feature\": \"Rochers\",\"featureText\": \"\",\"lat\": \"6.686527331885441\",\"lng\": \"45.35596017996392\",\"elevation\": \"0.0\",\"clc\": \"\",\"perception\": \"0\",\"negation\": \"0\",\"src\": \"National Gazetteer\",\"nb\": \"1\",\"cluster\": \"4\",\"isBest\": \"true\",\"verbe\": \"suivre\",\"vlemma\": \"suivre\",\"polarite\": \"median\",\"localise\": \"1\",\"relSpatial\": \"\",\"relTemporal\": \"-1\",\"description\": \"Verbe : suivre(motion)\"},\n" + 
				"{}]";
		
	
		try {
			
			
			System.out.println("Read JSON file");
			toponyms = readJsonString(jsonString);
			
			Iterator<Toponyme> it = toponyms.iterator();
			while(it.hasNext())
			{
			    Toponyme toponyme = it.next();
			    System.out.println("toponym : "+toponyme.getName());
			    
			}
			
			
			
			JSONObject geojson = getGeoJson(toponyms);
			
			String res = geojson.toString(3);
			
			System.out.println("GeoJson result : ");
			System.out.println(res);
			
			
			res = getKML(toponyms);
			System.out.println("KML result : ");
			System.out.println(res);
			
			
			res = getGPX(toponyms);
			System.out.println("GPX result : ");
			System.out.println(res);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public List<Toponyme> readJsonStream(InputStream in) throws IOException
	{
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
		       return readToponymsArray(reader);
		     } finally {
		       reader.close();
		     }
	}
	
	
	
	public static List<Toponyme> readJsonString(String jsonString) throws IOException
	{	
		JsonReader reader = new JsonReader(new StringReader(jsonString));
		try {
		       return readToponymsArray(reader);
		     } finally {
		       reader.close();
		     }
	}
	
	
	protected static List<Toponyme> readToponymsArray(JsonReader reader) throws IOException
	{
		List<Toponyme> toponyms = new ArrayList<Toponyme>();

		
			
	     reader.beginArray();
	     while (reader.hasNext()) {
	    	 
	    	 	Toponyme t = readToponym(reader);
	    	 	if(t != null)
	    	 		toponyms.add(t);
	     }
	     reader.endArray();
	     return toponyms;
	}
	
	
	
	protected static Toponyme readToponym(JsonReader reader) throws IOException {
		String title = "",id="",idPath="", src="", polarite ="", verbe="", vlemma ="", findName= "",country="",continent="",feature="", featureText="", clcCode="",relSpatial = "";
		double lat=0,lng = 0,elevation = 0;
    		int cluster = 0, nb=1,localise = -1, perception = 0, negation = 0, relTemporal = -1;
    		

	     reader.beginObject();
  	   
	    	while (reader.hasNext()) {
	    	 
	    		String name = reader.nextName();
	    		if (name.equals("title")) {
				   title = reader.nextString();
				 } else if (name.equals("id")) {
					    id = reader.nextString();
				 } else if (name.equals("idPath")) {
					    idPath = reader.nextString();
				 } else if (name.equals("lat")) {
				        lng = reader.nextDouble();
				 } else if (name.equals("lng")) {
				    lat = reader.nextDouble();
				 } else if (name.equals("polarite")) {
				       polarite = reader.nextString(); 
				 } else if (name.equals("verbe")) {
					 	verbe = reader.nextString();
				 } else if (name.equals("vlemma")) {
					 	vlemma = reader.nextString();
				 } else if (name.equals("src")) {
				       src = reader.nextString();
				 } else if (name.equals("nb")) {
				       nb = reader.nextInt();
				 } else if (name.equals("perception")) {
				       perception = reader.nextInt();
				 } else if (name.equals("negation")) {
				       negation = reader.nextInt();
				 } else if (name.equals("findName")) {
				       findName = reader.nextString();
				 } else if (name.equals("country")) {
				       country = reader.nextString();
				 } else if (name.equals("continent")) {
				       continent = reader.nextString();
				 } else if (name.equals("feature")) {
				       feature = reader.nextString();
				 } else if (name.equals("featureText")) {
				       featureText = reader.nextString();
				 } else if (name.equals("localise")) {
				       localise = reader.nextInt();
				 } else if (name.equals("cluster")) {
				       cluster = reader.nextInt();
				 } else if (name.equals("elevation")) {
				       elevation = reader.nextDouble();
				 } else if (name.equals("clc")) {
					 clcCode = reader.nextString();
				 } else if (name.equals("relSpatial")) {
					 relSpatial = reader.nextString();
				 } else if (name.equals("relTemporal")) {
					 relTemporal = reader.nextInt();
				 }else{
				     reader.skipValue();
				 }
	    	   }
	    	   reader.endObject();


	    	   if(!id.isEmpty())
	    	   {
	    		   Verb verb = new Verb(verbe,"",polarite,vlemma);
		    	   Toponyme t = new Toponyme(Integer.parseInt(id),Integer.parseInt(idPath),findName,title,verb,lat,lng,src,nb,country,continent,feature,featureText,localise,cluster,false);
	    		   t.setPerception(perception);
	    		   t.setNegation(negation);
		    	 
	    		   t.setElevation(elevation);
	    		   t.setClcCode(clcCode);
		    	 
	    		   t.setRelSpatial(relSpatial);
	    		   t.setRelTemporal(relTemporal);
			   
	    		   return t;
	    	   }
	    	   
	    	   return null;
	    
	   }
	
	
	
	
	
	public static JSONObject getGeoJson(List<Toponyme> toponyms) throws JSONException
	{
		
	
		Iterator<Toponyme> it = toponyms.iterator();
	
		JSONObject geoJson = new JSONObject();
	
		
		
	    JSONArray featureArr = new JSONArray();
		
		while(it.hasNext())
		{
		    Toponyme toponyme = it.next();
		   
		    
		    System.out.println("toponym : "+toponyme.getName());
		    
		    
	        /* Format GeoJson
	         * 
	         * 
	         {
			  "type": "FeatureCollection",
			  "features": [
			    {
			      "type": "Feature",
			      "properties": {},
			      "geometry": {
			        "type": "Point",
			        "coordinates": [
			          6.6515350341796875,
			          45.36034780449632
			        ]
			      }
			    },
			    {
			      "type": "Feature",
			      "properties": {},
			      "geometry": {
			        "type": "Point",
			        "coordinates": [
			          6.6515350341796875,
			          45.36034780449632
			        ]
			      }
			    }
			  ]
			}
	         */
		    
		    JSONObject feature = new JSONObject();
		    
		    feature.put("type", "Feature");
		    
		    JSONObject geometryObj = new JSONObject();
		    geometryObj.put("type", "Point");
		    
		    JSONArray coordArr = new JSONArray();
		    coordArr.put(toponyme.getLng());
		    coordArr.put(toponyme.getLat());
		    
		    geometryObj.put("coordinates",coordArr);
		    
		    feature.put("geometry", geometryObj);
		    
		    
		    JSONObject propertiesObj = new JSONObject();
		    propertiesObj.put("name", toponyme.getName());
		    
		    /**
		     * Ajouter ici les autres propriétés qui sont stockées dans la classe toponyme
		     */
		    
		    feature.put("properties", propertiesObj);
		    
		    
		    featureArr.put(feature);
		}
		
		geoJson.put("features",featureArr);
		geoJson.put("type", "FeatureCollection");
		
		
		return geoJson;
	
	}
	
	
	public static String getKML(List<Toponyme> toponyms) 
	{
		
		String kml =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://www.opengis.net/kml/2.2\">";
	
		Iterator<Toponyme> it = toponyms.iterator();
	

		while(it.hasNext())
		{
		    Toponyme toponyme = it.next();
	
	/* Format KML
	  	<?xml version="1.0" encoding="UTF-8"?>
		<kml xmlns="http://www.opengis.net/kml/2.2">
		  <Placemark>
		    <name>Simple placemark</name>
		    <description>Attached to the ground. Intelligently places itself 
		       at the height of the underlying terrain.</description>
		    <Point>
		      <coordinates>-122.0822035425683,37.42228990140251,0</coordinates>
		    </Point>
		  </Placemark>
		</kml>
*/
		    kml += "<Placemark>";
		    kml += "<name>"+toponyme.getName()+"</name>";
		    kml += "<description>"+toponyme.getValue()+"</description>";
		    kml += "<Point><coordinates>"+toponyme.getLng()+","+toponyme.getLat()+"</coordinates></Point>";
		    kml += "</Placemark>";
		    
		    
		}
		
		
		kml += "</kml>";
		
		return kml;
	}
	
	
	
	
	public static String getGPX(List<Toponyme> toponyms) 
	{
		
		String gpx =  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"
				+ "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"byHand\" version=\"1.1\" \n" + 
				"		    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
				"		    xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
		
		Iterator<Toponyme> it = toponyms.iterator();
	

		while(it.hasNext())
		{
		    Toponyme toponyme = it.next();
	
	/* Format GPX
	  	<?xml version="1.0" encoding="UTF-8" standalone="no" ?>
		<gpx xmlns="http://www.topografix.com/GPX/1/1" creator="byHand" version="1.1" 
		    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		    xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd">
		 
		  <wpt lat="39.921055008" lon="3.054223107">
		    <ele>12.863281</ele>
		    <time>2005-05-16T11:49:06Z</time>
		    <name>Cala Sant Vicenç - Mallorca</name>
		    <sym>City</sym>
		  </wpt>
		</gpx>
*/
		    
		    gpx += "<wpt lat=\""+toponyme.getLat()+"\" lon=\""+toponyme.getLng()+"\">";
		    gpx += "<ele>0.0</ele>";
		    gpx += "<time>0000-00-00000:00:000</time>";
		    gpx += "<name>"+toponyme.getName()+"</name>";
		    gpx += "</wpt>";
		    
		}
		
		gpx += "</gpx>";
		
		return gpx;
	}
	
	
}
