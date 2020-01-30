/*
 * Copyright (C) 2016 Ludovic Moncla <ludovic.moncla@univ-pau.fr>
 * 
 * This file is part of POSprocessing - Perdido project <http://erig.univ-pau.fr/PERDIDO/>
 *
 * POSprocessing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * POSprocessing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with POSprocessing.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package choucas.perdido.postagger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;



/**
 * POStagger class : provides methods to execute POS taggers
 * @author Ludovic Moncla
 */
public abstract class POStagger {
	
	
	protected String _installDirectory = null;
	protected String _lang = null;
	protected String _POStaggerName = null;
	
	private Hashtable<String, String> _tags = new Hashtable<String, String>();
	
	/**
	 * 
	 * @param installDirectory
	 * @param lang
	 * @param POStaggerName
	 */
	public POStagger(String installDirectory, String lang, String POStaggerName)
	{
		_installDirectory = installDirectory;
		_lang = lang;
		_POStaggerName = POStaggerName;
		
		
	}
	
	/**
	 * 
	 * @param input
	 * @param outputFile
	 * @throws Exception
	 */
	public abstract void run(String input, String outputFile) throws Exception;
	
	
	
	/**
	 * 
	 * @param inputFile
	 * @throws Exception
	 */
	public abstract String tagger2pivot(String inputFile) throws Exception;

	
	

	/**
	 * load the list of POS tags
	 * @param uri 		path of the file containing the POS tags
	 */
	protected void loadTags() throws Exception
	{

		Class<?> currentClass = new Object() { }.getClass().getEnclosingClass();
		InputStream ips = currentClass.getResourceAsStream("/resources/Tag_"+_POStaggerName+"_"+_lang);
		
		
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
	}
	
	
	
	
	/**
	 * load the list of POS tags
	 * @param tag 		Hashtable of POS tags
	 */
	protected void loadTags(Hashtable<String, String> tag) throws Exception
	{
		_tags = tag;
	}

	
	/**
	 * get the list of POS tags
	 * @return Hashtable<String, String>		Hashtable of POS tags
	 */
	public Hashtable<String, String> getTags()
	{
		return _tags;
	}
	
	
	/**
	 * 
	 * @param inputFile
	 * @return String
	 * @throws Exception
	 */
	public String tagger2unitex(InputStreamReader inputFile) throws Exception {
		
		String line = "";
		String content = "";
		
		//InputStream ips = new FileInputStream(input);
		//InputStreamReader ipsr = new InputStreamReader(inputFile,"UTF-8");
		BufferedReader br = new BufferedReader(inputFile);
		
		while ((line = br.readLine()) != null) {	
			content += line + "\n";
		}
		
		br.close();
		
		return tagger2unitex(content);
	}

	
	/**
	 * tagger2unitex : turn pivot POS annotation into a unitex compliant format
	 * @param inputContent
	 * @return String
	 * @throws Exception
	 */
	public String tagger2unitex(String inputContent) throws Exception {
		
		
		this.loadTags();
		
		String result = "";
		String line[] = inputContent.split("\n");
		
		String token ="", lemma = "", pos = "";
		
		for(int i=0; i<line.length;i++) {
			
			
			String str[] = line[i].split("\t");

			
			token = str[0];
			pos = str[1];
			lemma = str[2].toLowerCase();
			
			//System.out.println("token : "+token);
			//System.out.println("pos   : "+pos);
			//System.out.println("lemma : "+lemma);
			
			//treetagger en anglais ne met de pas de POS pour la ponctuation à part la fin de phrase...
			if(pos.equals(",") || pos.equals(";") || pos.equals(":"))
				pos = "PUN";
			
			if(!_tags.get(pos).equals("null")) 
			{
				
			
				String st[]=_tags.get(pos).split(";");
				
				
				if(st!=null) 
				{	
					if(token.equals("-"))
						st[1] = "PUN";
					
					
					if(st[1].equals("PUN"))// || st[1].equals("SEN"))
					{
						result += token;
					}
					else
					{
						result += "{";
						
						result += token+",";
						if(!lemma.equals("") && !lemma.equals(".") && !lemma.equals("null") && !lemma.equals("_"))
							result += lemma;
						result +="."+st[1];
						
						result += "} ";
					}
				}
			}
			else
			{
				System.err.println("Unknown tag");
			}

		}

		
		return result;
		
	}

}

