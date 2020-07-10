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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
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
public class AlpineJournal2Unitex  {

	private static Hashtable<String, String> _tags = new Hashtable<String, String>();
	
	public static void main(String[] args) throws Exception
	{
		
		
		Class<?> currentClass = new Object() { }.getClass().getEnclosingClass();
		InputStream ips = currentClass.getResourceAsStream("/resources/Tag_treetagger_English");
		
		InputStreamReader ipsr = new InputStreamReader(ips,"UTF-8");
		BufferedReader br = new BufferedReader(ipsr);
		
		String line = "";
		
		while ((line = br.readLine()) != null) 
		{
			String str[] = line.split(";");
			
			if(!str[1].equals("null"))
				_tags.put(str[0], str[1]+";"+str[2]);
			else
				_tags.put(str[0], str[1]);	
		}
		
		
		
		
			
			
		ips.close();	 
		
	
		String folder = "/Users/lmoncla/YACCA/test/test/";
		File fl[] = FileTools.listFiles(folder);
	
		for (int j = 0; j < fl.length; j++) {
			
			String fileName = "";
			String extension = "";
			
			//String content = "";
			FileTools.createFile(folder+fileName+".txt","");
			
			try {
				fileName = StringTools.getNameWithoutExtention(fl[j].getName().toString());
				extension = StringTools.getExtension(fl[j].getName().toString());
			
				System.out.println("filename : "+fileName);
				
				if(extension.equals(".xml"))
				{
				
				SAXBuilder sxb = new SAXBuilder();
				Document document = sxb.build(new File(folder+fileName+extension));
				
				
				
				Iterator<Element> it = document.getRootElement().getDescendants(new ElementFilter("w", null )); //Gets the requested elements. 
				
				
				while (it.hasNext())
				{
					
					Element eltName = (Element) it.next();
					
					String content = "";
					
					//System.out.println("pos : "+eltName.getAttributeValue("pos"));
					
					
					
					//System.out.println("pos : "+eltName.getAttributeValue("pos"));
				//	if(eltName.getAttributeValue("pos").equals(",") || eltName.getAttributeValue("pos").equals("(") || eltName.getAttributeValue("pos").equals(")") || eltName.getAttributeValue("pos").equals(":") || eltName.getAttributeValue("pos").equals(";"))
					if(eltName.getValue().equals("."))
					{
						content += "{"+eltName.getValue()+",.SEN} ";
					}
					else
					{
						try{	
							if(!_tags.get(eltName.getAttributeValue("pos")).equals("null")) 
							{
							
								String st[]=_tags.get(eltName.getAttributeValue("pos")).split(";");
							
								String lemma = eltName.getAttributeValue("lemma");
								lemma = lemma.replaceAll(".", "");
								
								content += "{";
								content += eltName.getValue();
								content += ",";
								content += lemma;
								content += ".";
								content += st[1]; //pos
								content += "} "; 
							}
						}
						catch(Exception e){
							content += eltName.getValue()+" ";
						}
					}
					
					
					
					//System.out.println(content_tmp);
					
					//content += content_tmp;
					FileTools.updateFileTxt(folder+fileName+".txt",content);
				}
				
				//System.out.println("content : "+content);
				
				
				
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		

		}
	
	}
}
