package choucas.perdido.maps;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import choucas.perdido.elements.Edge;
import choucas.perdido.elements.Node;
import choucas.perdido.elements.Toponyme;
import choucas.perdido.elements.Verb;
import choucas.perdido.postgresql.Postgis;
import choucas.perdido.tools.MapsFunctions;
import choucas.perdido.processing.ItineraryReconstruction;

import com.google.gson.stream.JsonReader;

/**
 * GoogleMaps class : provide some method to use GoogleMaps API
 * @author Ludovic Moncla
 * @version 1.0
 */
public class GoogleMaps {
	 
	private Double _centerLat;
	private Double _centerLng;
	private String _type;
	private int _zoom;
    
    private Vector<Toponyme> markers = new Vector<Toponyme>();
    private Vector<String> icons = new Vector<String>();
  
    
	/**
	 * Constructor class
	 * @param centerLat
	 * @param centerLng
	 * @param zoom
	 * @param type
	 */
	public GoogleMaps(double centerLat, double centerLng, int zoom, String type)
	{
		_centerLat = centerLat;
		_centerLng = centerLng;
		_zoom = zoom;
		_type = type;
		
		/*
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/grey.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/red.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/blue.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/green.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/orange.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/yellow.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/purple.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/red.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/blue.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/green.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/orange.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/yellow.png");
		icons.add("http://maps.google.com/intl/en_us/mapfiles/ms/micons/purple.png");
		*/
		
	}
	
	public String declarElevationFunction() throws Exception
	{
		System.out.println(" Begin declarElevationFunction");
		
		String resultHTML = "<script type=\"text/javascript\">";
		resultHTML += "var elevator = new google.maps.ElevationService(); ";
		
		resultHTML += "function getElevation(el_lat, el_lng, callback) {"+
			  "var locations = []; "+
			  "el = 0; "+
			  "locations.push(new google.maps.LatLng(el_lat, el_lng)); ";
			  // Create a LocationElevationRequest object using the array's one value
		
		resultHTML += "var positionalRequest = {"+
			    "'locations': locations"+
			  "}; ";
		
		// Initiate the location request
				resultHTML += "elevator.getElevationForLocations(positionalRequest, function(results, status) { ";
				resultHTML += "console.log('status : '+status); ";
				
				resultHTML += "if (status == google.maps.ElevationStatus.OK) { ";
					//resultHTML += "for (var iter = 0; iter < results.length; iter++) {";
						resultHTML += "if (results[0]) {";
							resultHTML += "el = results[0].elevation; ";
							resultHTML += "console.log('results : '+el); ";
							
							resultHTML += "callback.succes(results[0].elevation); ";
							resultHTML += "return; ";
							
							//resultHTML += "return results[0].elevation;";
							//resultHTML += "document.getElementById('divElevation').innerHTML = el; ";
				
						resultHTML += "} ";
					//resultHTML += "}";
				resultHTML +="} "+
			  "}); ";
				
				//resultHTML += "console.log('x : '+x); ";
		
			   
		resultHTML += "}";
				
				
		
		resultHTML += "</script>";
		
		
		
		
		System.out.println("End declarElevationFunction");
	
		return resultHTML;
	}

	
	
	public String viewElevation()
	{
		String resultHTML = "";
		
		resultHTML += "<script type=\"text/javascript\">";
		resultHTML += "var e = getElevation(6.591292,45.213845,{"+
				"succes: function(result1) {";
        
		resultHTML += "if(result1){";
		resultHTML += "document.getElementById('divElevation').innerHTML = result1;";
		resultHTML += "}";
		
			resultHTML +=  "}});";
		
			
			
			
	
		//resultHTML += "getElevation();";
		
		
		
		
		resultHTML += "</script>";
		
		
		return resultHTML;
	}
	
	

	/**
	 * loadMarkers loads a vector of toponyms
	 * @param toponyms			vector of toponyme
	 */
	public void loadMarkers(Vector<Toponyme> toponyms) throws Exception
	{
		System.out.println(" Begin loadMarkers");
		
		markers = toponyms;	
		
		System.out.println(" End loadMarkers");
	}

	
	
	

	
	
	/**
	 * loadMarkers loads toponyms from a JSON file
	 * @param toponyms			Json file uri
	 */
	public void loadMarkers(String uriJson) throws Exception
	{
		System.out.println(" Begin loadMarkers");
		JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(uriJson)));
		
		reader.beginArray();
	       while (reader.hasNext()) {
	    	   String title = "" ,description = "",id="",idPath="", src="", polarite ="", verbe="", findName= "",country="",continent="",feature="", featureText="";
	    	   double lat=0,lng = 0;
	    	   int cluster = 0, nb=1,localise = -1;
	    	   reader.beginObject();
	    	   
	    	   while (reader.hasNext()) {
		    	 
	    		 String name = reader.nextName();
				 if (name.equals("title")) {
				   title = reader.nextString();
				 } else if (name.equals("id")) {
				    id = reader.nextString();
				 } else if (name.equals("idPath")) {
				    idPath = reader.nextString();
				 }  else if (name.equals("lat")) {
				        lat = reader.nextDouble();
				 } else if (name.equals("lng")) {
				    lng = reader.nextDouble();
				 } else if (name.equals("description")) {
				       description = reader.nextString();
				 } else if (name.equals("polarite")) {
				       polarite = reader.nextString(); 
				 } else if (name.equals("verbe")) {
					 	verbe = reader.nextString();
				 } else if (name.equals("src")) {
				       src = reader.nextString();
				 } else if (name.equals("nb")) {
				       nb = reader.nextInt();
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
				 } else{
				     reader.skipValue();
				 }
		         
	    	   }
	    	   reader.endObject();
		     
		     if(lat != 0 && lng != 0)
		     {
		    	 Verb verb = new Verb(verbe,"",polarite,"");
		    	 //cptMarker++;
			     //System.out.println("id : "+id+" point : "+title+" lat : "+lat+" lng : "+lng+" src : "+src);
		    	
			     markers.add(new Toponyme(Integer.parseInt(id),Integer.parseInt(idPath),findName,title,verb,lat,lng,src,nb,country,continent,feature,featureText,localise,cluster,false));
		     }
	     }
	     reader.endArray();
	     System.out.println(" End loadMarkers");
	}
	
	
	/**
	 * 
	 * @param bestCluster
	 * @return
	 */
	/*public String getLineHtml(int bestCluster) throws Exception
	{
		System.out.println(" * Begin getLineHtml");
		
		Vector<Node> points = new Vector<Node>();
		//System.out.println("size ="+markers.size());
		
		String html = "<script type=\"text/javascript\">";
		
		for(int i=0;i<markers.size();i++)
		{
		//	System.out.println("i ="+i+" cluster id= "+ markers.get(i).getCluster());
			if(markers.get(i).getCluster() == bestCluster)
			{
				Node pt = new Node(markers.get(i));
				//pt.setWeight(weight);
				points.add(pt);
			}
		}
		
		
		Vector<Edge> arcs = ItineraryReconstruction.minimumSpannigTree(points);
		//System.out.println("size"+ arcs.size());
		
		
		
		
		
		html += "var flightPlanCoordinates = [";
		for(int i=0;i<arcs.size();i++)
		{
			//System.out.println("new google.maps.LatLng("+arcs.get(i).getNodeStart().getLat()+", "+arcs.get(i).getNodeStart().getLng()+")");
			html += "new google.maps.LatLng("+arcs.get(i).getNodeStart().getLng()+", "+arcs.get(i).getNodeStart().getLat()+"),";
		}
	         
	    html += "];";
	    
	          html += "var flightPath = new google.maps.Polyline({"+
	          "path: flightPlanCoordinates,"+
	          "geodesic: true,"+
	          "strokeColor: '#0000FF',"+
	          "strokeOpacity: 1.0,"+
	          "strokeWeight: 2"+
	        "});";
	
	  //  html += " flightPath.setMap(map);";
	    
	    System.out.println(" * End getLineHtml");
	    
	    html += "</script>";
	    
		return html;
	}
	*/
	
	
	/**
	 * 
	 * @param bestCluster
	 * @return
	 */
	public String getPolygonHtml(Postgis objPostgis, String table,int bestCluster) throws Exception
	{
		String html = "<script type=\"text/javascript\">";
		
		//SELECT ST_AsText(ST_ConvexHull((SELECT ST_Collect(coord) FROM evalfr_1e_jour_de_pralognan_au_refuge_de_la_lei_clust WHERE clusterid=0)))
		try {
			//objPostgis.connect("outputDemo");
			objPostgis.connect(objPostgis.db_results());
		
			//select count(distinct id), clusterid from routine_1e_jour_de_champagny_le_haut_au_refuge_d where clusterid>-1 group by clusterid
			Statement state = objPostgis.conn.createStatement();
			ResultSet res = state.executeQuery("SELECT ST_AsText(ST_ConvexHull((SELECT ST_Collect(coord) FROM "+table+" WHERE clusterid="+bestCluster+")))");
			res.next();
				
				
			String poly = res.getString(1);
				
			System.out.println("### poly : "+poly+" ###");
			
			poly = poly.replaceAll("POLYGON\\(\\(", "");
			poly = poly.replaceAll("\\)\\)", "");
			
			String coords[] = poly.split(",");
			
			if(coords.length>1)
			{
			
			
			html += "var polyCoords = [";
			for(int i=0;i<coords.length;i++)
			{
				String coord[] = coords[i].split(" ");
				
				
				html += "new google.maps.LatLng("+coord[1]+", "+coord[0]+"),";
						
			}
			html += "];";
			
			
		
			
			 html += "polygon = new google.maps.Polygon({"+
			          "paths: polyCoords,"+
			          "strokeColor: '#FF0000',"+
			          "strokeOpacity: 1.0,"+
			          "strokeWeight: 2,"+
			          "fillColor: '#FF0000',"+
			          "fillOpacity: 0.35"+
			        "});";
			
			}
		//	    html += "polygon.setMap(map);";
			
			
			res.close();
			
			
			
			objPostgis.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		html += "</script>";
		
		System.out.println("### html : "+html+" ###");
	
		return html;
	}
	
	
	/**
	 * 
	 * @param bestCluster
	 * @return
	 */
	public String getBoundingBox(Postgis objPostgis, String table,int bestCluster) throws Exception
	{
		String html = "<script type=\"text/javascript\">";
		
		//SELECT ST_AsText(ST_ConvexHull((SELECT ST_Collect(coord) FROM evalfr_1e_jour_de_pralognan_au_refuge_de_la_lei_clust WHERE clusterid=0)))
		try {
			//objPostgis.connect("outputDemo");
			objPostgis.connect(objPostgis.db_results());
		
			//select count(distinct id), clusterid from routine_1e_jour_de_champagny_le_haut_au_refuge_d where clusterid>-1 group by clusterid
			Statement state = objPostgis.conn.createStatement();
			ResultSet res = state.executeQuery("SELECT ST_AsText(ST_Envelope(ST_ConvexHull((SELECT ST_Collect(coord) FROM "+table+" WHERE clusterid="+bestCluster+"))))");
			res.next();
				
				
			String poly = res.getString(1);
				
			System.out.println("### poly : "+poly+" ###");
			
			poly = poly.replaceAll("POLYGON\\(\\(", "");
			poly = poly.replaceAll("\\)\\)", "");
			
			String coords[] = poly.split(",");
			
			
			html += "var bboxs = [";
			for(int i=0;i<coords.length;i++)
			{
				String coord[] = coords[i].split(" ");
				
				
				html += "new google.maps.LatLng("+coord[1]+", "+coord[0]+"),";
						
			}
			html += "];";
			
			
		
			
			 html += "var bbox = new google.maps.Polygon({"+
			          "paths: bboxs,"+
			          "strokeColor: '#00FF00',"+
			          "strokeOpacity: 1.0,"+
			          "strokeWeight: 2,"+
			          "fillColor: '#00FF00',"+
			          "fillOpacity: 0.35"+
			        "});";
			
			 
			    html += "bbox.setMap(map);";
			
			
			res.close();
			
			
			
			objPostgis.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		html += "</script>";
		
		System.out.println("### html : "+html+" ###");
	
		return html;
	}
	
	
	
	/**
	 * 
	 * @param objPostgis
	 * @param table
	 * @param bestCluster
	 * @return
	 * @throws Exception
	 */
	public String getCircumscribedCircle(Postgis objPostgis, String table,int bestCluster) throws Exception
	{
		String html = "<script type=\"text/javascript\">";
		
		//SELECT ST_AsText(ST_ConvexHull((SELECT ST_Collect(coord) FROM evalfr_1e_jour_de_pralognan_au_refuge_de_la_lei_clust WHERE clusterid=0)))
		try {
			//objPostgis.connect("outputDemo");
			objPostgis.connect(objPostgis.db_results());
		
			//select count(distinct id), clusterid from routine_1e_jour_de_champagny_le_haut_au_refuge_d where clusterid>-1 group by clusterid
			Statement state = objPostgis.conn.createStatement();
			ResultSet res = state.executeQuery("SELECT ST_AsText(ST_Centroid(ST_Envelope((SELECT ST_Collect(coord) FROM "+table+" WHERE clusterid="+bestCluster+"))))");
			res.next();
				
				
			String point = res.getString(1);
			
			
			
						ResultSet res2 = state.executeQuery("SELECT ST_AsText(ST_Envelope(ST_ConvexHull((SELECT ST_Collect(coord) FROM "+table+" WHERE clusterid="+bestCluster+"))))");
						res2.next();
							
							
						String poly = res2.getString(1);
							
						System.out.println("### poly : "+poly+" ###");
						
						poly = poly.replaceAll("POLYGON\\(\\(", "");
						poly = poly.replaceAll("\\)\\)", "");
						
						if(!poly.equals(point))
						{
						
						String coords[] = poly.split(",");
						
						String coord[] = coords[0].split(" ");
						
						
						
			
				
			System.out.println("### point : "+point+" ###");
			
			point = point.replaceAll("POINT\\(", "");
			point = point.replaceAll("\\)", "");
			
			String centroid[] = point.split(" ");
			
		/*	
			html += "var myLatlng = new google.maps.LatLng("+centroid[1]+","+centroid[0]+");";
			html += "var marker = new google.maps.Marker({"+
			      "position: myLatlng,"+
			      "map: map,"+
			      "title: 'Hello World!'"+
			 "});";
			 
			 */
			
			System.out.println("### point1 : lat: "+centroid[1]+" lng: "+centroid[0]+"###");
			System.out.println("### point2 : lat: "+coord[1]+" lng: "+coord[0]+"###");
			
			
			//Node p = new Node(new Toponyme(Double.parseDouble(centroid[0]),Double.parseDouble(centroid[1])));
			
			//Double dist = p.getDistanceInMeters(new Node(new Toponyme(Double.parseDouble(coord[0]),Double.parseDouble(coord[1]))));
			
			Double dist = MapsFunctions.getDistance(Double.parseDouble(centroid[0]),Double.parseDouble(centroid[1]),Double.parseDouble(coord[0]),Double.parseDouble(coord[1]));
			
			
			System.out.println("### radius : "+dist+" ###");
			
			html += "var circle = new google.maps.Circle({"+
				      "strokeColor: '#0000FF',"+
				      "strokeOpacity: 0.8,"+
				      "strokeWeight: 2,"+
				      "fillColor: '#0000FF',"+
				      "fillOpacity: 0.35,"+
				      "center: new google.maps.LatLng("+centroid[1]+", "+centroid[0]+"),"+
				      "radius: "+dist+
				    "});";
				    
				   // html += "googleCircle = new google.maps.Circle(circle);";
			
				 //   html += "googleCircle.setMap(map);";
			
						}
				    
		res.close();			
		objPostgis.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		html += "</script>";
		
		//System.out.println("### html : "+html+" ###");
	
		return html;
	}
	
	/**
	 * 
	 * @param objPostgis
	 * @param table
	 * @param bestCluster
	 * @return
	 * @throws Exception
	 */
	public String viewRadius() throws Exception
	{
		String html = "<script type=\"text/javascript\">";
		
		System.out.println("viewRadius");
		for(int i=0;i<markers.size();i++)
		{
			//System.out.println("viewRadius : "+i);
			
			html += "var circle = new google.maps.Circle({"+
				      "strokeColor: '#0000FF',"+
				      "strokeOpacity: 0.8,"+
				      "strokeWeight: 1,"+
				      "fillColor: '#0000FF',"+
				      "fillOpacity: 0.0,"+
				      "map: map,"+
				      "center: new google.maps.LatLng("+markers.get(i).getLng()+", "+markers.get(i).getLat()+"),"+
				      "radius: 7000"+
				    "});";
				    
				    html += "radiusMatching = new google.maps.Circle(circle);";
			
				 //  
			
		}
		
		 html += "radiusMatching.setMap(map);";
		 
		html += "</script>";
		
		//System.out.println("### html : "+html+" ###");
	
		return html;
	}
	
	
	
	/**
	 * 
	 * @param bestCluster
	 * @return
	 */
	public static String getCircleHTML(int id, double lat, double lng, double radius) throws Exception
	{
		String html = "<script type=\"text/javascript\">";
		
		
			//System.out.println("### radius : "+dist+" ###");
			
			html += "missingPoint["+id+"] = new google.maps.Circle({"+
				      "strokeColor: '#0000FF',"+
				      "strokeOpacity: 0.8,"+
				      "strokeWeight: 2,"+
				      "fillColor: '#0000FF',"+
				      "fillOpacity: 0.35,"+
				      "center: new google.maps.LatLng("+lat+", "+lng+"),"+
				      "radius: "+radius+
				    "});";
				    
				    	
		html += "</script>";
		
	
		return html;
	}
	
	
		
	/**
	 * getMapHtml returns the html/js code of the google map
	 * @param divName 			name of the div element where the map has to be display
	 * @param bestCluter		if != -1 colour the points
	 * @param showAmbiguities
	 * @return html
	 */
	public String getMapHtml(String divName, int bestCluster, boolean showAmbiguities, String ignKey) throws Exception
	{		
		
		System.out.println(" Begin getMapHtml");
		String html = "<script type=\"text/javascript\">";
		
		
		html += "var ignKey = \""+ignKey+"\";";

		html += "function makeIGNMapType(layer, name, maxZoom) {"+
			        "return new google.maps.ImageMapType({"+
			            "getTileUrl: function(coord, zoom) {"+
			                "return \"http://gpp3-wxs.ign.fr/\" + ignKey + \"/geoportail/wmts?LAYER=\" +"+
			                    "layer +"+
			                    "\"&EXCEPTIONS=text/xml&FORMAT=image/jpeg&SERVICE=WMTS&VERSION=1.0.0\" +"+
			                    "\"&REQUEST=GetTile&STYLE=normal&TILEMATRIXSET=PM&TILEMATRIX=\" +"+
			                    "zoom + \"&TILEROW=\" + coord.y + \"&TILECOL=\" + coord.x;"+
			            "},"+
			            "tileSize: new google.maps.Size(256,256),"+
			            "name: name,"+
			            "maxZoom: maxZoom"+
			        "});"+
			    "}";
	
		html += "markers = [];";
		html += "markers2 = [];";
		
		 html += "var bounds = new google.maps.LatLngBounds ();";	
	
		html += "var myOptions, map, myLatlng;";
	       
	     html += " myLatlng = new google.maps.LatLng( "+_centerLat+", "+_centerLng+" );";
	    
	     /*"myOptions = {"+
	          "zoom: "+_zoom+","+
	          "center: myLatlng,"+
	          "mapTypeId: google.maps.MapTypeId."+_type+""+
	          
	            "scaleControl: true,"+
	            "streetViewControl: true,"+
	            "panControl: false,"+
	            "mapTypeControlOptions: {"+
	                "mapTypeIds: ["+
	                    "'IGN', google.maps.MapTypeId.ROADMAP],"+
	                "style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR"+
	            "}"+
	      "};";
	      */
	     
	     
	     html += "map = new google.maps.Map( document.getElementById( \""+divName+"\" ), {" +
		            "scaleControl: true,"+
		            "streetViewControl: true,"+
		            "panControl: false,"+
		            "mapTypeId: 'IGN',"+
		            "mapTypeControlOptions: {"+
		                "mapTypeIds: [google.maps.MapTypeId.ROADMAP,google.maps.MapTypeId.TERRAIN,google.maps.MapTypeId.HYBRID,'IGN'],"+
		                "style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR"+
		            "},"+
		            "zoom: "+_zoom+","+
		            "center: myLatlng"+
		       "}); ";
	     
	     
	     html += "map.mapTypes.set('IGN', makeIGNMapType(\"GEOGRAPHICALGRIDSYSTEMS.MAPS\", \"IGN\", 18));";
	     html += "map.setMapTypeId(google.maps.MapTypeId.ROADMAP);";
	     
	     for(int i=0;i<markers.size();i++)   
	     {
	    	Toponyme m = markers.elementAt(i);
	    	
		    html += " var latLng = new google.maps.LatLng("+m.getLng()+","+m.getLat()+"); ";
		    String indice = "0";       
		    if(m.getId()<29) 
		    	indice = m.getId()+"";
		       
		    if(m.getCluster() == bestCluster || bestCluster == -1)
		    {
		    	html += "var marker"+i+" = new google.maps.Marker({"+
	                 	" position: latLng,"+
	                 	" title: \""+m.getName()+"\","+
	                 	"icon : new google.maps.MarkerImage("+
		    		    "\"./icons/"+indice+".png\","+
		    		    "null, "+
		    		    "null, "+
		    		    "null, "+
		    		    "new google.maps.Size(18, 22)"+
		    		")  "+
		    	" });";
		    	
		    	   html += "var infoWindow"+i+" = new google.maps.InfoWindow({"+
						    "content  : \"<div class='gm-title'>"+m.getId()+"- "+m.getName()+"</div><div class='gm-basicinfo'>Name : "+m.getValue()+"<br/>Feature : "+m.getFeature()+"<br/>Source : "+m.getSources()+"<br/>Number : "+m.getNb()+"</div>\","+
						    "position : latLng"+
						"});";
				     
					html += "google.maps.event.addListener(marker"+i+", 'click', function() {"+
									"infoWindow"+i+".open(map,marker"+i+");"+
							"});";

					html += "markers.push({id:\""+m.getId()+"\",src:\""+m.getSources()+"\",nb:\""+m.getNb()+"\",marker:marker"+i+"});";
					
					 html += "bounds.extend(latLng);";
		    }
		    else
		    {
		    	
			    html += "var marker"+i+" = new google.maps.Marker({"+
		                 	" position: latLng,"+
		                 	" title: \""+m.getName()+"\","+
		                 	"icon : new google.maps.MarkerImage("+
			    		    //"\"./icons/hide.png\","+
			    		    "\"./icons/"+indice+".png\","+
			    		    "null, "+
			    		    "null, "+
			    		    "null, "+
			    		    "new google.maps.Size(18, 22)"+
			    		")  "+
		          " });";
			    
			    html += "var infoWindow"+i+" = new google.maps.InfoWindow({"+
					    "content  : \"<div class='gm-title'>"+m.getId()+"- "+m.getName()+"</div><div class='gm-basicinfo'>Nom : "+m.getValue()+"<br/>Feature : "+m.getFeature()+"<br/>Source : "+m.getSources()+"<br/>Number : "+m.getNb()+"</div>\","+
					    "position : latLng"+
					"});";
			     
				html += "google.maps.event.addListener(marker"+i+", 'click', function() {"+
								"infoWindow"+i+".open(map,marker"+i+");"+
						"});";

				html += "markers2.push({id:\""+m.getId()+"\",src:\""+m.getSources()+"\",nb:\""+m.getNb()+"\",marker:marker"+i+"});";
			}
	     }
	     
	     
	     html += "map.fitBounds(bounds);";
	     
	     
	     html += "for(j=0;j<markers.length;j++){ markers[j].marker.setMap(map); }";
	     
	     
	     if(showAmbiguities)
	     {
	    	 html += "for(j=0;j<markers2.length;j++){ markers2[j].marker.setMap(map); }";
	   	 }
	     
	     
	     html += "function filtre(type){"+
					"for(i=0;i<markers.length;i++){"+
						"if(markers[i].src == type){"+
						"if (markers[i].marker.getMap() === null) {"+
							"markers[i].marker.setMap(map);}"+
						"else {markers[i].marker.setMap(null);}}}}";
				
	     html += "</script>";
	     
	     System.out.println(" End getMapHtml");
	     
	     return html;
	}
	
	/**
	 * getKMLHtml return the html code to add a KML layer on the google map
	 * @param urlFileName		url of the KML file to be added
	 * @return html
	 */
	public String getKMLHtml(String urlFileName) throws Exception
	{
		System.out.println(" Begin getKMLHtml");
		String html = "<script type=\"text/javascript\">";
		
		
		html += "var ctaLayer = new google.maps.KmlLayer({"+
				"url: '"+urlFileName+"'"+
				//"url: 'http://www.lmoncla.fr/kml/"+urlFileName+"'"+
				//"url: 'http://gmaps-samples.googlecode.com/svn/trunk/ggeoxml/cta.kml'"+
				"});";
				//"ctaLayer.setMap(map);";
		 
		html += "</script>";
	     
		 System.out.println(" End getKMLHtml");
		 
		
		return html;
	}
	/**
	 * getGPXHtml return the html code to add a GPX trace on the google map
	 * @param urlFileName		url of the GPX file to be added
	 * @return html
	 */
	public String getGPXHtml(String urlFileName) throws Exception
	{
		System.out.println(" Begin getGPXHtml");
		String html = "<script type=\"text/javascript\">";
		 
		 html += "$.ajax({";
	     html += "type: \"GET\",";
	     html += "url: \""+urlFileName+"\",";
		 html += "dataType: \"xml\",";
		 html += "success: function(xml) {";
		 html += "var points = [];";
		 html += "var bounds = new google.maps.LatLngBounds ();";
		 html += "$(xml).find(\"trkpt\").each(function() {";
		 html += "var lat = $(this).attr(\"lat\");";
		 html += "var lon = $(this).attr(\"lon\");";
		 html += "var p = new google.maps.LatLng(lat, lon);";
		 html += "points.push(p);";
		 html += "bounds.extend(p);";
		 html += "});";

		 html += "poly = new google.maps.Polyline({";
	         // use your own style here
		 html += "path: points,";
		 html += "strokeColor: \"#FF0000\",";
		 //html += "strokeColor: \"#FF00AA\",";
		 html += "strokeOpacity: 0.6,";
		 html += "strokeWeight: 6";
		 html += "});";
	       
		 //html += "poly.setMap(map);";
	       
	       // fit bounds to track
		 html += "map.fitBounds(bounds);";
		 html += "}";
		 html += "});";
		 
		 html += "</script>";
     
		 System.out.println(" End getGPXHtml");
		 
		return html;
	}
	
	public String showPath() throws Exception
	{
		System.out.println(" Begin showPath");
		String html = "<script type=\"text/javascript\">";
		 
		 int cpt = 0;
		 
		 html += "var points = [];";
		
		 for(int i=0;i<markers.size();i++)
		 {
			 
			 if(markers.get(i).getIdPath() == cpt)
			 {
	
				 html += "p = new google.maps.LatLng("+markers.get(i).getLng()+", "+markers.get(i).getLat()+");";
				 html += "points.push(p);";
				 i=-1;
				 cpt++;
			 }
			
		 
		 }
		 
		 html += "polypath = new google.maps.Polyline({";
		 // use your own style here
			 html += "path: points,";
			 //html += "strokeColor: \"#FF00AA\",";
			 html += "strokeColor: \"#0000FF\",";
			 html += "strokeOpacity: .7,";
			 html += "strokeWeight: 6";
		 html += "});";
		 
		 html += "polypath.setMap(map);";
		 
		
		
		 
		 
		 
		 
			 
			 html += "</script>";
		
		System.out.println(" End showPath");
		return html;
	}
	
	
	
	public String showBuffer(Postgis objPostgis) throws Exception
	{
		System.out.println(" Begin showBuffer");
		String html = "<script type=\"text/javascript\">";
		 
		 int cpt = 0;
		 
		Vector<Toponyme> v = new Vector<Toponyme>();
		
		 for(int i=0;i<markers.size();i++)
		 {
			 
			 if(markers.get(i).getIdPath() == cpt)
			 {
	
				 v.add(markers.get(i));
				 i=-1;
				 cpt++;
			 }
			
		 
		 }

		 try {
			 
		 //objPostgis = new Postgis("localhost","5432","postgres","postgres");
		 objPostgis.connect(objPostgis.db_results());
		Statement state = objPostgis.conn.createStatement();
				
		// Define the LatLng coordinates for the polygon's path.
				 
		 html += "var polygons = [];";
		 
		 for(int i=0;i<v.size()-1;i++)
		 {
			 
			
			 {
				// ici
				 String seg_lineString = v.get(i).getLng()+" "+v.get(i).getLat()+","+v.get(i+1).getLng()+" "+v.get(i+1).getLat();
				 
				 
				 String stReq = "select ST_length(ST_GeographyFromText('LINESTRING("+seg_lineString+")'))";
				 ResultSet res = state.executeQuery(stReq);
				 res.next();
				 double seg_length = res.getDouble(1);
				 
				 double buffer = (15 * seg_length ) / 100; 
				 
				  stReq = "select St_AsText(ST_Buffer(ST_GeographyFromText('LINESTRING("+seg_lineString+")'),"+buffer+"))";
				 
				  res = state.executeQuery(stReq);
				 res.next();
				 String ch = res.getString(1);
				 
				 
				 html += "var polygonCoords"+i+" = [ ";
				 
				 ch = ch.replaceAll("POLYGON\\(\\(", "");
				 ch = ch.replaceAll("\\)\\)", "");
				 
				 String c[] = ch.split(",");
				 for(int j=0;j<c.length;j++)
				 {
					 String c2[] = c[j].split(" ");
					 
					 
					 System.out.println("ic polygon : "+c[j]);
					 
					 if(j == c.length-1)
						 html += "new google.maps.LatLng("+c2[0]+","+c2[1]+") ";
					 else
						 html += "new google.maps.LatLng("+c2[0]+","+c2[1]+"), ";
				 }
				 
				
				 
				 html += "]; ";
				 
				
					
				 
				  // Construct the polygon.
				 html += "polygon"+i+" = new google.maps.Polygon({ ";
				 html += "paths: polygonCoords"+i+", " ;
				 html += "strokeColor: '#0000FF', " ;
				 html += "strokeOpacity: 0.8, ";
				 html += "strokeWeight: 1,";
				 html += " fillColor: '#0000FF', ";
				 html += "fillOpacity: 0.25 ";
				 html += " }); ";
	
				 
				 html += " polygons.push(polygon"+i+"); ";
				 
				 html += "polygon"+i+".setMap(map); ";
				 
				
			 }
		 }
		 
		 
		 
		 } catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.print(e.toString());
				e.printStackTrace();
			}
		
		/* html += "new google.maps.LatLng(25.774252, -80.190262),";
		 html += "new google.maps.LatLng(18.466465, -66.118292),";
		 html += "new google.maps.LatLng(32.321384, -64.75737),";
		 html += "new google.maps.LatLng(25.774252, -80.190262)";
		*/

		
		 
		 
			 
			 html += "</script>";
		
		System.out.println(" End showBuffer");
		return html;
	}
	
	public String getGPXHtml_test(String urlFileName) throws Exception
	{
		System.out.println(" Begin getGPXHtml");
		String html = "<script type=\"text/javascript\">";
		 
		html += "var xml = '"+urlFileName+"';";
		
		// html += "$.ajax({";
	    // html += "type: \"GET\",";
	    // html += "url: \""+urlFileName+"\",";
		// html += "dataType: \"xml\",";
		// html += "success: function(xml) {";
		 html += "var points = [];";
		 html += "var bounds = new google.maps.LatLngBounds ();";
			 html += "$(xml).find(\"trkpt\").each(function() {";
				 html += "var lat = $(this).attr(\"lat\");";
				 html += "var lon = $(this).attr(\"lon\");";
				 html += "var p = new google.maps.LatLng(lat, lon);";
				 html += "points.push(p);";
				 html += "bounds.extend(p);";
			 html += "});";

			
	       
		// html += "poly.setMap(map);";
	       
	       // fit bounds to track
		 html += "map.fitBounds(bounds);";
		// html += "}";
		 //html += "});";
		 
		 html += "</script>";
     
		 System.out.println(" End getGPXHtml");
		 
		return html;
	}
}
