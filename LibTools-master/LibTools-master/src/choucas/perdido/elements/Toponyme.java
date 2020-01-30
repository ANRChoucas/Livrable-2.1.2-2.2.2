/*
 * Copyright (C) 2016 Ludovic Moncla <ludovic.moncla@univ-pau.fr>
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

package choucas.perdido.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Toponyme class
 * @author Ludovic Moncla
 * @version 1.0
 */
@XmlRootElement(name = "toponym")  
public class Toponyme implements Serializable{
	 
	private int _gid = 0;
    private int _id = 0;   
    private int _iid = -1;
    private int _idPath = -1;
    protected String _value = ""; //name find in the resource
    protected String _name = ""; 
    private Verb _verb = null;
    private double _lat = 0;
    private double _lng = 0;
    private double _elevation = 0;
    private String _clcCode = "";
    private int _perception = 0;
    private int _negation = 0;
    
    
    private String _relSpatial = ""; //N,S,W,E,NW,NE,SW,SE
    private int _relTemporal = -1;	//id of another toponym
    
    private double _direction = -1;
    
    
    //private String _source = "";
    private List<String> _source = new ArrayList<String>();
    //private Vector<String> _source = new Vector<String>();
    private String _country = "";
    private String _continent = "";
   
    
    private String _feature = ""; //feature from gazeetter
    private String _featureText = ""; //feature from text 
    
    private boolean _sameFeature;
    
    private String _lang = "";
    
    private int _nb = 1; // nombre de toponymes trouvés dans la ressource ayant le mm nom
    private int _localise = -1; // 1 :localise, 0 : semi-localise, -1 : non localise
    
    
    private int _cluster = 0; //cluster id
    boolean _isBest = false;
    
    
    public Toponyme()
   	{
    	
   	}
    
    
/**
 * Constructor class
 * @param id
 * @param value
 * @param name
 * @param verb
 * @param lat
 * @param lng
 * @param source
 * @param cluster
 * @param nb
 * @param country
 * @param continent
 * @param feature
 * @param localise
 */
    public Toponyme(int id, int idPath, String value, String name, Verb verb, double lat, double lng, String source, int nb, String country, String continent, String feature,String featureText, int localise,int cluster,boolean isBest)
	{
    	
    	_id = id;
    	_iid = -1;
    	_idPath = idPath;
		_value = value; //name find in the resource
		_name = name;
		_verb = verb;
		_lat = lat;
		_lng = lng;
		_source.add(source);
		
		_nb = nb;
		_country = country;
		_continent = continent;
		_feature = feature;
		_featureText = featureText;
		_localise = localise;
		
		_cluster = cluster;
		_isBest = isBest;
		
	}
    
    
    public Toponyme(String value, String name, double lat, double lng, String source, String country, String continent, String feature, String lang)
   	{
      
   		_value = value; //name find in the resource
   		_name = name;
   		_lat = lat;
   		_lng = lng;
   		_source.add(source);
   		_country = country;
   		_continent = continent;
   		_feature = feature;
   		_lang = lang;
   	}
       
    
    
    /**
     * 
     * @param lat
     * @param lng
     */
    public Toponyme(double lat, double lng)
    {
		_lat = lat;
		_lng = lng;	
    }
    
    /**
     * 
     * @param lat
     * @param lng
     * @param elevation
     */
    public Toponyme(double lat, double lng, double elevation)
    {
		_lat = lat;
		_lng = lng;	
		_elevation = elevation;
    }
    
    /**
     * 
     * @return id
     */
    public int getId()
    {
    	return _id;
    }
    
    /**
     * 
     * @return id
     */
    public int getIid()
    {
    	return _iid;
    }
    
    /**
     * 
     * @return id
     */
    public void setIid(int iid)
    {
    	_iid = iid;
    }
    
    public void setId(int id)
    {
    	_id = id;
    }
    
    
    public String getRelSpatial()
    {
    	return _relSpatial;
    }

    public void setRelSpatial(String relSpatial)
    {
    	_relSpatial = relSpatial;
    }
    
    public double getDirection()
    {
    	return _direction;
    }

    public void setDirection(double direction)
    {
    	_direction = direction;
    }
    
    public int getRelTemporal()
    {
    	return _relTemporal;
    }

    public void setRelTemporal(int relTemporal)
    {
    	_relTemporal = relTemporal;
    }
    
    
    public int getPerception()
    {
    	return _perception;
    }

    public void setPerception(int perception)
    {
    	_perception = perception;
    }
    
    public int getNegation()
    {
    	return _negation;
    }

    public void setNegation(int negation)
    {
    	_negation = negation;
    }
    
    /**
     * 
     * @return clcCode
     */
    public String getClcCode()
    {
    	return _clcCode;
    }

    /**
     * 
     * @param clcCode
     */
    public void setClcCode(String clcCode)
    {
    	_clcCode = clcCode;
    }
   
   
    /**
     * 
     * @return latitude
     */
    public Double getLat()
    {
    	return _lat;
    }
    

    
    /**
     * 
     * @return longitude
     */
    public Double getLng()
    {
    	return _lng;
    }
    
    /**
     * 
     * @return idPath
     */
    public int getIdPath()
    {
    	return _idPath;
    }
    
    /**
     * 
     * @return longitude
     */
    public void setIdPath(int idPath)
    {
    	_idPath =  idPath;
    }
    
    /**
     * 
     * @return elevation
     */
    public Double getElevation()
    {
    	return _elevation;
    }
    
    /**
     * 
     * @return cluster
     */
    public int getCluster()
    {
    	return _cluster;
    }
    
    /**
     * 
     * @return nb of results for this toponym
     */
    
    public int getNb()
    {
    	return _nb;
    }
    
    /**
     * 
     * @return value
     */
    public String getValue()
    {
    	return _value;
    }
    
    /**
     * 
     * @return 0 = found with subtoponym; 1= found with full name
     */
    
    public int getLocalise()
    {
    	return _localise;
    }
    
    /**
     * 
     * @return verb associated or null
     */
    
    
    public void setVerb(Verb verb)
    {
    	_verb = verb;
    
    }
    
    
    public Verb getVerb()
    {
    	if(_verb == null)
    		return null;
    	
    	return _verb;
    }
    
    /**
     * 
     * @param cluster
     */
    public void setCluster(int cluster)
    {
    	_cluster = cluster;
    }
    
    /**
     * 
     * @param nb
     */
    public void setNb(int nb)
    {
    	_nb = nb;
    }
    
    /**
     * 
     * @param elevation
     */
    public void setElevation(double elevation)
    {
    	_elevation = elevation;
    }
    
    /**
     * 
     * @param country
     */
    public void setCountry(String country)
    {
    	_country = country;
    }
    
    /**
     * 
     * @param continent
     */
    public void setContinent(String continent)
    {
    	_continent = continent;
    }
    
    /**
     * 
     * @param feature
     */
    public void setFeature(String feature)
    {
    	_feature = feature;
    }
    
    /**
     * 
     * @param feature
     */
    public void setFeatureText(String featureText)
    {
    	_featureText = featureText;
    }
    
  
    
    public void setSameFeature(boolean sameFeature)
    {
    	_sameFeature = sameFeature;
    }
    
    public boolean isSameFeature()
    {
    	return _sameFeature;
    }
    /**
     * 
     * @param localise
     */
    public void setLocalise(int localise)
    {
    	_localise = localise;
    }
    
    /**
     * 
     * @param isBest
     */
    public void setIsBest(boolean isBest)
    {
    	_isBest = isBest;
    }
    
    /**
     * 
     * @return isBest
     */
    public boolean isBest()
    {
    	return _isBest;
    }
    
    
    /**
     * 
     * @param gid
     */
    public void setGid(int gid)
    {
    	_gid = gid;
    }
    
    /**
     * 
     * @param source
     */
    public void addSource(String source)
    {
    	_source.add(source);
    }
    
    
    /**
     * 
     * @return gid
     */
    public int getGid()
    {
    	return _gid;
    }
    
    
    /**
     * 
     * @return name
     */
    @XmlElement
    public String getName()
    {
    	return _name;
    }
    

    /**
     * 
     * @return source
     */
    @XmlElement
    public String getSources()
    {
    	String sources = "";
    	Iterator itr = _source.iterator();
        while(itr.hasNext()) {
           Object element = itr.next();
           sources += element+";";
        }
    	return sources;
    }
    
    /**
     * 
     * @return
     */
    public String getSource()
    {
    	
    	return _source.get(0);
    }
    
    /**
     * 
     * @param source
     * @return
     */
    public boolean sourceEqual(String source)
    {
    	
    	for(int i=0;i<_source.size();i++)
    	{
    		if(_source.get(i).equals(source))
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * 
     * @return country
     */
    @XmlElement
    public String getCountry()
    {
    	return _country;
    }
    
    /**
     * 
     * @return continent
     */
    @XmlElement
    public String getContinent()
    {
    	return _continent;
    }
    
    /**
     * 
     * @return feature
     */
    @XmlElement
    public String getFeature()
    {
    	return _feature;
    }
    
    /**
     * 
     * @return featureText
     */
    @XmlElement
    public String getFeatureText()
    {
    	return _featureText;
    }
    
    
    /**
     * 
     * @return coord
     */
    @XmlElement
    public String getCoord()
    {
    	return _lat+","+_lng;
    }
    
    
    public String getXML()
    {
    	String xml ="";
    	xml +="<toponym continent=\""+_continent+"\" country=\""+_country+"\" lat=\""+_lat+"\" lng=\""+_lng+"\" feature=\""+_feature+"\" nameFound=\""+_value+"\" source=\""+this.getSources()+"\" >"+_name+"</toponym>";
    	
    	return xml;
    }
    
    
    public String getLang()
    {
    	return _lang;
    }
    
    public void setLang(String lang)
    {
    	_lang = lang;
    }
   
}
