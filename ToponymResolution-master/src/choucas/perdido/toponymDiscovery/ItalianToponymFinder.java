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

import java.util.List;
import java.util.Scanner;

import org.w3c.dom.Node;

/** 
 * 
 * @author Javier Nogueras
 * ToponymFinder specialized for the Italian Gazetteer
 */
public class ItalianToponymFinder extends ToponymFinder {

	public ItalianToponymFinder(WFSClient wfsClient) {
		super(wfsClient);
	}

	protected String getToponymNodeName() {
		return "ms:NG.TOPONIMI.";
	}
	
	protected String getName(Node input) {
		String result = null;

		Node name = firstChildNode(input,"ms:toponimo");
		if (name!=null) result= name.getTextContent();

		return result;
	}
	
	protected  String getLatitude(Node input) {
		String result = null;
		Node boundary = firstChildNode(input,"ms:boundary");
		if (boundary!=null) {
			Node point = firstChildNode(boundary,"gml:Point");
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
		Node boundary = firstChildNode(input,"ms:boundary");
		if (boundary!=null) {
			Node point = firstChildNode(boundary,"gml:Point");
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
		return "IT";
	}
	
	protected String getContinent(Node input) {
		return "EU";
	}

	protected String getType(Node input) {
		Node typeNode = firstChildNode(input, "ms:tipo");
		if (typeNode!=null)
			return typeNode.getTextContent();
		else return null;
	}
	
	protected  String getLocalType(Node input) {
		String result = null;

		Node localType = firstChildNode(input,"ms:oggetto_toponimo");
		if (localType!=null) 
			result= localType.getTextContent();

		return result;
	}
	
	/** 
	 * Search the toponyms associated with a toponym name
	 * @param toponymName name of the toponym to look up
	 * @return List of toponyms
	 * @see ToponymInfo
	 */
	@Override
	public List<ToponymInfo> searchToponym(String toponymName,boolean strictQuery){
		
		String featureCollection = _wfsClient.getFeaturesGet(toponymName,strictQuery);
		//System.out.println(featureCollection);
		
		List<ToponymInfo> toponymInfoList = processFeatureCollection(featureCollection);
		
		// Take into account that we should check the number of features returned by WFS. Currently, we only process 10 as maximum
		if (toponymInfoList.size()==_wfsClient.getMaxResults()){
			System.err.println("Warning: "+ toponymInfoList.size() + " results (the maximum) reached with toponym "+toponymName);
		}
	
		return toponymInfoList;
	}
	

}
