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


/**
 * Node class
 * @author Ludovic Moncla
 */
public class Node implements Serializable{
	
	private Toponyme _toponym = null;
    private int _weight = 1;
    
	/**
	 * Constructor class
	 * @param toponym
	 */
    
    public Node()
   	{
       	
   	}
    
    public Node(Toponyme toponym)
	{
    	_toponym = toponym;	
	}
    
    
    public Toponyme getToponym()
    {
    	return _toponym;
    }
    /**
     * 
     * @return id
     */
    public int getId()
    {
    	return _toponym.getId();
    }
    
    public int getIid()
    {
    	return _toponym.getIid();
    }
    
    /**
     * 
     * @return id
     */
    public int getGid()
    {
    	return _toponym.getGid();
    }
    
    /**
     * 
     * @return value
     */
    public String getValue()
    {
    	return _toponym.getValue();
    }
    
  
    
    /**
     * 
     * @return name
     */
    public String getName()
    {
    	return _toponym.getName();
    }
    
    /**
     * 
     * @return latitude
     */
    public Double getLat()
    {
    	return _toponym.getLat();
    }
    
    /**
     * 
     * @return longitude
     */
    public Double getLng()
    {
    	return _toponym.getLng();
    }
   
    /**
     * 
     * @return weight
     */
    public int getWeight()
    {
    	return _weight;
    }
    
    public void setWeight(int weight)
    {
    	_weight = weight;
    }
    
    
    
    
    

}
