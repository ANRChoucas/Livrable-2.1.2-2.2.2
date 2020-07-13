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

package choucas.perdido.disambiguation;

import java.util.Vector;

import choucas.perdido.elements.Toponyme;



/**
 * Subtyping class : provides some methods for toponyms disambiguation using the subtyping method
 * @author Ludovic Moncla
 */
public class Subtyping {
	
	
	private static Vector<String> _termGeo = new Vector<String>();
	
	
	public Subtyping() 
	{
		
	}
	
	
	/**
	 * Returns true if the feature is geographical, interrogates ontologies to determine if a string is a geo feature
	 * @param feature
	 * @param lang
	 * @return true or false
	 */
	public static boolean isFeatureGeo(String feature, String lang)
	{
		//TODO interroger une ressource externe de type ontologie, ex DBPEDIA,...
		System.err.println("_termGeo.size = "+_termGeo.size());
		if(_termGeo.isEmpty())
			initTermGeo(lang);
		
		boolean isGeo = _termGeo.contains(feature.toLowerCase());
	

		return isGeo;
	}
	
	
	/**
	 * Returns true if the toponyms have the same feature
	 * @param toponyms
	 * @return true if the toponym in parameter has the same feature
	 */
	public static boolean isSameFeature(Vector<Toponyme> toponyms)
	{
	
		for(int i=0;i<toponyms.size();i++){
			if(toponyms.get(i).isSameFeature())
				return true;
		}
	
		return false;
	}
	
	
	/**
	 * 
	 * @param toponyms
	 * @param featureText
	 * @return
	 */
	public static Vector<Toponyme> checkFeature(Vector<Toponyme> toponyms, String featureText)
	{

		//System.out.println("Begin checkFeature : "+feature);
		
		//http://wiki.openstreetmap.org/wiki/Map_Features
	
		for(int i=0;i<toponyms.size();i++)
		{
			toponyms.get(i).setFeatureText(featureText);
			toponyms.get(i).setSameFeature(checkFeature(toponyms.get(i)));		
		}
	
		return toponyms;
	}
	
	
	/**
	 * Checks if the feature associated with the toponym is compliant with the feature associated in the gazetteer.
	 * @param toponym
	 * @return
	 */
	public static boolean checkFeature(Toponyme toponym)
	{
		
		String source = toponym.getSource();
		
		String featureGazetteer = toponym.getFeature();
		String feature = toponym.getFeatureText();
		String lang = toponym.getLang();
		
		
		if(source.equals("OpenStreetMap"))
		{
		
			if(featureGazetteer.equals("hamlet") || featureGazetteer.equals("city") || featureGazetteer.equals("village") || featureGazetteer.equals("town") || featureGazetteer.equals("administrative"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("bourg") || feature.contains("hameau") || feature.contains("ville") || feature.contains("village") || feature.contains("agglomération"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("refuge"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("refuge"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("water"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("lac") || featureGazetteer.equals("étang"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("path"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("pont"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("castle"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("château"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("road") || featureGazetteer.equals("motorway"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("route"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("peak"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("pic"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("arete"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("arête"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("river") || featureGazetteer.equals("stream"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("rivière") || feature.contains("fleuve"))
						return true;
				}
			}
			
			
			if(featureGazetteer.equals("alpine_hut"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("refuge"))
						return true;
				}
			}
			
	
			if(featureGazetteer.equals("place_of_worship"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("église") || feature.contains("cathédrale")|| feature.contains("chapelle") || feature.contains("mosquée")|| feature.contains("temple") || feature.contains("synagogue"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("theatre"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("théâtre"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("cinema"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("cinéma"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("fountain"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("fontaine"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("hospital"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("hôpital"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("parking"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("parking"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("hotel"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("hôtel"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("port"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("port"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("swimming_pool"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("piscine"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("school"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("école")  || feature.contains("collège")  || feature.contains("lycée"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("college"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("IUT")  || feature.contains("ESC"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("university"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("université")  || feature.contains("campus"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("canal"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("canal") )
						return true;
				}
			}
			
			if(featureGazetteer.equals("roundabout"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("rond-point") )
						return true;
				}
			}
			
			if(featureGazetteer.equals("primary") || featureGazetteer.equals("secondary") || featureGazetteer.equals("tertiary"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("rue") || featureGazetteer.equals("avenue") || featureGazetteer.equals("impasse") || featureGazetteer.equals("boulevard"))
						return true;
				}
			}
			
			if(featureGazetteer.equals("station"))
			{
				if(lang.equals("French"))
				{
					if(feature.contains("gare"))
						return true;
				}
			}
			
		}
		
		
		if(source.equals("National Gazetteer"))
		{
			if(lang.equals("French"))
			{
				if(featureGazetteer.equals("Commune") || featureGazetteer.equals("Canton") || featureGazetteer.equals("Préfecture") || featureGazetteer.equals("Lieu-dit habité"))
				{
					if(feature.contains("bourg") || feature.contains("hameau") || feature.contains("ville") || feature.contains("village") || feature.contains("agglomération") || feature.contains("préfecture"))
						return true;
				}
				
				if(featureGazetteer.equals("Moulin"))
				{
					
					if(feature.contains("moulin"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Refuge") || feature.contains("Grange"))
				{
					
					if(feature.contains("refuge"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Château"))
				{
					
					if(feature.contains("château"))
						return true;
					
				}
				
		
				if(featureGazetteer.equals("Camping"))
				{
					
					if(feature.contains("camping"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Monument"))
				{
					
					if(feature.contains("monument"))
						return true;
					
				}
				
				
				if(featureGazetteer.equals("Bois"))
				{
					
					if(feature.contains("bois") || feature.contains("forêt"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Lac") || featureGazetteer.equals("Point d'eau")  || featureGazetteer.equals("Marais"))
				{
					
					if(feature.contains("lac"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Glacier"))
				{
					
					if(feature.contains("glacier"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Canal"))
				{
					
					if(feature.contains("canal"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Vallée"))
				{
					
					if(feature.contains("vallée"))
						return true;
					
				}
				
				
				if(featureGazetteer.equals("Crête"))
				{
					
					if(feature.contains("arête") || feature.contains("crête"))
						return true;
				
				}
				
				if(featureGazetteer.equals("Versant"))
				{
					
					if(feature.contains("versant"))
						return true;
				
				}
				
		
				if(featureGazetteer.equals("Pont"))
				{
					
					if(feature.contains("pont"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Tunnel"))
				{
					
					if(feature.contains("tunnel"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Port"))
				{
					
					if(feature.contains("port"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Téléphérique"))
				{
					
					if(feature.contains("téléphérique"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Chemin") || featureGazetteer.equals("Infrastructure routière"))
				{
					
					if(feature.contains("route") || feature.contains("chemin"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Sommet"))
				{
					
					if(feature.contains("sommet"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Rochers"))
				{
					
					if(feature.contains("rocher"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Grotte"))
				{
					
					if(feature.contains("grotte"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Plaine"))
				{
					
					if(feature.contains("plaine"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Ile"))
				{
					
					if(feature.contains("île"))
						return true;
					
				}
				
				if(featureGazetteer.equals("Pic"))
				{
					
					if(feature.contains("pic"))
						return true;
				}
			}
		}
	
		
		return false;
	}
	
	
	
	/**
	 * Initializes the geographical terms. Must be replaced by an ontology
	 * @param lang
	 */
	public static void initTermGeo(String lang)
	{
		
		if(lang.equals("French"))
		{
			_termGeo.add("hôtel");
			_termGeo.add("bourg");
			_termGeo.add("bourgs");
			_termGeo.add("hameau");
			_termGeo.add("hameaux");
			_termGeo.add("refuge");
			_termGeo.add("pont");
			_termGeo.add("chalet");
			_termGeo.add("chalets");
			_termGeo.add("lac");
			_termGeo.add("col");
			_termGeo.add("torrent");
			_termGeo.add("rue");
			_termGeo.add("avenue");
			_termGeo.add("place");
			_termGeo.add("fontaine");
			_termGeo.add("village");
			_termGeo.add("villages");
			_termGeo.add("ville");
			_termGeo.add("chapelle");
			_termGeo.add("téléphérique");
			_termGeo.add("glacier");
			_termGeo.add("glaciers");
			_termGeo.add("dôme");
			_termGeo.add("église");
			_termGeo.add("camping");
			_termGeo.add("ruisseau");
			_termGeo.add("ru");
			_termGeo.add("auberge");
			_termGeo.add("vallon");
			_termGeo.add("arête");
			_termGeo.add("alpage");
			_termGeo.add("alpages");
			_termGeo.add("bois");
			_termGeo.add("massif");
			_termGeo.add("vallée");
			_termGeo.add("gare");
			_termGeo.add("rempart");
			_termGeo.add("remparts");
			_termGeo.add("quartier");
			_termGeo.add("écluse");
			_termGeo.add("parking");
			_termGeo.add("moulin");
			_termGeo.add("maison-phare");
			_termGeo.add("route");
			_termGeo.add("pointe");
			_termGeo.add("port");
			_termGeo.add("anse");
			_termGeo.add("étang");
			_termGeo.add("parc");
			_termGeo.add("fossé");
			_termGeo.add("chemin");
			_termGeo.add("château");
			_termGeo.add("sentier");
			_termGeo.add("forêt");
			_termGeo.add("sommet");
			_termGeo.add("sommets");
			_termGeo.add("ruine");
			_termGeo.add("canal");
			_termGeo.add("cimetière");
			_termGeo.add("mont");
			_termGeo.add("monts");
			_termGeo.add("abbaye");
			_termGeo.add("plaine");
			_termGeo.add("rocher");
			_termGeo.add("lieu-dit");
			_termGeo.add("ferme");
			_termGeo.add("département");
			_termGeo.add("grotte");
			_termGeo.add("montagne");
			_termGeo.add("montagnes");
			_termGeo.add("gouffre");
			_termGeo.add("évêché");
			_termGeo.add("commune");
			_termGeo.add("communes");
			_termGeo.add("piste");
			_termGeo.add("combe");
			_termGeo.add("phare");
			_termGeo.add("passage");
			_termGeo.add("faubourg");
			
			_termGeo.add("usine");
			_termGeo.add("garage");
			_termGeo.add("rive");
			_termGeo.add("rives");
			_termGeo.add("carrefour");
			_termGeo.add("intersection");
			_termGeo.add("chemin de fer");
			_termGeo.add("plan");
			_termGeo.add("chaîne");
			_termGeo.add("île");
			_termGeo.add("ria");
			_termGeo.add("maison");
			_termGeo.add("maisons");
			_termGeo.add("allée");
			_termGeo.add("aérodrome");
			_termGeo.add("dentelles");
			_termGeo.add("bâtisse");
			_termGeo.add("pays");
			_termGeo.add("lieu");
			_termGeo.add("bâtiment");
			_termGeo.add("domaine");
			_termGeo.add("faubourg");
			_termGeo.add("exploitation agricole");
			_termGeo.add("musée");
			_termGeo.add("croisement");
			
			//issu de pyrando
			_termGeo.add("thermes");
			_termGeo.add("cabane");
			_termGeo.add("barrage");
			_termGeo.add("embranchement");
			_termGeo.add("station de ski");
			_termGeo.add("rivière");
			_termGeo.add("gave");
			_termGeo.add("plateau");
			_termGeo.add("laquet");
			_termGeo.add("cascade");
			_termGeo.add("source");
			_termGeo.add("hôtellerie");
			_termGeo.add("promontoire");
			
			_termGeo.add("pic");
			_termGeo.add("versant");
			_termGeo.add("versants");
			_termGeo.add("territoire");
			_termGeo.add("gorge");
			_termGeo.add("escarpement");
			_termGeo.add("bassin");
			_termGeo.add("crête-mère");
			_termGeo.add("mines");
			_termGeo.add("terres");
			_termGeo.add("crête");
			_termGeo.add("rochers");
			_termGeo.add("trou");
			_termGeo.add("sources");
			_termGeo.add("zones");
			_termGeo.add("cîmes");
			_termGeo.add("plaines");
			_termGeo.add("hospice");
	
			_termGeo.add("cinéma");
			
			//issu de bassin de thau
			_termGeo.add("hinterland");
			_termGeo.add("incinérateur");
			_termGeo.add("base logistique");
			_termGeo.add("quai");
			_termGeo.add("piscine");
			_termGeo.add("école");
			_termGeo.add("jardin");
			_termGeo.add("région");
			_termGeo.add("rond-point");
			_termGeo.add("iut");
			_termGeo.add("agglo");
			_termGeo.add("tunnel");
			_termGeo.add("mer");
			_termGeo.add("office de tourisme");
			_termGeo.add("grand'rue");
			_termGeo.add("banque");
			_termGeo.add("canton");
		}
		
		
		if(lang.equals("Spanish"))
		{
			_termGeo.add("puente");
			_termGeo.add("recinto");
			_termGeo.add("parque");
			_termGeo.add("rio");
			_termGeo.add("río");
			_termGeo.add("basIlica");
			_termGeo.add("basílica");
			_termGeo.add("iglesia");
			_termGeo.add("orilla");
			_termGeo.add("canal");
			_termGeo.add("esclusas");
			_termGeo.add("barrio");
			_termGeo.add("avenida");
			_termGeo.add("localidad");
			_termGeo.add("barranco");
			_termGeo.add("valle");
			_termGeo.add("castillo");
			_termGeo.add("camino");
			_termGeo.add("embalse");
			_termGeo.add("acueducto");
			_termGeo.add("laguna");
			_termGeo.add("fuente");
			_termGeo.add("azud");
			_termGeo.add("pasarela");
			_termGeo.add("casa");
			_termGeo.add("santuario");
			_termGeo.add("carretera");
			_termGeo.add("cueva");
			_termGeo.add("rambla");
			_termGeo.add("estación");
			_termGeo.add("pueblo");
			_termGeo.add("ribera");
			
			_termGeo.add("olivar");
			_termGeo.add("molino");
			_termGeo.add("soto");
			_termGeo.add("esclusas");
			_termGeo.add("estacion");
			_termGeo.add("fonteta");
			_termGeo.add("ermita");
			_termGeo.add("comarca");
			_termGeo.add("mirador");
			_termGeo.add("arboleda");
			_termGeo.add("rambla");
			_termGeo.add("pasarela");
			_termGeo.add("pabellon");
			_termGeo.add("carretera");
			_termGeo.add("cueva");
			_termGeo.add("central");
			_termGeo.add("masia");
			_termGeo.add("hoz");
			_termGeo.add("catedral");
			_termGeo.add("convento");
			_termGeo.add("ex-convento");
			_termGeo.add("parroquia");
			_termGeo.add("despoblado");
			_termGeo.add("torre");
			_termGeo.add("zona");
			_termGeo.add("corral");
			_termGeo.add("mina");
			_termGeo.add("barranco");
			_termGeo.add("balsa");
			_termGeo.add("presa");
			_termGeo.add("cascada");
			_termGeo.add("iglesieta");
			_termGeo.add("acueducto");
			
		}
		
		if(lang.equals("Italian"))
		{
			_termGeo.add("città");
			_termGeo.add("rifugio");
			_termGeo.add("passo");
			_termGeo.add("cittadina");
			_termGeo.add("villaggio");
			_termGeo.add("prato");
			_termGeo.add("prati");
			_termGeo.add("croce");
			_termGeo.add("hotel");
			_termGeo.add("baita");
			_termGeo.add("parco");
			_termGeo.add("piste");
			_termGeo.add("albergo");
			_termGeo.add("torrente");
			_termGeo.add("masi");
			_termGeo.add("laghetto");
			_termGeo.add("castello");
			_termGeo.add("chiesa");
			_termGeo.add("chiesette");
			_termGeo.add("sentiero");
			_termGeo.add("via");
			_termGeo.add("edificio");
			_termGeo.add("posteggio");
			_termGeo.add("conca");
			_termGeo.add("maso");
			_termGeo.add("residenza");
			_termGeo.add("castle");
			_termGeo.add("paese");
			_termGeo.add("convento");
			_termGeo.add("cittadina");
			_termGeo.add("case");
			_termGeo.add("gola");
			_termGeo.add("habitato");
			_termGeo.add("cima");
			_termGeo.add("bosco");
			_termGeo.add("località");
			_termGeo.add("rovine");
			_termGeo.add("municipio");
			_termGeo.add("santuario");
			_termGeo.add("stazioni");
			_termGeo.add("avvallamento");
			_termGeo.add("baite");
			_termGeo.add("strada");
			_termGeo.add("ponte");
			_termGeo.add("ristorante");
			_termGeo.add("monte");
			_termGeo.add("lago");
			_termGeo.add("foce");
			_termGeo.add("versante");
			_termGeo.add("valle");
			_termGeo.add("cresta");
			_termGeo.add("squarci");
			_termGeo.add("costa");
			_termGeo.add("mont");
			_termGeo.add("abitato");
			_termGeo.add("castel");
			_termGeo.add("col");
			_termGeo.add("malga");
			
		}
		
		
	}
	
}
