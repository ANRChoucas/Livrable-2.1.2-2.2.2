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

package choucas.perdido.toponymDiscovery;

/** 
 * A factory specialized in the Spanish gazetteer
 */
public class SpanishToponymArtifactFactory extends ToponymArtifactFactory {

	private static String spanishWfsURL = "http://www.ign.es/wfs-inspire/ngbe"; 
	private static String spanishTypeName ="gn:NamedPlace";
	private static String spanishPropertyName = "gn:name/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text";
	private static String spanishNameSpaceGet = "gn=urn:x-inspire:specification:gmlas:GeographicalNames:3.0";
	private static String spanishNameSpacePost = "gn=\"urn:x-inspire:specification:gmlas:GeographicalNames:3.0\"";
	
	@Override
	public WFSClient makeWFSClient(int maxResults) {
		WFSClient wfs = new WFSClient (spanishWfsURL,spanishTypeName, spanishPropertyName
				,spanishNameSpaceGet,spanishNameSpacePost,"1.1.0","gml/3.2.1",maxResults);
		return wfs;
	}

	@Override
	public ToponymFinder makeToponymFinder(int maxResults) {
		
		WFSClient wfs = makeWFSClient(maxResults);
		ToponymFinder finder = new SpanishToponymFinder(wfs);
		return finder;
	}

}
