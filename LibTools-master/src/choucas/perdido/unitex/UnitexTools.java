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


package choucas.perdido.unitex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import choucas.perdido.tools.FileTools;



/**
 * Annotation class : provide methods to execute POS taggers and cascade of transducers (Cassys)
 * @author Ludovic Moncla
 * @version 1.0
 */
public class UnitexTools {
	
	/**
	 * transform the output of the cascade of tranducer into XML format
	 * @param input			
	 * @param output
	 * @throws Exception
	 */
	public void unitex2xml(String input, String output) {
	
		//System.out.println(" ** Debut unitex2xml **");
		try{

			String resultXML = "<?xml version='1.0' encoding='UTF-8'?><text><sentence id=\"n1\" xml:id=\"d1p1s1\">"; // XML
			
			InputStream ips = new FileInputStream(input);
	
			InputStreamReader ipsr = new InputStreamReader(ips, "UTF-8");
			BufferedReader br = new BufferedReader(ipsr);
			int cpt = 0;
			String ligne = "";
			while ((ligne = br.readLine()) != null) {
				String line = new String(ligne.getBytes(), "UTF-8");
				String sub = "";
				
				line = line.replaceAll("ÿþ", "");
				/*
				if(cpt==0)
				{
					sub = line.substring(2); //supprime les 2 premiers caractères, pour corriger encodage sortie unitex
					cpt++;
				}
				else*/
					sub = line;
	
				resultXML += sub;
			}
			
			resultXML = resultXML.replaceAll("\0", "");
			resultXML += "</sentence></text>";
		
			//on transforme le fichier txt issu d'unitex au format xml
			FileTools.createFile(output, resultXML);
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
		}
		//System.out.println(" ** Fin unitex2xml **");
	}



	/**
	 * transform the output of the cascade of tranducer into XML format
	 * @param inputFile			
	 * @param outputFile
	 * @throws Exception
	 */
	public static void txt2tei(String inputFile, String outputFile) {
	
		//System.out.println(" ** Debut unitex2tei **");
		try{
	
			String resultXML = "<?xml version='1.0' encoding='UTF-8'?><TEI xmlns='http://www.tei-c.org/ns/1.0\'><s>"; // XML
			
			resultXML += txt2xmlLike(inputFile);
			
			
			resultXML = resultXML.replaceAll("\0", "");
			resultXML += "</s></TEI>";
		
			//on transforme le fichier txt issu d'unitex au format xml
			FileTools.createFile(outputFile, resultXML);
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
		}
		//System.out.println(" ** Fin unitex2tei **");
	}
		
	
	public static String txt2xmlLike(String inputFile) {
		
		String resultXML = "";//"<?xml version='1.0' encoding='UTF-8'?><TEI xmlns='http://www.tei-c.org/ns/1.0\'><s>"; // XML
		
		//System.out.println(" ** Debut unitex2tei **");
		try{
	
			
			InputStream ips = new FileInputStream(inputFile);
		
			InputStreamReader ipsr = new InputStreamReader(ips, "UTF-8");
			BufferedReader br = new BufferedReader(ipsr);
			int cpt = 0;
			String ligne = "";
			while ((ligne = br.readLine()) != null) {
				String line = new String(ligne.getBytes(), "UTF-8");
				String sub = "";
				
				
				line = line.replaceAll("ÿþ", "");
				/*
				if(cpt==0)
				{
					sub = line.substring(2); //supprime les 2 premiers caractère, pour corriger encodage sortie unitex
					cpt++;
				}
				else
				*/
				sub = line;
		
				resultXML += sub;
			}
			
			//resultXML = resultXML.replaceAll("\0", "");
			//resultXML += "</s></TEI>";
		
			//on transforme le fichier txt issu d'unitex au format xml
			//FileTools.createFile(outputFile, resultXML);
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
		}
		//System.out.println(" ** Fin unitex2tei **");
		
		return resultXML;
	}
	

}
