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

package choucas.perdido.tools;

import java.util.Vector;

import choucas.perdido.elements.Coord;
import choucas.perdido.elements.Toponyme;

/**
 * MapsFunctions class
 * @author Ludovic Moncla
 * @version 1.0
 */
public class MapsFunctions {
	 
	
	
	
	
	/**
	 * Constructor class
	 */
	public MapsFunctions()
	{
		
		
	}
	
	
	/**
     * Compute the great-circle distance with weights
     * @param point
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2)
    {
    	double earth_radius = 6378137;
    
   
    		
    	double radLat1 = lat1*Math.PI/180;
    	double radLng1 = lng1*Math.PI/180;
    	double radLat2 = lat2*Math.PI/180;
    	double radLng2 = lng2*Math.PI/180;
    			
    			
    	 
		//haversine formula
		double d = 2 * Math.asin(Math.sqrt(Math.pow (Math.sin((radLat2-radLat1)/2) , 2) + Math.cos(radLat1)*Math.cos(radLat2)* Math.pow( Math.sin((radLng2-radLng1)/2) , 2)));
	 	
    			
    	return (earth_radius * d);
    }
    
   
    
    /**
     * Compute the great-circle distance using elevation
     * @param point
     * @return
     */
    public static double getDistanceUsingElevation(double d, double el1, double el2)
    {
    	
    	double h = Math.abs(el1 - el2);
    	
    	//double dist = d + (Math.pow(h, 2) / (2 * d));
    	double tmp = Math.pow(d,2) + Math.pow(h,2);
    	//pythagore
    	double dist = Math.sqrt(tmp);
    	
    	//System.out.println("** dist : "+dist);
    	return dist;
    	
    }
    
    /**
     * Compute the great-circle distance using elevation
     * @param point
     * @return
     */
    public static double getDistanceUsingElevation(double lat1, double lng1, double el1, double lat2, double lng2, double el2)
    {
    	
    	double d = getDistance(lat1, lng1, lat2,lng2);
    	
    	//System.out.println("d : "+d+" , e1 : "+el1+" , e2 : "+el2);
    	
    	return getDistanceUsingElevation(d, el1, el2);
    
    }
    
    
    /**
     * 
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return euclidian distance
     */
    public static double getEuclidianDistance(double lat1, double lng1, double lat2, double lng2)
    {
    
	    	double coef = 111352; //conversion degre <> metres
	    	
	    	lat1 = lat1 * coef;
	    	lat2 = lat2 * coef;
	    	lng1 = lng1 * coef;
	    	lng2 = lng2 * coef;
	    	
	    	//pythagore
	    	double dist = Math.sqrt(Math.pow(Math.abs(lat1 - lat2), 2) + Math.pow(Math.abs(lng1 - lng2), 2));
	    	
	    	 
	    	return dist;
    	
    }
    
    
    public static Coord getBarycenter(Coord c1, Coord c2)
    {
    
    		double lat = (c1.lat + c2.lat) / 2;
    		double lng = (c1.lng + c2.lng) / 2;
    		
    		return new Coord(lat, lng);
    		
    	
    }
    
    public static Coord getBarycenterCoord(Vector<Coord> vec)
    {
    	
    		try
    		{
	    		double sumLat = 0;
	    		double sumLng = 0;
	    		
	    		for(int i=0;i<vec.size();i++)
	    		{
	    			sumLat += vec.get(i).lat;
	    			sumLng += vec.get(i).lng;
	    		}
	    		
	    		double lat = sumLat / vec.size();
	    		double lng = sumLng / vec.size();
	    		
	    		return new Coord(lat, lng);
	    		
    		}
    		catch(Exception e)
    		{
    			
    		}
	    	 
	   return null;
    	
    }
    
    
    
    public static Coord getBarycenter(Vector<Toponyme> vec)
    {
    	
    		try
    		{
	    		double sumLat = 0;
	    		double sumLng = 0;
	    		
	    		for(int i=0;i<vec.size();i++)
	    		{
	    			sumLat += vec.get(i).getLat();
	    			sumLng += vec.get(i).getLng();
	    		}
	    		
	    		double lat = sumLat / vec.size();
	    		double lng = sumLng / vec.size();
	    		
	    		return new Coord(lat, lng);
	    		
    		}
    		catch(Exception e)
    		{
    			
    		}
	    	 
	   return null;
    	
    }
    

	
}
