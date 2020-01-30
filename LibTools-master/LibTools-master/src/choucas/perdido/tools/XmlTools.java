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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

import choucas.perdido.elements.Verb;




/**
 * XmlTools class, provides some methods to use XML with Perdido
 * @author Ludovic Moncla
 */
public class XmlTools  {

	
	
	/**
	 * Return the value of the element named "term" with the attribut type equal to N
	 * @param elt
	 * @return
	 */
	public static String getTermN(Element elt)
	{
		//System.out.println(". Debut getTermN ");		
		//Element e = elt.getChild("w");  //w @lemma @type @subtype 
		String term = "";
		
		List<?> l = elt.getChildren(); // en précisant w ça ne fonctionne plus
		
		//l = elt.getChildren();
		
		Iterator<?> i = l.iterator();
		while (i.hasNext()) {
			
			Element e = (Element) i.next();
			//System.out.println("[Debug] 1");	
			//on vérifie qu'il s'agit d'un verbe
			if(e.getName().equals("term") && e.getAttributeValue("type").equals("N"))
			{
				
				term = XmlTools.getValueEltTei(e);

				//System.out.println(". Fin getTermN ");
				return term;
			}
		}
		
		//System.out.println(". Fin getTermN ");	
		
		return null;
	}
	
	public static Verb getVerbTei(Element elt)
	{
		//System.out.println(". Debut getVerb ");		
		//Element e = elt.getChild("w");  //w @lemma @type @subtype 
		Verb v = null;
		
		List<?> l = elt.getChildren(); // en précisant w ça ne fonctionne plus
		
		//l = elt.getChildren();
		
		Iterator<?> i = l.iterator();
		while (i.hasNext()) {
			
			Element e = (Element) i.next();
			//System.out.println("[Debug] 1");	
			//on vérifie qu'il s'agit d'un verbe
			if(e.getName().equals("w") && e.getAttributeValue("type").equals("V"))
			{
				//System.out.println("[Debug] 2");	
				String lemma = e.getAttributeValue("lemma");
				String type = e.getAttributeValue("subtype"); 
				//motion_initial, motion_median, motion_final, topographic, perception
				String polarity = null;
				
				if(type.equals("motion_initial"))
				{
					polarity = "initial";
					type = "motion";
				}
				if(type.equals("motion_median"))
				{
					polarity = "median";
					type = "motion";
				}
				if(type.equals("motion_final"))
				{
					polarity = "final";
					type = "motion";
				}
				
				v = new Verb(e.getValue(),type,polarity,lemma);
				//System.out.println(". Fin getVerb ");
				return v;
			}
		}
		
		//System.out.println(". Fin getVerb ");	
		
		return v;
	}
	
	public static Element getEltName(Element elt, String name)
	{
		 Element parentElt = elt.getParentElement();
		
		 if(parentElt != null)
		 {
			if(parentElt.getName().equals(name))
			{
				return parentElt;
			}
			else
			{
				return getParentElt(parentElt,name);
			}
		 }
		
		 return null;
	}
	
	
	
	/**
	 * 
	 * @param elt
	 * @param name
	 * @return
	 */
	public static Element getParentElt(Element elt, String name)
	{
		Element parentElt = elt.getParentElement();
		
		if(parentElt != null)
		{
			//System.out.println("parentName : "+parentElt.getName());	
			if(parentElt.getName().equals(name))
			{
				return parentElt;
			}
			else
			{
				return getParentElt(parentElt,name);
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param elt
	 * @param name
	 * @return
	 */
	public int countElt(Element elt, String name) {
		int count = 0;

		if (name.equals(elt.getName())) {
			count++;
		} else {
			List<?> listLss = elt.getChildren();
			Iterator<?> i = listLss.iterator();
			while (i.hasNext()) {
				Element courant = (Element) i.next();

				count += countElt(courant, name);

			}
		}
		return count;
	}
	
	

	/**
	 * 
	 * @param elt
	 * @return
	 */
	public String getValueElt_V5(Element elt) {
		String value = "";

		List<?> listLss = elt.getChildren();
		Iterator<?> i = listLss.iterator();
		value = elt.getText();
		//value = elt.getValue();
		while (i.hasNext()) {
			
			Element child = (Element) i.next();
			//if (child.getName().equals("token")) {
			//	value += child.getValue() + " ";
			//} else {
				//value += " ";
				value += " "+getValueElt_V5(child);
			//}

		}

		return value;
	}

	public static String getValueElt(Element elt) {
		String value = "";

		List<?> listLss = elt.getChildren();
		Iterator<?> i = listLss.iterator();

		while (i.hasNext()) {
			Element child = (Element) i.next();
			if (child.getName().equals("token")) {
				value += child.getValue() + " ";
			} else {
				value += getValueElt(child) + " ";
			}

		}

		return value;
	}
	
	
	public static String getValueElt(Element elt, String nameElt) {
		String value = "";

		List<?> listLss = elt.getChildren();
		Iterator<?> i = listLss.iterator();

		while (i.hasNext()) {
			Element child = (Element) i.next();
			if (child.getName().equals(nameElt)) {
				return child.getValue();
			} else {
				getValueElt(child,nameElt);
			}

		}

		return value;
	}
	
	
	public static String getValueEltTei(Element elt) {
		String value = "";

		//List listLss = elt.getChildren();
		//Iterator i = listLss.iterator();
		
		Iterator<Element> i = elt.getDescendants(new ElementFilter("w", null ));

		while (i.hasNext()) {
			Element child = (Element) i.next();
			
			value += child.getValue() + " ";
			/*
			if (child.getName().equals("w")) {
				value += child.getValue() + " ";
			} else {
				value += getValueEltTei(child) + " ";
			}*/

		}

		return value;
	}
	
	
	
	public static String getValueTermTei(Element elt) {
		String value = "";

		//List listLss = elt.getChildren();
		//Iterator i = listLss.iterator();
		
		Iterator<Element> i = elt.getDescendants(new ElementFilter("w", null ));

		while (i.hasNext()) {
			Element child = (Element) i.next();
			
			if(child.getAttributeValue("type").equals("N") || child.getAttributeValue("type").equals("PREP"))
				value += child.getValue() + " ";
			/*
			if (child.getName().equals("w")) {
				value += child.getValue() + " ";
			} else {
				value += getValueEltTei(child) + " ";
			}*/

		}
		
		value = value.trim();

		return value;
	}
	
	
	public static String getValueGeogNameTei(Element elt) {
		String value = "";

		//List listLss = elt.getChildren();
		//Iterator i = listLss.iterator();
		
		Iterator<Element> i = elt.getDescendants(new ElementFilter("w", null ));

		while (i.hasNext()) {
			Element child = (Element) i.next();
			
			if(!child.getAttributeValue("type").equals("A"))
				value += child.getValue() + " ";
			/*
			if (child.getName().equals("w")) {
				value += child.getValue() + " ";
			} else {
				value += getValueEltTei(child) + " ";
			}*/

		}
		
		value = value.trim();

		return value;
	}
	
	
	public static String getValueTermTeiLemma(Element elt) {
		String value = "";

		//List listLss = elt.getChildren();
		//Iterator i = listLss.iterator();
		
		Iterator<Element> i = elt.getDescendants(new ElementFilter("w", null ));

		while (i.hasNext()) {
			Element child = (Element) i.next();
			
			if(child.getAttributeValue("type").equals("N") || child.getAttributeValue("type").equals("PREP"))
			{
				if(child.getAttributeValue("lemma") != null)
				{
					if(!value.equals(""))
						value += " ";
					value += child.getAttributeValue("lemma");
				}
			}

		}
		
		value = value.trim();

		return value;
	}
	
	
	public static String getContent(String filePath, String eltName) throws Exception
	{
		String content = "";
		try
		{
			SAXBuilder sxb = new SAXBuilder();
			
			Document document = sxb.build(new File(filePath));
			
			Element racine = document.getRootElement();
			
			List<?> listLss = racine.getChildren(eltName);
			
			//racine.getCh
	
			Iterator<?> i = listLss.iterator();
			while (i.hasNext()) 
			{
			
				Element courant = (Element) i.next();
				//System.err.println("elt : "+courant.getName());
				content = courant.getText();
			}
			
		
		} catch (Exception e) { }	
		
		return content;
	}
	
	
	public static String getContentElt(String filePath, String eltName) throws Exception
	{
		String content = "";
		try
		{
			SAXBuilder sxb = new SAXBuilder();
			
			Document document = sxb.build(new File(filePath));
			
			Element racine = document.getRootElement();
			
			List<?> listLss = racine.getChildren();
			
			//racine.getCh
	
			Iterator<?> i = listLss.iterator();
			while (i.hasNext()) 
			{
			
				Element courant = (Element) i.next();
				//System.err.println("elt : "+courant.getName());
				
				content = getValueElt(courant,eltName);
				//System.err.println("content : "+content);
				
				if(!content.equals(""))
				{
					return content;
				}
				//content = courant.getText();
			}
			
		
		} catch (Exception e) { }	
		
		return content;
	}
	
	
	
	public static String getMsgXML(String root, String msg)
	{
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><"+root+">"+msg+"</"+root+">";
	}
	
	
	public static String eltStatusXML(String err)
	{
		return "<status message=\""+err+"\" />";
	}
	
	
	

	
	
}
