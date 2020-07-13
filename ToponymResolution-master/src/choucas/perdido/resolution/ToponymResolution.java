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

package choucas.perdido.resolution;

import java.util.Vector;

import choucas.perdido.elements.Toponyme;
import choucas.perdido.postgresql.Postgis;
import choucas.perdido.gazetteers.Gazetteer;
import choucas.perdido.gazetteers.Geonames;
import choucas.perdido.gazetteers.IGN;
import choucas.perdido.gazetteers.OSM;




/**
 * Gazetteer class : provide some methods for toponyms resolution, query gazeteers and apply clustering methods.
 * @author Ludovic Moncla
 * @version 1.0
 */
public class ToponymResolution {
	
	private boolean _doStrictQuery;
	private int _maxResults;
	private String _lang;
	
	
	private boolean _ign = false;
	private boolean _geonames = false;
	private boolean _osm = false;
	
	private String[] _ign_bdd_tables = null;
	private String _geom = null;
	private String _geonamesAPIkey = "";
	private Postgis _objPostgis;
			
	
	
	public ToponymResolution(boolean doStrictQuery, int maxResults, String lang, boolean geonames, boolean ign, boolean osm,String[] ign_bdd_tables, String geom, Postgis objPostgis, String geonamesAPIkey) 
	{
		_doStrictQuery = doStrictQuery;
		_maxResults = maxResults;
		_lang = lang;
		
		_geonames = geonames;
		_ign = ign;
		_osm = osm;
		
		_ign_bdd_tables = ign_bdd_tables;
		_geom = geom;
		_objPostgis = objPostgis;
		_geonamesAPIkey = geonamesAPIkey;
	}
	
	
	
	
	public Vector<Toponyme> searchToponym(String toponymCandidate)
	{
		System.out.println(" -- searchToponym : "+toponymCandidate);
		
		Vector<Toponyme> toponyms = new Vector<Toponyme>();
		int sizeTmp = 0;
		Gazetteer gazetteer = null;
		
		try {
			System.out.println(" + ign : "+_ign);
			if(_ign){
				
				if(_lang.equals("French"))
					gazetteer = new IGN(_doStrictQuery, _maxResults, _lang, _ign_bdd_tables, _geom, _objPostgis);
				else
					gazetteer = new IGN(_doStrictQuery, _maxResults, _lang);
				
				toponyms.addAll(gazetteer.searchToponym(toponymCandidate));
			}
			System.out.println(toponyms.size()+" results");
			sizeTmp = toponyms.size();
			
			System.out.println(" + osm : "+_osm);
			if(_osm){
				gazetteer = new OSM(_doStrictQuery, _maxResults, _lang);
				toponyms.addAll(gazetteer.searchToponym(toponymCandidate));
			}
			System.out.println(toponyms.size()-sizeTmp+" results");
			sizeTmp = toponyms.size();
			
			System.out.println(" + geonames : "+_geonames);
			if(_geonames){
				gazetteer = new Geonames(_doStrictQuery, _maxResults, _lang,_geonamesAPIkey);
				toponyms.addAll(gazetteer.searchToponym(toponymCandidate));
			}
			System.out.println(toponyms.size()-sizeTmp+" results");
			sizeTmp = toponyms.size();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return toponyms;
	}
	
	
	
	
	
}
