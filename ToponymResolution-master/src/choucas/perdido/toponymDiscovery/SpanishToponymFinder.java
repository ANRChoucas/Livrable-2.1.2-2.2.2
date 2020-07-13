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

import org.w3c.dom.Node;
import java.util.Scanner;

/** 
 * 
 * @author Javier Nogueras
 * ToponymFinder specialized for the Spanish Gazetteer
 */
public class SpanishToponymFinder extends ToponymFinder {

	public SpanishToponymFinder(WFSClient wfsClient) {
		super(wfsClient);
	}
	
	protected String getToponymNodeName() {
		return "gn:NamedPlace";
	}
	
	protected String getName(Node input) {
		String result = null;

		Node name = firstChildNode(input,"gn:name");
		if (name!=null) {
			Node geographicalName = firstChildNode(name,"gn:GeographicalName");
			if (geographicalName!=null) {
				Node spelling = firstChildNode(geographicalName,"gn:spelling");
				if (spelling!=null) {
					Node spellingOfName = firstChildNode(spelling,"gn:SpellingOfName");
					if (spellingOfName!=null) {
						Node text = firstChildNode(spellingOfName,"gn:text");
						if (text!=null) result= text.getTextContent();
					}
				}
			}
		}

		return result;
	}
	
	protected  String getLatitude(Node input) {
		String result = null;
		Node geometry = firstChildNode(input,"gn:geometry");
		if (geometry!=null) {
			Node point = firstChildNode(geometry,"gml:Point");
			if (point!=null) {
				Node pos = firstChildNode(point,"gml:pos");
				String text = pos.getTextContent();
				if (text!=null) {
					Scanner scanner = new Scanner(text);
					if (scanner.hasNext())
						result = scanner.next();
				}
			}
		}

		return result;
	}

	protected  String getLongitude(Node input) {
		String result = null;
		Node geometry = firstChildNode(input,"gn:geometry");
		if (geometry!=null) {
			Node point = firstChildNode(geometry,"gml:Point");
			if (point!=null) {
				Node pos = firstChildNode(point,"gml:pos");
				String text = pos.getTextContent();
				if (text!=null) {
					Scanner scanner = new Scanner(text);
					if (scanner.hasNext()) {
						scanner.next();
						if (scanner.hasNext())
							result = scanner.next();
					}
				}
			}
		}

		return result;
	}
	
	protected String getCountry(Node input) {
		return "SP";
	}
	
	protected String getContinent(Node input) {
		return "EU";
	}

	protected String getType(Node input) {
		Node typeNode = firstChildNode(input, "gn:type");
		if (typeNode!=null)
			return typeNode.getTextContent();
		else return null;
	}
	
	protected  String getLocalType(Node input) {
		String result = null;

		Node localType = firstChildNode(input,"gn:localType");
		if (localType!=null) {
			Node localisedCharacterString = firstChildNode(localType,"gmd:LocalisedCharacterString");
			if (localisedCharacterString!=null) result= localisedCharacterString.getTextContent();
		}

		return result;
	}
	

	
	

}
