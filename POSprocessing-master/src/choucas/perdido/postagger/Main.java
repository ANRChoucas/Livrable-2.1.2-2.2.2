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


/**
 * This class shows a simple example of how we can use the POSTagger class
 * @author lmoncla
 *
 */
public class Main {

	 public static void main(String[] args) {
		 
		 String tagger = "stanfordNLP";
		 String URIPOStagger = ""; // add the URI to the install directory of the POS tagger
		 String lang = "English";
		 
		 String URIOutputFile = "/Users/lmoncla/Programme/stanford-postagger-full/sample-outputTest.txt"; // add the URI of the output file
		 
		 
		 
		 POStagger pos = null;
		 
		 
		 
		 if(tagger.equals("stanfordNLP"))
		 {
		 	if(lang.equals("English"))
		 		URIPOStagger = "/Users/lmoncla/Programme/stanford-postagger-full/models/english-left3words-distsim.tagger";
			
			if(lang.equals("French"))
				URIPOStagger = "/Users/lmoncla/Programme/stanford-postagger-full/models/french.tagger";
			
			if(lang.equals("Spanish"))
				URIPOStagger = "/Users/lmoncla/Programme/stanford-postagger-full/models/spanish.tagger";
		 
			 pos = new StanfordNLP(URIPOStagger,lang);
		 }
		 

		 if(tagger.equals("treetagger"))
			 pos = new Treetagger(URIPOStagger,lang);
		 
		//pos = new Freeling(URIPOStagger,lang);
		//pos = new Talismane(URIPOStagger,lang);
		//
			
		
			
		
		try {
			

			pos.run("/Users/lmoncla/Programme/stanford-postagger-full/sample-input.txt", URIOutputFile);
			
			
			
			//pos.run("Ceci est un test", URIOutputFile);
			
			//create the string containing the result of the POS tagging with the pivot tags
			String pivot = pos.tagger2pivot(URIOutputFile);
			
			
			System.out.println("pivot : " +pivot);
			
			
			//turn the result into the Unitex format
			String result = pos.tagger2unitex(pivot);
		
			
			System.out.println("result : " +result);
			//result : {Ceci,ceci.PRO+Pdem} {est,être.V} {un,un.DET+ART} {test,test.N} 
			
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
			
	 }
}
