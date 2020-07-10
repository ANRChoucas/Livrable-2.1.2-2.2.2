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

import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

/**
 * 
 * @author lmoncla
 *
 */
public class Sparql
{
	
	public static void main(String[] args) {
	
		System.out.println("main ");
		
		String prefix = ""
				+ "PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX geo:     <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
                + "PREFIX dbo:     <http://dbpedia.org/ontology/>"
                + "\n";
		String name = "Morning-Chronicle";
		
		
		try {
			
			String query = prefix+ "ASK WHERE {?x rdf:type foaf:Person . " +
					"{ ?x rdfs:label '"+name+"'@fr } union { ?x rdfs:label '"+name+"'@en } }";
			
			boolean result = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query).execAsk();
			
			
	        
	        if(result)
	        { 
	        	System.out.println(name+" est une personne");
	        }
	        else
	        {
	        	System.out.println(name+" n'est pas une personne");
	        	
	        	String query2 = prefix+ "ASK WHERE {?x rdf:type dbo:Location . " +
						"{ ?x rdfs:label '"+name+"'@fr } union { ?x rdfs:label '"+name+"'@en } }";
	        	
	        	boolean result2 = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query2).execAsk();
	        	
	        	if(result2)
		        { 
		        	System.out.println(name+" est un lieu");
		        	
		        	
		        	/*
		        	String qs3 = prefix+ "SELECT ?lat ?long WHERE {?x rdfs:label '"+en+"'@fr . ?x geo:lat ?lat . ?x geo:long ?long}";

		        	ResultSet results  = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", qs2).execSelect();
		        	
		        	//ResultSet results = exec. ;
		        	for ( ; results.hasNext() ; )
		            {
		        	
		              QuerySolution solution = results.next();
		              Literal latitude = solution.getLiteral( "?lat" );
		              Literal longitude = solution.getLiteral( "?long" );

		             // String sLat = latitude.getLexicalForm();
		              //String sLon = longitude.getLexicalForm();

		              float fLat = latitude.getFloat();
		              float fLon = longitude.getFloat();

		              
		              System.out.println(en + ": lat: " + fLat + " long: " + fLon );
		              
		            }
		        	*/
		        	
		        }
		        else
		        {
		        	System.out.println(name+" n'est pas un lieu");
		        	
		       
		        	//optimisation mettre ca en une seule requete avec la query1 ou avec OPTIONAL
		        	String query3 = prefix+ "ASK WHERE {?x dbo:wikiPageDisambiguates ?y . " +
		        			//"{ ?y rdfs:label '"+name+" (name)'@fr} union  {?y rdfs:label '"+name+" (surname)'@fr}" +
		        			"{ ?y rdfs:label '"+name+" (name)'@en} union {?y rdfs:label '"+name+" (surname)'@en} . " +
							"{ ?x rdfs:label '"+name+"'@fr } union { ?x rdfs:label '"+name+"'@en } }";
		        	
		        	boolean result3 = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query3).execAsk();
		        	
		        	if(result3)
		        		System.out.println(name+" est une personne");
		        	else
		        	{
		        	
		        		//optimisation mettre ca en une seule requete avec la query1 ou avec OPTIONAL
			        	String query4 = prefix+ "ASK WHERE {?x dbo:wikiPageDisambiguates ?y . " +
			        			//"{ ?y rdfs:label '"+name+" (name)'@fr} union  {?y rdfs:label '"+name+" (surname)'@fr}" +
			        			"?y rdf:type foaf:Person . " +
								"{ ?x rdfs:label '"+name+"'@fr } union { ?x rdfs:label '"+name+"'@en } }";
			        	
			        	boolean result4 = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query4).execAsk();
			        	
			        	if(result4)
			        		System.out.println(name+" est une personne");
			        	else
			        	{
			        		System.out.println(name+" n'est pas une personne");
			        		
			        		
			        	}
		        		
		        	}
		        		
		        	
		        	
		        	
		        }
	        	
	        }
	        
	       
        
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   
}
