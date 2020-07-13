
/*
 * Copyright (C) 2016 Ludovic Moncla <ludovic.moncla@univ-pau.fr>
 * 
 * This file is part of ToponymResolution - Perdido project <http://erig.univ-pau.fr/PERDIDO/>
 *
 * ToponymResolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ToponymResolution is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ToponymResolution. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package choucas.perdido.gazetteers;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import com.google.gson.stream.JsonReader;

import choucas.perdido.elements.Toponyme;




/**
 * OSM class : provides some methods for toponyms resolution from openstreetmap
 * @author Ludovic Moncla
 */
public class OSM extends Gazetteer {
	
	
	/**
	 * 
	 * @param doStrictQuery
	 * @param maxResults
	 * @param lang
	 */
	public OSM(boolean doStrictQuery, int maxResults, String lang) {
		super(doStrictQuery, maxResults, lang);
		_gazetteerName = "OpenStreetMap";
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param toponymCandidate
	 */
	public Vector<Toponyme> searchToponym(String toponymCandidate) throws Exception
	{
		
		Vector<Toponyme> toponyms = new Vector<Toponyme>();
		//Vector<String> resJson = null;
		
		int cpt = 0;
		String params = "/search/?q="+toponymCandidate+"&format=json&limit="+_maxResults;
		
		
	
		Vector<String> resJson = OSM_getJson("nominatim.openstreetmap.org",params);
	
		//on ajoute le nombre de resultats avec OSM dans le fichier csv
		//int nbResult = Integer.parseInt(resJson[0]);
		
	
		for(int i=0;i<resJson.size();i++)
		{
			//on split le retour d'OSM pour obtenir les coordonnées
			String str[]= resJson.get(i).split(";;");
			//name;;lat;;lng;;type
			
			String name = str[0];
			double lat = Double.parseDouble(str[1]);
			double lng = Double.parseDouble(str[2]);
			String feature = str[3];
			
			//on verifie que le résultat proposer par OSM correspond au nom que l'on recherche
			if(_doStrictQuery)
			{
				//on test si la premiere partie du nom correspond
				String c[]= name.split(",");
				
				if(toponymCandidate.equalsIgnoreCase(c[0]) || toponymCandidate.equalsIgnoreCase("le "+c[0]) || toponymCandidate.equalsIgnoreCase("la "+c[0]) || toponymCandidate.equalsIgnoreCase("les "+c[0]) || c[0].equalsIgnoreCase("le "+toponymCandidate) || c[0].equalsIgnoreCase("la "+toponymCandidate) || c[0].equalsIgnoreCase("les "+toponymCandidate))
				{
					/**
					 * Ajout du toponyms dans la structure qui va servir par la suite a remplir le fichier json
					 */
					toponyms.add(new Toponyme(name,toponymCandidate,lat,lng,_gazetteerName,"","",feature,_lang));

				}
			}
			else
			{
				toponyms.add(new Toponyme(name,toponymCandidate,lat,lng,_gazetteerName,"","",feature,_lang));

			}	
		}
		

		
		return toponyms;
		
	}
	
	
	
	/**
	 * 
	 * @param s_url
	 * @param s_params
	 * @return
	 * @throws Exception
	 */
	public static Vector<String> OSM_getJson(String s_url, String s_params) throws Exception
	{
		
		Vector<String> data = new Vector<String>();
		String res = "";
		URL url;
		URI uri;
		int cpt = 0;
		
				
		//uri = new URI("http", "nominatim.openstreetmap.org", "/search.php?q=Place Francois Sarraillé&format=json", "");
		uri = new URI("http", s_url, s_params, "");

		url = uri.toURL();
		
        URLConnection yc = url.openConnection();

        JsonReader reader = new JsonReader(new InputStreamReader(yc.getInputStream()));
       
       // System.err.println("uri : "+url);
        
       // System.err.println("reader : "+reader.toString());
        
        reader.beginArray();
        while (reader.hasNext()) {
    	   String title = "" ,display_name = "", id="", type="";
    	   double lat=0,lng = 0;
	    	   
    	   reader.beginObject();
    	   while (reader.hasNext()) {
			       //messages.add(readMessage(reader));
	    		 //  System.out.println("Object "+cpt);
    		   String name = reader.nextName();
    		   if (name.equals("place_id")) {
				   id = reader.nextString();
				   //System.out.println("place_id "+id);
    		   } else if (name.equals("lat")) {
    			   lat = reader.nextDouble();
				 // System.out.println("lat "+lat);
    		   } else if (name.equals("lon")) {
    			   lng = reader.nextDouble();
				//System.out.println("lon "+lng);
    		   } else if (name.equals("display_name")) {
				   display_name = reader.nextString();
				  // System.out.println("display_name "+display_name);
    		   } else if (name.equals("type")) {
				   type = reader.nextString();
				  // System.out.println("display_name "+display_name);
    		   }
    		   else
    		   {
    			   reader.skipValue();
					// System.out.println(reader.nextString());
    		   }
    	   }
	    	   
    	   //concatenation du nom et des coordonnées
    	   res = display_name+";;"+lng+";;"+lat+";;"+type+";;";
    	   
    	   reader.endObject();
    	   cpt++;
    	   //System.out.println("Object "+cpt+" : display name : "+display_name);
		    
    	  // data[cpt] = res;  
    	   data.add(res);
    	   
	     }
	     reader.endArray();

		
		
		return data;
    }
	
	
	
	
	
	
	
	
	
}
