package choucas.perdido.api;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import choucas.perdido.parsing.ParsingPerdidoTEI;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.stream.JsonReader;

import choucas.perdido.elements.Toponyme;
import choucas.perdido.postgresql.User;
import choucas.perdido.tools.FileTools;
import choucas.perdido.tools.JsonTools;
import choucas.perdido.tools.StringTools;
import choucas.perdido.tools.XMLindent;
import choucas.perdido.tools.XmlTools;
import choucas.perdido.processing.Perdido;



/**
 * RESTService class
 * @author Ludovic Moncla
 * @version 1.0
 */
@Path("/")
public class RESTService{

	/**
	 * S1 & S2
	 */
	@GET  
    @Path("/pos/txt_unitex/") 
	@Produces("text/plain;charset=utf-8") 
	//@Produces("application/json")  
    public String POS_TXT_UNITEX(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key, @DefaultValue("treetagger") @QueryParam("POStagger") String POStagger) throws Exception{  
		
		
		System.out.println("api_key = "+api_key);
		System.out.println("content = "+content);
		System.out.println("lang = "+lang);
		System.out.println("postagger = "+POStagger);
		
		
		System.out.println("checkParam");
		
		
		String status = checkParam("pos", content, lang, api_key,POStagger,1);
		
		System.out.println("status = "+status);
		
		
		if(status.equals(""))	
		{
			 User user = new User();
			String username = user.getUserName(api_key);
			
			System.out.println("api_key = "+api_key+ " username = "+username);
			return getPOS(content, lang, username,POStagger);
		}
		else
			return XmlTools.getMsgXML("errors",status);
    }  
	

	@GET  
    @Path("/pos/txt_xml/") 
	@Produces("application/xml")
	//@Produces("application/json")  
    public String POS_TXT_XML(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key, @DefaultValue("treetagger") @QueryParam("POStagger") String POStagger) throws Exception{  
		
		String status = checkParam("pos", content, lang, api_key,POStagger,1);
		if(status.equals(""))	
		{
			User user = new User();
			String username = user.getUserName(api_key);
				
			System.out.println("api_key = "+api_key+ " username = "+username);
			return getPOS_TEI(content, lang, username,POStagger);
		}
		else
			return XmlTools.getMsgXML("errors",status);
    }  
	
	
	@POST
    @Path("/pos/txt_unitex/")
	@Produces("text/plain;charset=utf-8") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String POS_TXT_UNITEX(InputStream incomingData) {
       
		
		StringBuilder builder = new StringBuilder();
        String content = "",api_key = "",lang = "", POStagger="treetagger";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            
            JSONObject request = obj.getJSONObject("request");;
           
            
            content = request.getString("content");
            api_key = request.getString("api_key");
            lang = request.getString("lang");
            
            POStagger = request.getString("POStagger");
           
            String status = checkParam("pos",content, lang, api_key,POStagger,1);
    		if(status.equals(""))	
    		{
	            
	            User user = new User();
				String username = user.getUserName(api_key);
				
	            //String username = getUsername(api_key);
	            
	            return getPOS(content, lang, username,POStagger);
    		}
    		else
    			return XmlTools.getMsgXML("errors",status);
            
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
            return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("an error occured"));
        }
      //  System.out.println("Data Received: " + builder.toString());
 
 
    }
	
	@POST
    @Path("/pos/txt_xml/")
	@Produces("application/xml") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String POS_TXT_XML(InputStream incomingData) {
       
		
		StringBuilder builder = new StringBuilder();
        String content = "",api_key = "",lang = "", POStagger="";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            
            JSONObject request = obj.getJSONObject("request");;
           
            
            content = request.getString("content");
            api_key = request.getString("api_key");
            lang = request.getString("lang");
            
            POStagger = request.getString("POStagger");
           
            String status = checkParam("pos",content, lang, api_key,POStagger,1);
    		if(status.equals(""))	
    		{
	            
	            User user = new User();
				String username = user.getUserName(api_key);
				
	            //String username = getUsername(api_key);
	            
	            return getPOS_TEI(content, lang, username,POStagger);
    		}
    		else
    			return XmlTools.getMsgXML("errors",status);
            
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
            return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("an error occured"));
        }
      //  System.out.println("Data Received: " + builder.toString());
 
 
    }
	
	
	
	//-----------------------------------------------------------------------------------------------
	
	
	
	@GET  
    @Path("/ner/pos_xml/") 
	@Produces("application/xml")
	//@Produces("application/json")  
    public String NER_POS_XML(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key) throws Exception{  
		
		String status = checkParam("parsing", content, lang, api_key);
		if(status.equals(""))	
		{
			User user = new User();
			String username = user.getUserName(api_key);
				
			System.out.println("api_key = "+api_key+ " username = "+username);
			return getParsing_generic_unitex(content, lang, username);
		}
		else
			return XmlTools.getMsgXML("errors",status);
    }  
	
	
	//JMA 04/07/2017 DEBUT
		/****service web SW 4 execution de la cascade unitex, input = txt avec annotation morphosyntaxique au format unitex; OUTPUT= XML-TEI generic (non-catégorisé) */
		@POST
	//	@Path("/parsingTEI/generic/unitex/")
	    @Path("/ner/pos_xml/")
		@Produces("application/xml") 
	    @Consumes(MediaType.APPLICATION_JSON)
	    public String NER_POS_XML(InputStream incomingData) {
	       
	        
			StringBuilder builder = new StringBuilder();
	        String content = "",api_key = "",lang = "";
	        try {
	            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	            String line = null;
	            while ((line = in.readLine()) != null) {
	                builder.append(line);
	            }
	            
	            JSONObject obj = new JSONObject(builder.toString());
	            
	            
	            JSONObject request = obj.getJSONObject("request");;
	           
	            
	            content = request.getString("content");
	            api_key = request.getString("api_key");
	            lang = request.getString("lang");
	            
	           
	            //disambiguationLevel = request.getString("disambiguationLevel");
	            
	            
	            String status = checkParam("parsing", content, lang, api_key);
	    		if(status.equals(""))	
	    		{
	            
		            User user = new User();
					String username = user.getUserName(api_key);
		            
		           // String username = getUsername(api_key);
		           
		            return getParsing_generic_unitex(content, lang, username);
	    		}
	    		else
	    			return XmlTools.getMsgXML("errors",status);
	            
	        } catch (Exception e) {
	            System.out.println("Error Parsing: - ");
	            return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("an error occured"));
	        }
	      //  System.out.println("Data Received: " + builder.toString());

	    }
		
		//JMA 04/07/2017 FIN
	
		
		
		

		@GET  
	    @Path("/nerc/ner_xml/") 
		@Produces("application/xml")
		//@Produces("application/json")  
	    public String NERC_NER_XML(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key) throws Exception{  
			
			String status = checkParam("parsing", content, lang, api_key);
			if(status.equals(""))	
			{
				User user = new User();
				String username = user.getUserName(api_key);
				
				
				boolean IGN = true;
	            boolean GeoName = true;
	            boolean OSM = true;
					
				System.out.println("api_key = "+api_key+ " username = "+username);
				return getParsing_spatial_generic(content, lang, username, IGN, GeoName, OSM);
			}
			else
				return XmlTools.getMsgXML("errors",status);
	    }  
		
		
		
	
	//-------------------------------------------------------------------------------------------------------------------
	/**
	 * SW 7 : Interogation des gazetiers INPUT: XML-TEI generic (non-categorisé) OUTPU : xml-tei catégorisé 
	 * @param incomingData
	 * @return
	 */
	//JMA 20/07/2017 DEBUT
	@POST
	@Path("/nerc/ner_xml/")
	@Produces("application/xml") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String NERC_NER_XML(InputStream incomingData) {
       
		StringBuilder builder = new StringBuilder();
        String content = "",api_key = "",lang = "";
        Boolean IGN = false, GeoName = false, OSM = false;
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            JSONObject request = obj.getJSONObject("request");
           
            
            content = request.getString("content");
            api_key = request.getString("api_key");
            lang = request.getString("lang");
            
            IGN = true;
            GeoName = true;
            OSM = true;
            /*
            IGN = request.getBoolean("IGN");
            GeoName = request.getBoolean("GeoName");
            OSM = request.getBoolean("OSM");
            */
            
            String status = checkParam("parsing",content, lang, api_key);
	    		if(status.equals(""))	
	    		{
		            User user = new User();
					String username = user.getUserName(api_key);
		           
		            return getParsing_spatial_generic(content, lang, username, IGN, GeoName, OSM);
	    		}
	    		else
	    			return XmlTools.getMsgXML("errors",status);
	            
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
            return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("an error occured"));
        }
       
    }

	//JMA 20/07/2017 FIN
	//----------------------------------------------------------------------------------------------------------------
    /*
	@GET
	@Path("/")
	public Response responseMsg(@DefaultValue("none") @QueryParam("value") String value, @DefaultValue("none") @QueryParam("lang") String lang) {

		String output = "Value : " + value+ "lang : "+lang;
	
		return Response.status(200).entity(output).build();

	}
	*/
	
	
	/*
	
	@GET  
    @Path("/geoparsing/") 
	@Produces("application/xml") 
	//@Produces("application/json")  
    public String getGeoparsingXML(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key) throws Exception{  
		
		String status = checkParam(content, lang, api_key);
		if(status.equals(""))	
		{
			//String username = getUsername(api_key);
			 User user = new User();
			String username = user.getUserName(api_key);
			return getGeoparsing(content, lang, username);
		}
		else
			return status;
    }  
	
	
	
	@POST  
    @Path("/geoparsing/") 
	@Produces("application/xml") 
	@Consumes(MediaType.APPLICATION_JSON)  
    public String getGeoparsingXML(InputStream incomingData){  
		
		
		
		StringBuilder builder = new StringBuilder();
        String content = "",api_key = "",lang = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            
            JSONObject request = obj.getJSONObject("request");;
           
            
            content = request.getString("content");
            api_key = request.getString("api_key");
            lang = request.getString("lang");
            
            
            String status = checkParam(content, lang, api_key);
    		if(status.equals(""))	
    		{
	           // String username = getUsername(api_key);
	           
	            User user = new User();
				String username = user.getUserName(api_key);
	            
	            return getGeoparsing(content, lang, username);
   
	        }
			else
				return status;
            
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
        }
        return "";
    }  
    
    */
	
	
	/**** services web SW3*/
	
	@GET  
    @Path("/ner/txt_xml/") 
	@Produces("application/xml") 
	//@Produces("application/json")  
    public String NER_TXT_XML(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key, @DefaultValue("treetagger") @QueryParam("POStagger") String POStagger) throws Exception{  
		
		String status = checkParam("generic",content, lang, api_key, POStagger);
		if(status.equals(""))	
		{
			User user = new User();
			String username = user.getUserName(api_key);
			//String username = getUsername(api_key);
			return getParsing_generic(content, lang, username, POStagger);
		}
		else
			return XmlTools.getMsgXML("errors",status);
    }  
	
	/*****Anotation morphosyntaxique TreeTagger*/
	@POST
	@Path("/ner/txt_xml/")
	//@Path("/parsingTEI/generic/")
	@Produces("application/xml") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String NER_TXT_XML(InputStream incomingData) {
       
        
		StringBuilder builder = new StringBuilder();
        String content = "",api_key = "",lang = "", POStagger="treetagger";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            
            JSONObject request = obj.getJSONObject("request");;
           
            
            content = request.getString("content");
            api_key = request.getString("api_key");
            lang = request.getString("lang");
            
            POStagger = request.getString("POStagger");
            //disambiguationLevel = request.getString("disambiguationLevel");
            
            
            String status = checkParam("parsing", content, lang, api_key, POStagger);
    		if(status.equals(""))	
    		{
            
	            User user = new User();
				String username = user.getUserName(api_key);
	            
	           // String username = getUsername(api_key);
	           
	            return getParsing_generic(content, lang, username, POStagger);
    		}
    		else
    			return XmlTools.getMsgXML("errors",status);
            
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
            return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("an error occured"));
        }
      //  System.out.println("Data Received: " + builder.toString());

    }
	
	
	@GET  
    @Path("/nerc/txt_xml/") 
	@Produces("application/xml") 
	//@Produces("application/json")
	
    public String NERC_TXT_XML(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key, @DefaultValue("treetagger") @QueryParam("POStagger") String POStagger, @DefaultValue("true") @QueryParam("IGN") Boolean IGN, @DefaultValue("false") @QueryParam("GeoName") Boolean GeoName, @DefaultValue("false") @QueryParam("OSM") Boolean OSM) throws Exception{  
		
		String status = checkParam("spatial",content, lang, api_key, POStagger);
		if(status.equals(""))	
		{
			 User user = new User();
				String username = user.getUserName(api_key);
			//String username = getUsername(api_key);
			return getParsing_spatial(content, lang, username, POStagger, IGN, GeoName, OSM);
		}
		else
			return XmlTools.getMsgXML("errors",status);
    }  
	
	/**
	 * S5 <=> S2+S4+S7
	 * @param incomingData
	 * @return
	 */
	@POST
	@Path("/nerc/txt_xml/")
    //@Path("/parsingTEI/spatial/")
	@Produces("application/xml") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String NERC_TXT_XML(InputStream incomingData) {
       
        
		StringBuilder builder = new StringBuilder();
        String content = "",api_key = "",lang = "", POStagger = "treetagger";
        Boolean IGN = false, GeoName = false, OSM = false;
        //int disambiguationLevel = 1;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            
            JSONObject request = obj.getJSONObject("request");
           
            
            content = request.getString("content");
            api_key = request.getString("api_key");
            lang = request.getString("lang");
            
            IGN = request.getBoolean("IGN");
            GeoName = request.getBoolean("GeoName");
            OSM = request.getBoolean("OSM");
            
            POStagger = request.getString("POStagger");
           // disambiguationLevel = request.getInt("disambiguationLevel");
            //JMA 05/07/2017 Ajout "parsing" en parametre de checkParam
            String status = checkParam("parsing",content, lang, api_key,POStagger);
    		if(status.equals(""))	
    		{
            
	            User user = new User();
				String username = user.getUserName(api_key);
	            //String username = getUsername(api_key);
	           
	            return getParsing_spatial(content, lang, username, POStagger, IGN, GeoName, OSM);
    		}
    		else
    			return XmlTools.getMsgXML("errors",status);
            
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
            return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("an error occured"));
        }
      //  System.out.println("Data Received: " + builder.toString());
       
    }
	/**
	 * S6=s4+s7
	 * @param incomingData
	 * @return
	 */
	/*
	//JMA 05/07/2017 DEBUT
	@POST
	@Path("/nerc/pos_xml/")
	//@Path("/parsingTEI/spatial/unitex/")
	@Produces("application/xml") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String NERC_POS_XML(InputStream incomingData) {
       
        
		StringBuilder builder = new StringBuilder();
        String content = "",api_key = "",lang = "";
        Boolean IGN = false, GeoName = false, OSM = false;
        //int disambiguationLevel = 1;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            
            JSONObject request = obj.getJSONObject("request");
           
            
            content = request.getString("content");
            api_key = request.getString("api_key");
            lang = request.getString("lang");
            
            IGN = request.getBoolean("IGN");
            GeoName = request.getBoolean("GeoName");
            OSM = request.getBoolean("OSM");
                        
           
           // disambiguationLevel = request.getInt("disambiguationLevel");
            
            String status = checkParam("parsing",content, lang, api_key);
    		if(status.equals(""))	
    		{
            
	            User user = new User();
				String username = user.getUserName(api_key);
	           
	            return getParsing_spatial_unitex(content, lang, username, IGN, GeoName, OSM);
    		}
    		else
    			return XmlTools.getMsgXML("errors",status);
            
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
            return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("an error occured"));
        }
      //  System.out.println("Data Received: " + builder.toString());
       
    }

	//JMA 05/07/2017 FIN

	*/
	
	
	
	
	
	
	/*
	@GET  
    @Path("/getGeoparsing/json/") 
	@Produces("application/json")  
    public String getGeoparsingJSON(@DefaultValue("Je vais à Pau") @QueryParam("content") String content, @DefaultValue("French") @QueryParam("lang") String lang, @DefaultValue("WS") @QueryParam("api_key") String api_key){  
		return getGeoparsing(content, lang, api_key);
    }  
	*/
	
	
	/*
	@GET  
    @Path("/toponyms/txt_xml/") 
	@Produces("application/xml") 
	//@Produces("application/json")  
    public String TOPONYMS_TXT_XML(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("French") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key, 
    		@DefaultValue("treetagger") @QueryParam("POStagger") String POStagger, @DefaultValue("1") @QueryParam("disambiguationLevel") String disambiguationLevel, @DefaultValue("true") @QueryParam("IGN") Boolean IGN, @DefaultValue("false") @QueryParam("GeoName") Boolean GeoName, @DefaultValue("false") @QueryParam("OSM") Boolean OSM){  
		return getToponyms(content, lang, api_key,POStagger,Integer.parseInt(disambiguationLevel), IGN, GeoName, OSM);
    }  
	*/

	
	@GET  
    @Path("/toponyms/txt_json/") 
	@Produces("application/json")  
    public String TOPONYMS_TXT_JSON(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("French") @QueryParam("lang") String lang, @DefaultValue("") @QueryParam("api_key") String api_key, @DefaultValue("treetagger") @QueryParam("POStagger") String POStagger, @DefaultValue("1") @QueryParam("disambiguationLevel") String disambiguationLevel) throws JSONException{  
		
		return getToponyms(content, lang, api_key,POStagger,Integer.parseInt(disambiguationLevel));
    }  
	
	
	
	/**
	 * S9
	 * @param incomingData
	 * @return
	 */
	/*
	@POST
    @Path("/toponyms/txt_xml/")
	@Produces("application/xml") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String TOPONYMS_TXT_XML(InputStream incomingData) {
       
        return getToponymsPOST(incomingData);
    }
    */
	/**
	 * S8
	 * @param incomingData
	 * @return
	 * @throws JSONException
	 */
	@POST
    @Path("/toponyms/txt_json/")
	@Produces("application/json") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String TOPONYMS_TXT_JSON(InputStream incomingData) throws JSONException {
       
        return getToponymsPOST(incomingData);
    }
	/**
	 * S11
	 * @param incomingData
	 * @return
	 */
	//JMA 06/07/2017 DEBUT traitement données en entrée au format XML issues de getParsing_generic
	/*
	@POST
    @Path("/toponyms/ner_xml/")
	@Produces("application/xml") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String TOPONYMS_NER_XML(InputStream incomingData) {
       
        return getToponymsPOST_generic(incomingData);
    }
    */
	/**
	 * S10
	 * @param incomingData
	 * @return
	 * @throws JSONException
	 */
	@POST
    @Path("/toponyms/ner_json/")
	@Produces("application/json") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String TOPONYMS_NER_JSON(InputStream incomingData) throws JSONException {
       
        return getToponymsPOST_generic(incomingData);
    }
	//JMA 06/07/2017 FIN
	
	

	
	/**
	 * S12
	 * @param incomingData
	 * @return
	 */
	@POST
    @Path("/toponyms/json_gps/")
	//@Produces("application/json") 
	//@Produces("text/plain;charset=utf-8") 
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN}) 
    @Consumes(MediaType.APPLICATION_JSON)
    public String TOPONYMS_JSON_STANDARD(InputStream incomingData) {
        return getToponymsPOST_standard(incomingData);
    }
	
	@GET  
    @Path("/toponyms/json_gps/") 
	@Produces("application/json") 
	//@Produces("text/plain;charset=utf-8") 
    public String TOPONYMS_JSON_STANDARD(@DefaultValue("") @QueryParam("content") String content, @DefaultValue("") @QueryParam("api_key") String api_key, @DefaultValue("GeoJson") @QueryParam("outputFormat") String outputFormat,  @DefaultValue("true") @QueryParam("getURL") String getURL) throws JSONException{  
		
		return getToponyms_standard(content, outputFormat, api_key,getURL);
		//return getToponyms(content, lang, api_key,POStagger,Integer.parseInt(disambiguationLevel), IGN, GeoName, OSM);
    }  
	
	
	private String getToponymsPOST(InputStream incomingData) {
        StringBuilder builder = new StringBuilder();
        String content = "", api_key = "",lang = "", POStagger ="treetagger";
        Boolean IGN = false, GeoName = false, OSM = false;
        int disambiguationLevel = 1;
        
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            
            JSONObject request = obj.getJSONObject("request");
           
            
            content = request.getString("content");
            api_key = request.getString("api_key");
            lang = request.getString("lang");
           
            
            POStagger = request.getString("POStagger");
            disambiguationLevel = request.getInt("disambiguationLevel");
           
            
           
           
	            //String username = getUsername(api_key);
	            
			return getToponyms(content, lang, api_key,POStagger,disambiguationLevel);
    	
    		//else
    		//	return status;
           
    		
    		
            
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
        }
      //  System.out.println("Data Received: " + builder.toString());
 
        
        return null;
        // return HTTP response 200 in case of success
        //return Response.status(200).entity(builder.toString()).build();
        //return "api_key: "+api_key+" Lang: "+lang+" Content: "+content;
    }
	
	//JMA 06/07/2017 DEBUT traitement données en entrée au format XML issues de getParsing_generic
	private String getToponymsPOST_generic(InputStream incomingData) {
        StringBuilder builder = new StringBuilder();
        String content = "", api_key = "",lang = "";
        Boolean IGN = false, GeoName = false, OSM = false;
        int disambiguationLevel = 1;
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString()); 
            JSONObject request = obj.getJSONObject("request");;
           
           
            content = URLDecoder.decode(request.getString("content"), "UTF-8");
            
            api_key = request.getString("api_key");
            lang = request.getString("lang");
            
            IGN = true;//request.getBoolean("IGN");
            GeoName = true;//request.getBoolean("GeoName");
            OSM = true;//request.getBoolean("OSM");
            
            
            disambiguationLevel = request.getInt("disambiguationLevel");
            
			return getToponyms_generic(content, lang, api_key,disambiguationLevel, IGN, GeoName, OSM);
        } catch (Exception e) {System.out.println("Error Parsing: - ");}
    
        return null;
        // return HTTP response 200 in case of success
        //return Response.status(200).entity(builder.toString()).build();
        //return "api_key: "+api_key+" Lang: "+lang+" Content: "+content;
    }

	//JMA 06/07/2017 FIN
	
	
	
	private String getToponymsPOST_standard(InputStream incomingData) {
        StringBuilder builder = new StringBuilder();
       
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            JSONObject request = obj.getJSONObject("request");
       
            //String content = request.getString("content");
            String content = request.getJSONArray("content").toString();
            
            
            String api_key = request.getString("api_key");
            String outputFormat = request.getString("outputFormat");
            String getURL = request.getString("getURL");
            
                 
			return getToponyms_standard(content, outputFormat, api_key,getURL);
			
        } catch (Exception e) {System.out.println("An error occured");}
    
        
        return null;
        // return HTTP response 200 in case of success
        //return Response.status(200).entity(builder.toString()).build();
        //return "api_key: "+api_key+" Lang: "+lang+" Content: "+content;
    }

	
	 private String getToponyms_standard(String content, String outputFormat, String api_key, String getURL)
	 {
		String result = "";
		
		String status = checkParam("toponymsStandard",content,outputFormat,api_key);
    		if(status.equals(""))	
    		{
            
    			
    			boolean returnURL = Boolean.valueOf(getURL).booleanValue() ; 
    			
    			
			
			try {		
				
				
				
				//on parse le json contenant les toponymes
				List<Toponyme> toponyms = JsonTools.readJsonString(content);
				
				//on les transforme au format demandé
				if(outputFormat.equals("GeoJson"))
				{
					result = JsonTools.getGeoJson(toponyms).toString(3);
				}
				else if(outputFormat.equals("KML"))
				{
					result = JsonTools.getKML(toponyms);
				}
				else if(outputFormat.equals("GPX"))
				{
					result = JsonTools.getGPX(toponyms);
				}
				
				
				
				if(returnURL)
				{
					//on génère le nom du fichier dans le cas où on doit retourner l'url
				
					
					User user = new User();
					String username = user.getUserName(api_key);
				
					Perdido perdido = new Perdido();
					perdido.loadParams();
					perdido.user(username);
				
					String name = StringTools.generate(6);
					String outputDir = perdido.outputDirWebServices() + perdido.user();
					
					System.out.println(outputDir);
					FileTools.createDir(outputDir);	
					
					String filename = outputDir + "/"+name;
					String URL = "http://erig.univ-pau.fr/PERDIDO/downloads/webservices/"+username+"/"+name;
					
					
					if(outputFormat.equals("GeoJson"))
					{
						filename += ".json";
						URL += ".json";
					}
					else if(outputFormat.equals("KML"))
					{
						filename += ".kml";
						URL += ".kml";
					}
					else if(outputFormat.equals("GPX"))
					{
						filename += ".gpx";
						URL += ".gpx";
					}
					
					FileTools.createFile(filename, result);
					
					
					
					//String fileName = "/Users/lmoncla/Programme/Unitex3.1beta/webService/output/" +"/"+name;
					
					
					result = URL;
				}
				
			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		}
    		else
    		{
    			
    			try {
    					System.out.println(XML.toJSONObject(XmlTools.getMsgXML("errors",status)).toString(4));
					return XmlTools.getMsgXML("errors",status);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
		return result;			
	 }
	
	
	/**
	 * 
	 * @param content
	 * @param lang
	 * @param api_key
	 * @return
	 */
	 private String getToponyms(String content, String lang, String api_key, String POStagger, int disambiguationLevel)
	 {
		 	//test les parametres
			//List<Toponyme> topos = new ArrayList<Toponyme>();
			List<Toponyme> toponyms = null;
			
			boolean doClustering = false;
			boolean removeDuplicatePoints = true;
			
			String result = "";
			
			String status = checkParam("toponyms",content, lang, api_key,POStagger,disambiguationLevel);
    		if(status.equals(""))	
    		{
	            
	            User user = new User();
				String username;
				try {
					username = user.getUserName(api_key);
				
					//lancement du launchProcess
					Perdido perdido = new Perdido();
					perdido.loadParams();
					
					perdido.user(username);
					perdido.lang(lang);
				
					int bestCluster = -1;
					
					if(disambiguationLevel == 0)
					{
						removeDuplicatePoints =  false;
						doClustering = false;
					}
					if(disambiguationLevel == 1)
					{
						removeDuplicatePoints =  true;
						doClustering = false;
					}
					if(disambiguationLevel == 2)
					{
						removeDuplicatePoints = true;
						doClustering = true;
					}
					
					//pr.setParam(true, true, true, removeDuplicatePoints, doClustering,POStagger);
					
					perdido.doAnnotation(true);
					perdido.doPOS(true);
					perdido.doToponymsResolution(true);
					perdido.doToponymsDisambiguation(removeDuplicatePoints);
					perdido.doClustering(doClustering);
					perdido.analyserPOS(POStagger);
					
					perdido.nationalGazetteer(true);
					perdido.geonames(true);
					perdido.osm(true);
					
					String name = StringTools.generate(6);
					
					
					
					perdido.launchProcess(content,perdido.outputDir() + perdido.user() + "/" +name,name,"");
					//toposTmp = FileTools.loadJson(pr.outputDir()+username+"/"+name+"/"+name+".json");
					
					//toponyms = ParsingPerdidoTEI.loadToponymsFromJson(perdido.outputDir()+username+"/"+name+"/"+name+".json");
					
					//JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(perdido.outputDir()+username+"/"+name+"/"+name+".json")));
					//result = reader.toString();
					
					result = FileTools.getContent(perdido.outputDir()+username+"/"+name+"/"+name+".json");
					
					
					System.err.println(result);
					/*
					result ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
					result += "<toponyms>";
					result += "<nbResults>"+toponyms.size()+"</nbResults>";
					
					
					
					
					for(int i=0; i< toponyms.size();i++)
					{
						result += toponyms.get(i).getXML();
					}
					result += "</toponyms>";
					*/
					//suppression des fichiers et de la bdd
					//FileTools.delete(new File (pr.getOutputDir()+user+"/"+name+"/"));
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		else
    		{
    		//	JSONObject xmlJSONObj = XML.toJSONObject(status);
    			
    			try {
    				
    				System.out.println(XML.toJSONObject(XmlTools.getMsgXML("errors",status)).toString(4));
					return XmlTools.getMsgXML("errors",status);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}

    		
    		
			return result;			
	 }
	 

	 //JMA 06/07/2017 DEBUT
		/**
		 * 
		 * @param content
		 * @param lang
		 * @param api_key
		 * @return
		 */
		 private String getToponyms_generic(String content, String lang, String api_key, int disambiguationLevel, Boolean IGN, Boolean GeoName, Boolean OSM)
		 {
			 	//test les parametres
				//List<Toponyme> topos = new ArrayList<Toponyme>();
				List<Toponyme> toponyms = null;
				boolean doClustering = false;
				boolean removeDuplicatePoints = true;
				
				String result = "";
				
				String status = checkParam("toponyms",content, lang, api_key,disambiguationLevel);
	    		if(status.equals(""))	
	    		{
		            
		            User user = new User();
					String username;
					try {
						username = user.getUserName(api_key);
					
						//lancement du launchProcess
						Perdido perdido = new Perdido();
						perdido.loadParams();
						
						perdido.user(username);
						perdido.lang(lang);
						
						if(disambiguationLevel == 0)
						{
							removeDuplicatePoints =  false;
							doClustering = false;
						}
						if(disambiguationLevel == 1)
						{
							removeDuplicatePoints =  true;
							doClustering = false;
						}
						if(disambiguationLevel == 2)
						{
							removeDuplicatePoints = true;
							doClustering = true;
						}
						
						perdido.doAnnotation(false);
						perdido.doPOS(false);
						perdido.doToponymsResolution(true);
						perdido.doToponymsDisambiguation(removeDuplicatePoints);
						perdido.doClustering(doClustering);
						
						perdido.nationalGazetteer(IGN);
						perdido.geonames(GeoName);
						perdido.osm(OSM);
						
						String name = StringTools.generate(6);
						
						String outputDir = perdido.outputDir() + perdido.user();
						
						System.out.println(outputDir);
						FileTools.createDir(outputDir);	
						
						outputDir = perdido.outputDir() + perdido.user() + "/"+name;
						FileTools.createDir(outputDir);	
						
						String fileName = outputDir +"/"+name;
						
						//content = StringTools.filtreString(content);
						
						FileTools.createFile(fileName+".xml", content);
						
						perdido.launchProcess(content,perdido.outputDir() + perdido.user() + "/" +name,name,"");
						//toposTmp = FileTools.loadJson(pr.outputDir()+username+"/"+name+"/"+name+".json");
						/*
						toponyms = ParsingPerdidoTEI.loadToponymsFromJson(perdido.outputDir()+username+"/"+name+"/"+name+".json");
						
						result ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
						result += "<toponyms>";
						result += "<nbResults>"+toponyms.size()+"</nbResults>";
						
						
						
						
						for(int i=0; i< toponyms.size();i++)
						{
							result += toponyms.get(i).getXML();
						}
						result += "</toponyms>";
						*/
						result = FileTools.getContent(perdido.outputDir()+username+"/"+name+"/"+name+".json");
						
						//JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(perdido.outputDir()+username+"/"+name+"/"+name+".json")));
						
						//result = reader.toString();
						
						
						//suppression des fichiers et de la bdd
						//FileTools.delete(new File (pr.getOutputDir()+user+"/"+name+"/"));
							
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    		else
	    		{
	    		//	JSONObject xmlJSONObj = XML.toJSONObject(status);
	    			
	    			try {
	    				
	    				System.out.println(XML.toJSONObject(XmlTools.getMsgXML("errors",status)).toString(4));
						return XML.toJSONObject(XmlTools.getMsgXML("errors",status)).toString(4);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}

	    		
	    		
				return result;			
		 }

	 //JMA 06/07/2017 FIN
	 
	 
	 /**
		 * TEI
		 * @param content
		 * @param lang
		 * @param username
		 * @return
		 */
		 private String getParsing_generic(String content, String lang, String username, String POStagger)
		 {
			 	//test les parametres
				//List<Toponyme> topos = new ArrayList<Toponyme>();
			//	List<Toponyme> toposTmp = null;
				String result = "";
				String name = StringTools.generate(6);
				
				try {
									
					//lancement du launchProcess
					Perdido pr = new Perdido();
					pr.loadParams();
					
					pr.user(username);
					pr.lang(lang);
				
					pr.doAnnotation(true);
					pr.doPOS(true);
					pr.doToponymsResolution(false);
					pr.doToponymsDisambiguation(false);
					pr.doClustering(false);
					pr.analyserPOS(POStagger);
					
					String outputDir = pr.outputDir() + pr.user();
					FileTools.createDir(outputDir);	

					outputDir = pr.outputDir() + pr.user() + "/"+name;
					
					String fileName = outputDir +"/"+name;
					String inputCascade = fileName;
					
					pr.launchAnnotation(content, outputDir,name,fileName,inputCascade);	
					
					//pr.launchUnitex(content, outputDir,name,fileName,inputCascade);
					
					//result += "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">" +
					//		"<teiHeader><fileDesc><titleStmt><title><!-- title of the resource --></title></titleStmt><publicationStmt><p><!-- Information about distribution of the resource --></p></publicationStmt><sourceDesc><p><!-- Information about source from which the resource derives --></p></sourceDesc></fileDesc></teiHeader>" +
					//		"<text><body><p><s>";
					
					XMLindent.indentXMLFile(fileName+".xml");
					
					result = FileTools.getContent(pr.outputDir()+username+"/"+name+"/"+name+".xml");
					//suppression des fichiers et de la bdd
					//FileTools.delete(new File (pr.getOutputDir()+user+"/"+name+"/"));
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getStackTrace().toString();
				}

				return result;			
		 }
		 //JMA 04/07/2017 DEBUT
		 /**
			 * TEI
			 * @param content
			 * @param lang
			 * @param username
			 * @return
			 */
			 private String getParsing_generic_unitex(String content, String lang, String username)
			 {
				 	//test les parametres
					//List<Toponyme> topos = new ArrayList<Toponyme>();
				//	List<Toponyme> toposTmp = null;
					String result = "";
					String name = StringTools.generate(6);
					
					try {
										
						//lancement du launchProcess
						Perdido pr = new Perdido();
						pr.loadParams();
						
						pr.user(username);
						pr.lang(lang);
					
						pr.doAnnotation(true);
						pr.doPOS(false);
						pr.doToponymsResolution(false);
						pr.doToponymsDisambiguation(false);
						pr.doClustering(false);
						

						String outputDir = pr.outputDir() + pr.user();
						FileTools.createDir(outputDir);	

						outputDir = pr.outputDir() + pr.user() + "/"+name;
						
						String fileName = outputDir +"/"+name;
						String inputCascade = fileName;
						
						//pr.launchAnnotation(content, outputDir,name,fileName,inputCascade);	
						
						pr.launchUnitex(content, outputDir,name,fileName,inputCascade);
						
						XMLindent.indentXMLFile(fileName+".xml");
						
						result = FileTools.getContent(pr.outputDir()+username+"/"+name+"/"+name+".xml");
						//suppression des fichiers et de la bdd
						//FileTools.delete(new File (pr.getOutputDir()+user+"/"+name+"/"));
							
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return e.getStackTrace().toString();
					}

					return result;			
			 }
		 //JMA 04/07/2017 FIN
		 
		 
		 /**
			 * TEI
			 * @param content
			 * @param lang
			 * @param username
			 * @return
			 */
			 private String getParsing_spatial(String content, String lang, String username, String POStagger, Boolean IGN, Boolean GeoName, Boolean OSM)
			 {
				 	//test les parametres
					//List<Toponyme> topos = new ArrayList<Toponyme>();
				//	List<Toponyme> toposTmp = null;
				 
				boolean removeDuplicatePoints = true;
				boolean doClustering = false;
			 
				String result = "";
				String name = StringTools.generate(6);
				int bestCluster = -1;
				try {
				
					//lancement du launchProcess
					Perdido pr = new Perdido();
					pr.loadParams();
					
					pr.user(username);
					pr.lang(lang);
				
					pr.doAnnotation(true);
					pr.doPOS(true);
					pr.doToponymsResolution(true);
					pr.doToponymsDisambiguation(false);
					pr.doClustering(false);
					pr.analyserPOS(POStagger);
					
				    pr.nationalGazetteer(IGN);
				    pr.geonames(GeoName);
				    pr.osm(OSM);
				
					//pr.setParam(true, true, true, false, false, POStagger);
					//pr.setParam(true, true, true, false, disamb);
					
					
					//name = pr.tei_tmp(content);
					System.out.println("outputDir : "+pr.outputDir());
					
					String outputDir = pr.outputDir() + pr.user();
					FileTools.createDir(outputDir);	

					
					pr.launchProcess(content,  pr.outputDir() + pr.user() + "/" +name, name,"");
					
						
						
						
					result = FileTools.getContent(pr.outputDir()+username+"/"+name+"/"+name+".xml");
					
					//suppression des fichiers et de la bdd
					//FileTools.delete(new File (pr.getOutputDir()+user+"/"+name+"/"));
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getStackTrace().toString();
				}

				return result;			
			 }

		//JMA 05/07/2017 DEBUT
		/**
		 * TEI
		 * @param content
		 * @param lang
		 * @param username
		 * @return
		 */
		 private String getParsing_spatial_unitex(String content, String lang, String username,  Boolean IGN, Boolean GeoName, Boolean OSM)
		 {
					String result = "";
					String name = StringTools.generate(6);
					int bestCluster = -1;
					try {
					
						//lancement du launchProcess
						Perdido pr = new Perdido();
						pr.loadParams();
						
						pr.user(username);
						pr.lang(lang);
					
						pr.doAnnotation(true);
						pr.doPOS(true);
						pr.doToponymsResolution(true);
						pr.doToponymsDisambiguation(false);
						pr.doClustering(false);
						
						
						pr.nationalGazetteer(IGN);
						pr.geonames(GeoName);
						pr.osm(OSM);
						
						String outputDir = pr.outputDir() + pr.user();
						FileTools.createDir(outputDir);	

						outputDir = pr.outputDir() + pr.user() + "/"+name;
						
						String fileName = outputDir +"/"+name;
						String inputCascade = fileName;
						//s4
						pr.launchUnitex(content, outputDir,name,fileName,inputCascade);
						//s7
						pr.launchToponymsResolution(pr.outputDir() + pr.user() + "/" +name, name);
							
						result = FileTools.getContent(pr.outputDir()+username+"/"+name+"/"+name+".xml");
						
						//suppression des fichiers et de la bdd
						//FileTools.delete(new File (pr.getOutputDir()+user+"/"+name+"/"));
							
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return e.getStackTrace().toString();
					}

					return result;			
				 }

			 //JMA 05/07/2017 FIN
			//JMA 20/07/2017 DEBUT
			/**
			 * TEI
			 * @param content
			 * @param lang
			 * @param username
			 * @return
			 */
			 private String getParsing_spatial_generic(String content, String lang, String username,  Boolean IGN, Boolean GeoName, Boolean OSM)
			 {
						String result = "";
						String name = StringTools.generate(6);
						int bestCluster = -1;
						try {
						
							//lancement du launchProcess
							Perdido pr = new Perdido();
							pr.loadParams();
							
							pr.user(username);
							pr.lang(lang);
						/*
							pr.doAnnotation(true);
							pr.doPOS(true);
							pr.doToponymsResolution(true);
							pr.doToponymsDisambiguation(false);
							pr.doClustering(false);
							pr.analyserPOS(POStagger);
							*/
							pr.nationalGazetteer(IGN);
							pr.geonames(GeoName);
							pr.osm(OSM);
							
							String outputDir = pr.outputDir() + pr.user();
							FileTools.createDir(outputDir);	

							outputDir = pr.outputDir() + pr.user() + "/"+name;
							FileTools.createDir(outputDir);
							
							String fileName = outputDir +"/"+name;
							
							
							
							FileTools.createFile(fileName+".xml", content);
							//String inputCascade = fileName;
							//pr.launchUnitex(content, outputDir,name,fileName,inputCascade);

							pr.launchToponymsResolution(pr.outputDir() + pr.user() + "/" +name, name);
								
							result = FileTools.getContent(pr.outputDir()+username+"/"+name+"/"+name+".xml");
							
							//suppression des fichiers et de la bdd
							//FileTools.delete(new File (pr.getOutputDir()+user+"/"+name+"/"));
								
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return e.getStackTrace().toString();
						}

						return result;			
					 }

				 //JMA 05/07/2017 FIN
		 
		 
 /**
	 * 
	 * @param content
	 * @param lang
	 * @param username
	 * @return
	 */
	 private String getPOS(String content, String lang, String username, String analyserPOS)
	 {

		String st = "";
		String name = StringTools.generate(6);
		try {
		
			//JMA 06/07/2017 DEBUT MISE EN COMMENTAIRE ANCIEN TRAITEMENT
			/*
			Perdido pr = new Perdido();
			pr.loadParams();
			pr.user(username);
			pr.lang(lang);
			
			pr.outputDir(pr.outputDir()+username+"/");
			pr.analyserPOS(analyserPOS);
			
			
			FileTools.createDir(pr.outputDir());
			
			
			String name = StringTools.generate(6);
			FileTools.createDir(pr.outputDir()+name);
			
			content = StringTools.filtreString(content);	
			
			String fileName = pr.outputDir()+name+"/"+name;
			FileTools.createFile(fileName+".txt", content);
			
		
			
			POStagger pos = null;
			
			if(analyserPOS.equals("treetagger"))
			{
				pos = new Treetagger(pr.uriTreeTagger(),pr.lang());
				pos.run(content, fileName+"_"+pr.analyserPOS()+".txt");
			}
			
			if(analyserPOS.equals("talismane"))
			{
				//JMA 23/06/2017 ajout fichier conf pour version talismane 4.1.0
				if (pr.uriTalismane().contains("4.1.0")){
					pos = new Talismane(pr.uriTalismane(),pr.lang(),pr.uriTalismaneLang(),pr.uriTalismaneConf());
				}
				else {
					pos = new Talismane(pr.uriTalismane(),pr.lang(),pr.uriTalismaneLang());
				}
				pos.run(fileName+".txt", fileName+"_"+pr.analyserPOS()+".txt");	
			}
			*/
			//JMA 06/07/2017 FIN MISE EN COMMENTAIRE ANCIEN TRAITEMENT
			
			//A supprimer car stanfordNLP non installé
			/*if(analyserPOS.equals("stanfordNLP"))
			{
				pos = new StanfordNLP(pr.uriStanfordNLP(pr.lang()),pr.lang());
				pos.run(fileName+".txt", fileName+"_"+pr.analyserPOS()+".txt");	
			}*/
			
			//Fait partie de l'ancien traitement
			//String posPivot = pos.tagger2unitex(pos.tagger2pivot(fileName+"_"+pr.analyserPOS()+".txt"));
			
			//JMA 06/07/2017 DEBUT NOUVEAU TRAITEMENT
			Perdido pr = new Perdido();
			pr.loadParams();
			
			pr.user(username);
			pr.lang(lang);
		
			pr.doAnnotation(true);
			pr.doPOS(true);
			pr.doUnitex(false);
			//pr.doToponymsResolution(false);
			//pr.doToponymsDisambiguation(false);
			//pr.doClustering(false);
			pr.analyserPOS(analyserPOS);
			
			String outputDir = pr.outputDir() + pr.user();
			FileTools.createDir(outputDir);	
			
			outputDir = pr.outputDir() + pr.user() + "/"+name;
			
			String fileName = outputDir +"/"+name;
			String inputCascade = fileName;
							
			String posPivot = pr.launchAnnotation(content, outputDir,name,fileName,inputCascade);	

			//JMA 06/07/2017 FIN NOUVEAU TRAITEMENT			
			FileTools.createFile(fileName+"_output.txt", posPivot);
			 
			 
			 st = FileTools.getContent(fileName+"_output.txt");
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getStackTrace().toString();
		}
		
		String str2 = new String(st.getBytes(),Charset.forName("UTF-8"));
		return str2;			
	 }
	
	
	 /**
		 * 
		 * @param content
		 * @param lang
		 * @param username
		 * @return
		 */
		 private String getPOS_TEI(String content, String lang, String username, String analyserPOS)
		 {
			 
			 
			 String contentTEI = "";
			 String name = StringTools.generate(6);
			 
			 try {
					
					//lancement du launchProcess
				 //JMA 06/07/2017 DEBUT MISE EN COMMENTAIRE ANCIEN TRAITEMENT
				 /*
					Perdido pr = new Perdido();
					pr.loadParams();
					pr.user(username);
					pr.lang(lang);
					
					pr.outputDir(pr.outputDir()+username+"/");
					pr.analyserPOS(analyserPOS);
					
					
					FileTools.createDir(pr.outputDir());
					
					
					String name = StringTools.generate(6);
					FileTools.createDir(pr.outputDir()+name);
					
					content = StringTools.filtreString(content);	
					
					String fileName = pr.outputDir()+name+"/"+name;
					FileTools.createFile(fileName+".txt", content);
										
					POStagger pos = null;
					
					if(analyserPOS.equals("treetagger"))
					{
						pos = new Treetagger(pr.uriTreeTagger(),pr.lang());
						pos.run(content, fileName+"_"+pr.analyserPOS()+".txt");
					}
					
					if(analyserPOS.equals("talismane"))
					{
						//JMA 23/06/2017 ajout fichier conf pour version talismane 4.1.0
						if (pr.uriTalismane().contains("4.1.0")){
							pos = new Talismane(pr.uriTalismane(),pr.lang(),pr.uriTalismaneLang(),pr.uriTalismaneConf());
						}
						else {
							pos = new Talismane(pr.uriTalismane(),pr.lang(),pr.uriTalismaneLang());
						}
						pos.run(fileName+".txt", fileName+"_"+pr.analyserPOS()+".txt");	
					}
					*/
					//JMA 06/07/2017 FIN MISE EN COMMENTAIRE ANCIEN TRAITEMENT
				 
				 	//A supprimer car standfordNLP non installé
					/*if(analyserPOS.equals("stanfordNLP"))
					{
						pos = new StanfordNLP(pr.uriStanfordNLP(pr.lang()),pr.lang());
						pos.run(fileName+".txt", fileName+"_"+pr.analyserPOS()+".txt");	
					}
					*/
				 
				 	//JMA 06/07/2017 DEBUT NOUVEAU TRAITEMENT
					Perdido pr = new Perdido();
					pr.loadParams();
					
					pr.user(username);
					pr.lang(lang);
				
					pr.doAnnotation(true);
					pr.doPOS(true);
					pr.doUnitex(false);
					//pr.doToponymsResolution(false);
					//pr.doToponymsDisambiguation(false);
					//pr.doClustering(false);
					pr.analyserPOS(analyserPOS);
					
					String outputDir = pr.outputDir() + pr.user();
					FileTools.createDir(outputDir);	

					outputDir = pr.outputDir() + pr.user() + "/"+name;
					
					String fileName = outputDir +"/"+name;
					String inputCascade = fileName;
					
					pr.launchAnnotation(content, outputDir,name,fileName,inputCascade);	
                   
				 	//JMA 06/07/2017 FIN NOUVEAU TRAITEMENT
					
					//lire le fichier et le transformer en TEI
					//JMA 21/07/2017 DEBUT
					//InputStream ips = new FileInputStream(fileName+"_"+pr.analyserPOS()+".txt");
					InputStream ips = new FileInputStream(fileName+"_POSTEI.txt");
					//JMA 21/07/2017 FIN
					InputStreamReader ipsr = new InputStreamReader(ips);
					BufferedReader br = new BufferedReader(ipsr);
			
					
					String outputPivot = "";
					String line = "";
					String token = "", posTag = "", lemma = "";
					 
					 contentTEI += "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">" +
								"<teiHeader><fileDesc><titleStmt><title><!-- title of the resource --></title></titleStmt><publicationStmt><p><!-- Information about distribution of the resource --></p></publicationStmt><sourceDesc><p><!-- Information about source from which the resource derives --></p></sourceDesc></fileDesc></teiHeader>" +
								"<text><body><p><s>";
					//JMA 20/07/2017 DEBUT	
					// if(analyserPOS.equals("stanfordNLP") || analyserPOS.equals("treetagger"))
					if(analyserPOS.equals("talismane") || analyserPOS.equals("treetagger") || analyserPOS.equals("freeling"))	 
					 {
						 while ((line = br.readLine()) != null) 
						 {
							if(!line.equals(""))
							{
								String str[] = line.split("\t");
					
								
								token = str[0];// .toLowerCase();
								posTag = str[1];
								lemma = str[2];

								token = StringTools.filtreString(token);
								lemma = StringTools.filtreString(lemma);
								
								//if(pos.equals("."))
								if(token == ".")	
									contentTEI += "</s>";
								else
									contentTEI += "<w lemma=\""+lemma+"\" type=\""+posTag+"\">"+token+"</w>";
								
								
							}
						}
					 
					 }
						
					br.close();
					 
					 
					 contentTEI += "</s></p></body></text></TEI>";
					
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getStackTrace().toString();
				}
			 return contentTEI;
		 }
	
		 
		 

			
			
			protected String checkParam(String function, String content, String param, String api_key)
			{
				String status = "";
				
				if(function.equals("toponymsStandard"))
				{
					if(param.equals(""))
						status += XmlTools.eltStatusXML("Missing 'outputFormat' parameter.");
					
					if(!param.equals("GeoJson") && !param.equals("KML") && !param.equals("GPX") )
						status += XmlTools.eltStatusXML("The value of 'outputFormat' parameter must be: 'GeoJson', 'KML' or 'GPX' : "+param);
						
				}
				else
				{
					if(param.equals(""))
						status += XmlTools.eltStatusXML("Missing 'lang' parameter.");
				
				
					if(function.equals("pos"))
					{
						if(!param.equals("French") && !param.equals("Spanish") && !param.equals("Italian") && !param.equals("English") && !param.equals("German") && !param.equals("Dutch"))
							status += XmlTools.eltStatusXML("The value of 'lang' parameter must be: 'French', 'Spanish', 'Italian', 'English', 'German' or 'Dutch'");
							
					}
					else
					{
						if(!param.equals("French") && !param.equals("Spanish") && !param.equals("Italian"))
							status += XmlTools.eltStatusXML("The value of 'lang' parameter must be: 'French', 'Spanish' or 'Italian'");
					}
				}
				if(api_key.equals(""))
					status += XmlTools.eltStatusXML("Missing 'api_key' parameter.");
				else
				//if(!api_key.equals("demo"))
				{
					try
					{
						//on vérifie que l'utilisateur existe dans la bdd
						User user = new User();
						
						if(user.checkUserAPI_key(api_key))
						{
							System.out.println("checkUser");
							if(user.newConnexion(api_key))
							{
								System.out.println("newConnexion");
							}
							else
								status += XmlTools.eltStatusXML("Your daily limit of credits has been exceeded.");
						}
						else
							status += XmlTools.eltStatusXML("This api_key does not exist.");
					}
					catch(Exception e)
					{
						//System.err.println(e.toString());
						status += XmlTools.eltStatusXML("An error occured, please try again later.");
					}
					//on vérifie que la limite de l'utilisateur n'est pas atteinte
					
					
					if(content.equals(""))
						status += XmlTools.eltStatusXML("Missing 'content' parameter.");
				}
				
				//if(!status.equals(""))
				//	status = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><errors>"+status+"</errors>";
				
				return status;
			}
			
			protected String checkParam(String function, String content, String param, String api_key, String param2)
			{
				String status = checkParam(function, content, param, api_key);
				
				if(function.equals("toponymsStandard"))
				{
					if(!param2.equals("true") && !param2.equals("false") )
						status += XmlTools.eltStatusXML("The value of 'getURL' parameter must be: 'true' or 'false' (default)");
				}
				else
				{
					if(!param2.equals("treetagger") && !param2.equals("freeling") && !param2.equals("talismane") )
						status += XmlTools.eltStatusXML("The value of 'POStagger' parameter must be: 'treetagger' (default), 'freeling' or 'talismane' (French only)");
				}
				
				return status;
			}
			
			
			
			
			protected String checkParam(String function, String content, String lang, String api_key, String POStagger, int disambiguationLevel)
			{
				
				
				String status = checkParam(function, content, lang, api_key, POStagger);
				
			
				if(disambiguationLevel != 0 && disambiguationLevel != 1 && disambiguationLevel != 2)
					status += XmlTools.eltStatusXML("The value of 'disambiguationLevel' parameter must be: '0' (none), '1' (default: remove duplicate points) or '2' (for hiking descriptions)");
				
				return status;
			}
			//---
			protected String checkParam(String function, String content, String lang, String api_key,  int disambiguationLevel)
			{
				
				
				String status = checkParam(function, content, lang, api_key);
				
			
				if(disambiguationLevel != 0 && disambiguationLevel != 1 && disambiguationLevel != 2)
					status += XmlTools.eltStatusXML("The value of 'disambiguationLevel' parameter must be: '0' (none), '1' (default: remove duplicate points) or '2' (for hiking descriptions)");
				
				return status;
			}
		 

}
