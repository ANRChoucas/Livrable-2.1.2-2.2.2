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
 * Main class showing how to employ the factory of toponym artifacts and 
 * launch the discovery of a set of toponyms
 * @author Javier Nogueras
 *
 */
public class Main {
	
	  public static void main(String[] args) {
		  ToponymArtifactFactory factory;
		  factory = new SpanishToponymArtifactFactory();
		  ToponymFinder finder = factory.makeToponymFinder(100);
		  System.out.println(finder.searchToponym("Somport",false));
		  
		 
		  //finder.searchToponymInFile("data/spainInputData/anilloVerdeZaragoza.txt", "data/spainOutputData/anilloVerdeZaragozaResults.txt");
		  
		  //finder.searchToponymInFolder("data/spainInputData", "data/spainOutputData");
		  
		    factory = new ItalianToponymArtifactFactory();
		    finder = factory.makeToponymFinder(100);
			System.out.println(finder.searchToponym("Milano",false));
	}


}
