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

import java.util.Vector;

import choucas.perdido.elements.Toponyme;




/**
 * Gazetteer class : provides some methods for toponyms resolution and query gazeteers
 * @author Ludovic Moncla
 * @version 1.0
 */
public abstract class Gazetteer {
	
	
	protected boolean _doStrictQuery;
	protected int _maxResults;
	protected String _lang;
	protected String _gazetteerName = "";
	
	
	public Gazetteer(boolean doStrictQuery, int maxResults, String lang) 
	{
		_doStrictQuery = doStrictQuery;
		_maxResults = maxResults;
		_lang = lang;
	}
	
	
	
	public abstract Vector<Toponyme> searchToponym(String toponymCandidate) throws Exception;
	
	
	
	
	
	
	
}
