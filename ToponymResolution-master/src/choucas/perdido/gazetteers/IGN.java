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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import choucas.perdido.elements.Toponyme;
import choucas.perdido.postgresql.Postgis;
import choucas.perdido.toponymDiscovery.ItalianToponymArtifactFactory;
import choucas.perdido.toponymDiscovery.SpanishToponymArtifactFactory;
import choucas.perdido.toponymDiscovery.ToponymArtifactFactory;
import choucas.perdido.toponymDiscovery.ToponymFinder;
import choucas.perdido.toponymDiscovery.ToponymInfo;


/**
 * IGN class : provides some methods for toponyms resolution from national gazetteer (IGN)
 * @author Ludovic Moncla
 */
public class IGN extends Gazetteer {
	
	
	private String[] _ign_bdd_tables = null;
	private String _geom = null;
	private Postgis _objPostgis;
			
		
	/**
	 * 
	 * @param doStrictQuery
	 * @param maxResults
	 * @param lang
	 */
	public IGN(boolean doStrictQuery, int maxResults, String lang) {
		super(doStrictQuery, maxResults, lang);
		_gazetteerName = "IGN";
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 
	 * @param doStrictQuery
	 * @param maxResults
	 * @param lang
	 * @param ign_bdd_tables
	 * @param geom
	 * @param objPostgis
	 */
	public IGN(boolean doStrictQuery, int maxResults, String lang, String[] ign_bdd_tables, String geom, Postgis objPostgis) {
		super(doStrictQuery, maxResults, lang);
		_gazetteerName = "IGN";
		_ign_bdd_tables = ign_bdd_tables;
		_geom = geom;
		_objPostgis = objPostgis;
	}

	
	/**
	 * 
	 * @param toponymCandidate
	 */
	public Vector<Toponyme> searchToponym(String toponymCandidate) throws Exception
	{
		
		Vector<Toponyme> toponyms = new Vector<Toponyme>();
		//Vector<String> resJson = null;
		toponymCandidate = toponymCandidate.replaceAll("' ","'"); //on supprime l'espace dans la chaine d'origine
		
		if(_lang.equals("French")){
			
			_objPostgis.connect("BDnyme");
			
			for(int i=0;i<_ign_bdd_tables.length;i++){
		
				Statement state = _objPostgis.conn.createStatement();
				
				String ch2 = toponymCandidate.replaceAll("'","''"); //on double l'apostrophe pour la requete pgsql
				
				
				
				String stReq = "SELECT nom, ST_X("+_geom+"), ST_Y("+_geom+"), nature FROM "+_ign_bdd_tables[i]+" WHERE nom ~ E'.*"+ ch2.toLowerCase() + ".*' LIMIT "+_maxResults;
			
				ResultSet res = state.executeQuery(stReq);
								
				
				while(res.next())
				{ 
		
					String c = res.getString("nom");
					
					//System.err.println("result : "+c);
					if(_doStrictQuery){
						if(toponymCandidate.equalsIgnoreCase(c) || toponymCandidate.equalsIgnoreCase("le "+c) || toponymCandidate.equalsIgnoreCase("la "+c) || toponymCandidate.equalsIgnoreCase("les "+c) || c.equalsIgnoreCase("le "+toponymCandidate) || c.equalsIgnoreCase("la "+toponymCandidate) || c.equalsIgnoreCase("les "+toponymCandidate)){
							toponyms.add(new Toponyme(res.getString("nom"),toponymCandidate,res.getDouble("ST_X"),res.getDouble("ST_Y"),_gazetteerName,"FR","EU",res.getString("nature"),_lang));
						}
					}
					else
					{
						toponyms.add(new Toponyme(res.getString("nom"),toponymCandidate,res.getDouble("ST_X"),res.getDouble("ST_Y"),_gazetteerName,"FR","EU",res.getString("nature"),_lang));	
					}
				}
				res.close();
			
			}
			_objPostgis.close();
		}
		else
		{	
			ToponymArtifactFactory factory = null;
				
			if(_lang.equals("Italian"))
				factory = new ItalianToponymArtifactFactory();
			
			if(_lang.equals("Spanish"))
				factory = new SpanishToponymArtifactFactory();
			
			ToponymFinder finder = factory.makeToponymFinder(_maxResults);
		
			//finder.searchToponymInFile("data/spainInputData/anilloVerdeZaragoza.txt", "data/spainOutputData/anilloVerdeZaragozaResults.txt");
			//finder.searchToponymInFolder("data/spainInputData", "data/spainOutputData");
			
			List<ToponymInfo> res = finder.searchToponym(toponymCandidate,_doStrictQuery);
			if (res!=null) {
			    for (ToponymInfo toponym: res){
			    	toponyms.add(new Toponyme(toponym.getName(),toponymCandidate,Double.valueOf(toponym.getLongitude()),Double.valueOf(toponym.getLatitude()),_gazetteerName,toponym.getCountry(),toponym.getContinent(),toponym.getType(),_lang));
			    }
			}
		}
		
	
		
		return toponyms;
		
	}
	
	
	
	
	
}
