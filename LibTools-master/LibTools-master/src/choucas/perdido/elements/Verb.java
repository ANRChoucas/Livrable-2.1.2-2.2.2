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
 * Verb class
 * @author Ludovic Moncla
 * @version 1.0
 */
public class Verb implements Serializable{
	 
	private String _type = "";
	private String _value = "";
    private String _lemma = "";
    private String _polarity = "";

    
    public Verb()
    {
    	
    }
	/**
	 * Constructor class
	 * @param value
	 * @param type
	 * @param polarity
	 */
	public Verb(String value, String type, String polarity, String lemma)
	{
		_type = type;
		_value = value;
		_polarity = polarity;
		_lemma = lemma;
	}
	
	
	/**
	 *  value
	 * @return
	 */
	public String getValue()
    {
    	return _value;
    }
	
	/**
	 * 
	 * @return type
	 */
    public String getType()
    {
    	return _type;
    }
    
    /**
     * 
     * @return polarity
     */
    public String getPolarity()
    {
    	return _polarity;
    }
    
    /**
     * 
     * @return name
     */
    public String getLemma()
    {
    	return _lemma;
    }
	
}
