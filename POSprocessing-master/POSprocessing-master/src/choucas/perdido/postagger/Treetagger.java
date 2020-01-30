/*
 * Copyright (C) 2016 Ludovic Moncla <ludovic.moncla@univ-pau.fr>
 * 
 * This file is part of POSprocessing - Choucas-Perdido project <http://erig.univ-pau.fr/PERDIDO/>
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


import choucas.perdido.tools.StringTools;


/**
 * Treetagger class : provides methods to execute Treetagger POS taggers
 * @author Ludovic Moncla
 * @version 1.0
 */
public class Treetagger extends POStagger {
	
	

	/**
	 * 
	 * @param installDirectory
	 * @param lang
	 */
	public Treetagger(String installDirectory, String lang)
	{
		super(installDirectory, lang, "treetagger");
	}
	
	
	
	/**
	 * launch the Treetagger POS analyser
	 * @param inputContent				content to tag
	 * @param outputFile				path of the output file
	 * @throws Exception
	 */
	@Override
	public void run(String inputContent, String outputFile) throws Exception {

	
		Runtime runtime = Runtime.getRuntime();
		Process proc;

		String cmd_treetagger = _installDirectory + "/cmd/tree-tagger-"+_lang.toLowerCase()+" > " + outputFile;
			
		String[] cmd = {
			"bash",
			"-c",
			"echo \"" + inputContent + "\" | " +  cmd_treetagger};
		
		proc = runtime.exec(cmd);
		proc.waitFor();

	
	}
	
	
	/**
	 * 
	 * @param inputFile				path of the input file
	 * 
	 */
	@Override
	public String tagger2pivot(String inputFile) throws Exception {

		
		InputStream ips = new FileInputStream(inputFile);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);

		
		String outputPivot = "";
		String line = "";
		String token = "", pos = "", lemma = "";
		
		int BUFFER_SIZE = 1000;

		while ((line = br.readLine()) != null) 
		{
			if(!line.equals(""))
			{
				String str[] = line.split("\t");
	
				
				token = str[0];// .toLowerCase();
				pos = str[1];
				
				
				if(str.length>2) //if a lemma exists
				{
					if(str[2] != null  && !str[2].equals("<unknown>"))
					{
						String lem_tmp = str[2];
						//System.err.println("lem_tmp : " + lem_tmp);
						
						
						if(lem_tmp.contains("|")) //if several lemma, we choose the first one
						{
							
							//String c[] = lem_tmp.split("|"); // problème au niveau du split...
							//System.out.println("c[0] : " + c[0]);
							//System.out.println("c[1] : " + c[1]);
							//lemma = c[0];
							
							//System.out.println("lem "+lem_tmp.substring(0, lem_tmp.indexOf("|")));
							lemma = lem_tmp.substring(0, lem_tmp.indexOf("|"));
							
							
							//lemma = str[2];
						}
						else
						{
							//System.out.println("else");
							lemma = str[2];
						}
					}
					else
						lemma = "null";
				}
				else
					lemma = "null";
				
				
				//System.err.println("lemma : " + lemma);
				
				token = StringTools.filtreString(token);
				lemma = StringTools.filtreString(lemma);
				
				if(token.equals("M") || token.equals("Mr") || token.equals("Mme") || token.equals("Mlle") || token.equals("Mm") || token.equals("MM") || token.equals("Dr") || token.equals("Pr"))
				{
					br.mark(BUFFER_SIZE);
					String nextLine = br.readLine(); // read the next line
					if(nextLine != null)
					{
						String str2[] = nextLine.split("\t");
						
						
						String t = str2[0]; // token
						
						if(t.equals("."))
						{
							token = token+".";
						}
						else
						{
							br.reset();  //go back to the previous line
						}
					}
					
				}
				
				outputPivot += token + "\t" + pos + "\t" + lemma +"\n";
				
			}
		}
		
		br.close();
	
		return outputPivot;
	}

}


