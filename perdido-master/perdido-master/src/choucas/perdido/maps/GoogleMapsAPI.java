package choucas.perdido.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import choucas.perdido.elements.Toponyme;

/**
 * GoogleMaps class : provide some method to use GoogleMaps API
 * @author Ludovic Moncla
 * @version 1.0
 */
public class GoogleMapsAPI {
	 
	
	
	//private static String KEY_API = "AIzaSyBf9DWovwqerg4zAfYIKdLnHaLVu9q7TQ4"; //moncla.ludovic
	//private static String KEY_API = "AIzaSyCLCe5BKgyhXhRsdBld_gtrplJEJZMUtro"; //ludal360
	
	//without parameters
	private static String _elevationAPI_URL = "https://maps.googleapis.com/maps/api/elevation/json?";
	
	
	
	
	/**
	 * 
	 * @param toponym
	 * @return elevation
	 * @throws IOException
	 * @throws JSONException
	 */
	public static double getElevation(Toponyme toponym, String googleAPIkey) throws IOException, JSONException
	{
		
		
		//System.out.println("Begin getElevation");
		//https://maps.googleapis.com/maps/api/elevation/json?locations=39.7391536,-104.9847034&key=API_KEY	
		String url = _elevationAPI_URL;
		url += "locations="+toponym.getLng()+","+toponym.getLat();
		url += "&key="+googleAPIkey;
		
		
		double elevation = 0;
		
		InputStream is = new URL(url).openStream();
		
		try {
			Thread.sleep(800);
			
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      
	      JSONObject json = new JSONObject(jsonText);
	      
	      
	      
  	    	
	      if(json.getString("status").equals("OK"))
	      {
  	    
	    	  JSONArray res = json.getJSONArray("results");
	    	  for (int i = 0; i < res.length(); ++i) {
	    		    JSONObject rec = res.getJSONObject(i);
	    		    elevation = rec.getDouble("elevation");
	    		    //System.out.println("elevation : " + elevation);
	    		   
	    		}
	      } 
	      else
	    	  System.out.println("status : " + json.getString("status"));
	      
	      
	      
	      
	      
	      
	      //elevation = json.json[0].elevation;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally {
	      is.close();
	    }
		
		//System.out.println("End getElevation");
		return elevation;
	}
	
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param samples
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static Vector<Toponyme> getPathElevation(Toponyme t1,Toponyme t2, int samples, String googleAPIkey) throws IOException, JSONException
	{
		
		//System.out.println("Begin getPathElevation");
		//https://maps.googleapis.com/maps/api/elevation/json?path=36.578581,-118.291994|36.23998,-116.83171&samples=3&key=API_KEY
		String url = _elevationAPI_URL;
		url += "path="+t1.getLng()+","+t1.getLat()+"|"+t2.getLng()+","+t2.getLat();
		url += "&samples="+samples;
		url += "&key="+googleAPIkey;
		
		
		Vector<Toponyme> samplesToponyms = new Vector<Toponyme>();
		
		InputStream is = new URL(url).openStream();
		
		try {
			
			Thread.sleep(500);
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      
	      JSONObject json = new JSONObject(jsonText);
	      
	      
	   //   System.out.println("KEY_API : " + googleAPIkey);
  	    	
	      if(json.getString("status").equals("OK"))
	      {
  	    
	    	  JSONArray res = json.getJSONArray("results");
	    	  for (int i = 0; i < res.length(); ++i) {
	    		    JSONObject rec = res.getJSONObject(i);
	    		    
	    		   // System.out.println("___ lat "+rec.getJSONObject("location").getDouble("lat"));
	    		   // System.out.println("___ lng "+rec.getJSONObject("location").getDouble("lng"));
	    		    		
	    		    
	    		    samplesToponyms.add(new Toponyme(rec.getJSONObject("location").getDouble("lng"), rec.getJSONObject("location").getDouble("lat"),rec.getDouble("elevation")));
	    		    //System.out.println("lat : " + rec.getJSONObject("location").getDouble("lat"));
	    		    //System.out.println("lng : " + rec.getJSONObject("location").getDouble("lng"));
	    		    //System.out.println("elevation : " + rec.getDouble("elevation"));
	    		   
	    		}
	      } 
	      else
	      { 
	    	  System.out.println("status : " + json.getString("status"));
	    	  return null;  
	      }
	      	
	      //elevation = json.json[0].elevation;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally {
	      is.close();
	    }
		
		//System.out.println("End getPathElevation");
		return samplesToponyms;
	}
	
	
	
	
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
	
}
