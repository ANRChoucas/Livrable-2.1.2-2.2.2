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
public class LandscapesTerms2UnitexBox  {

	//private static Hashtable<String, String> _tags = new Hashtable<String, String>();
	
	public static void main(String[] args) throws Exception
	{
		
		
		
		
		
		
			
		String file = "/Users/lmoncla/YACCA/test/landscapes/landscapes.txt";
			String output = "/Users/lmoncla/YACCA/test/landscapes/landscapes_unitex.txt";
			//String content = "";
			FileTools.createFile(output,"");
			
			try {
				
				
					String content = FileTools.getContent(file);
					
					System.out.println("before : "+content);
					
					content = content.replaceAll("( ){2,}", " ");
					content = content.replaceAll(" ", ">+<");
					content = "<"+content+">";
					
					System.out.println("after : "+content);
					
					//System.out.println(content_tmp);
					
					//content += content_tmp;
					FileTools.updateFileTxt(output,content);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		

		
	
	}
}
