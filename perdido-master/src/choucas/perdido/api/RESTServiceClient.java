package choucas.perdido.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.stream.JsonReader;

import choucas.perdido.elements.Toponyme;
import choucas.perdido.tools.JsonTools;


/**
 * RESTServiceClient class
 * @author Ludovic Moncla
 */
public class RESTServiceClient {
    public static void main(String[] args) {
        
        String response = "";
     
             try {
            	
               	 
            	 String url_base = "http://api.geohistoricaldata.org/geocoding";//?address=rue%20de%20Rivoli
            	 
            	 String request = "{\"address\":\"rue de Rivoli\"}";
            	 
            	 response  = callServiceREST(url_base,request);
            	 
	            //String url_base =  "http://erig.univ-pau.fr";
	            	//url_base =  "http://localhost:8080";
	           	
	     
	            	//String api_key = "b21g058SgE"; // YOUR API_KEY
	           
	            	
	          
                
	           // response = callJSON(url_base,api_key);
	            
	            
	          //  response = callGeocodage(url_base,api_key);
                
                
                System.out.println("\nREST Service Invoked Successfully: "+response);
                
              
                
            } catch (Exception e) {
                System.out.println("\nError while calling REST Service");
                e.printStackTrace();
            }
 
        
    }
    
    
    
    
    protected static String callGeocodage(String api_url, String api_key) throws JSONException, IOException
    {
  
    		api_url += "/PERDIDO/api/toponyms/json_gps/";
  
    		
    		String outputFormat = "GeoJson";
        	String getURL = "false";
    		String content = "	[\r\n" + 
    			"    	{\"gid\": \"0\",\"id\": \"0\",\"iid\": \"-1\",\"idPath\": \"-1\",\"title\": \"le refuge des Barmettes\",\"findName\": \"Refuge des Barmettes, Pont de la Glière, Pralognan-la-Vanoise, Albertville, Savoie, Auvergne-Rhône-Alpes, France métropolitaine, 73710, France\",\"country\": \"\",\"continent\": \"\",\"feature\": \"alpine_hut\",\"featureText\": \"\",\"lat\": \"6.7527105\",\"lng\": \"45.3895634\",\"elevation\": \"0.0\",\"clc\": \"\",\"perception\": \"0\",\"negation\": \"0\",\"src\": \"OpenStreetMap\",\"nb\": \"1\",\"cluster\": \"0\",\"isBest\": \"false\",\"verbe\": \"\",\"vlemma\": \"\",\"polarite\": \"\",\"localise\": \"-1\",\"relSpatial\": \"\",\"relTemporal\": \"-1\",\"description\": \"\"},\r\n" + 
    			"    	{}]";
    
    		
    		String request = "{\"api_key\":\""+api_key+"\",\"content\":"+content+",\"outputFormat\":\""+outputFormat+"\",\"getURL\":\""+getURL+"\"}";
    	    
    		
    	
    	
    		return callServiceREST(api_url, request);
    	
    	
    }
    
    
    
    protected static String callJSON(String api_url, String api_key) throws JSONException, IOException
    {
  
    		api_url += "/PERDIDO/api/toponyms/ner_json/";
 
        	String xmlString = "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\r\n" + 
        			"  <s>\r\n" + 
        			"    <w lemma=\"je\" type=\"PRO\">Je</w>\r\n" + 
        			"    <phr type=\"motion\">\r\n" + 
        			"      <w lemma=\"visiter\" type=\"V\" subtype=\"motion_median\">visite</w>\r\n" + 
        			"      <rs type=\"rel\" subtype=\"name\">\r\n" + 
        			"        <term type=\"N\">\r\n" + 
        			"          <w lemma=\"le\" type=\"DET\">les</w>\r\n" + 
        			"          <w lemma=\"village\" type=\"N\">villages</w>\r\n" + 
        			"        </term>\r\n" + 
        			"        <w lemma=\"au\" type=\"PREPDET\">au</w>\r\n" + 
        			"        <term type=\"offset\" subtype=\"orientation\">\r\n" + 
        			"          <w lemma=\"sud\" type=\"A\">sud</w>\r\n" + 
        			"          <w lemma=\"de\" type=\"PREP\">de</w>\r\n" + 
        			"        </term>\r\n" + 
        			"        <rs type=\"expandedName\">\r\n" + 
        			"          <term type=\"N\">\r\n" + 
        			"            <w lemma=\"le\" type=\"DET\">la</w>\r\n" + 
        			"            <w lemma=\"ville\" type=\"N\">ville</w>\r\n" + 
        			"          </term>\r\n" + 
        			"          <w lemma=\"de\" type=\"PREP\">de</w>\r\n" + 
        			"          <name>\r\n" + 
        			"            <w lemma=\"Pau\" type=\"NPr\">Pau</w>\r\n" + 
        			"          </name>\r\n" + 
        			"        </rs>\r\n" + 
        			"      </rs>\r\n" + 
        			"    </phr>\r\n" + 
        			"    <pc force=\"strong\">.</pc>\r\n" + 
        			"  </s>\r\n" + 
        			"</TEI>";
        	
        	String content = URLEncoder.encode(xmlString, "UTF-8");
    
    		
        	String request = "{\"api_key\":\""+api_key+"\",\"content\":\""+content+"\",\"lang\":\"French\",\"disambiguationLevel\":1}";
    	    
    		
    		
    	
    		return callServiceREST(api_url, request);
    	
    	
    }
    
    
    private static String callServiceREST(String api_url, String request) throws IOException, JSONException
    {
    		String response = "";
    		
    		JSONObject jsonObject = new JSONObject("{\"request\":"+request+"}");
    		
    		URL url = new URL(api_url);
         URLConnection connection = url.openConnection();
         connection.setDoOutput(true);
         connection.setRequestProperty("Content-Type","application/json");
   
         OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
         out.write(jsonObject.toString());
         out.close();

         System.out.println("debug");
         
         BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String line = null;
        
         System.out.println("debug");
         
         while ((line = in.readLine()) != null) {
         		response += line;
         }
         
         in.close();
         
         return response;
    }
    
}
