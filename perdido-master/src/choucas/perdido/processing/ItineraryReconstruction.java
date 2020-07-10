package choucas.perdido.processing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;

import choucas.perdido.elements.Edge;
import choucas.perdido.elements.Node;
import choucas.perdido.elements.Toponyme;
import choucas.perdido.tools.MapsFunctions;
import choucas.perdido.maps.GoogleMapsAPI;
import choucas.perdido.postgresql.Postgis;
import choucas.perdido.tools.FileTools;



/**
 * ItineraryReconstruction class : provide some methods to compute minimum spanning tree
 * @author Ludovic Moncla
 * @version 1.0
 */
public class ItineraryReconstruction {
	
	
	
	public static Vector<Edge> process(Vector<Toponyme> toponyms,String filename,String criterion, String googleMapsAPIkey) throws Exception
	{
		System.out.println("Begin process ItineraryReconstruction");
		
		Vector<Node> nodes = new Vector<Node>();
		Vector<Edge> graph = new Vector<Edge>(); 	//graph containing all possible edges
		Vector<Edge> completeGraph = new Vector<Edge>(); 
		
		Vector<Edge> mst = new Vector<Edge>();		//maximumSpanningTree
		Vector<Edge> path = new Vector<Edge>(); 	//tree containing only the node reached during the displacement
		
		//direction
		System.out.println("  --> direction");
		toponyms = setDirection(toponyms);
		
		//on créer le vecteur de noeuds
		for(int i=0;i<toponyms.size();i++)
		{
			//if(toponyms.get(i).isBest())
			{
				if(!toponyms.get(i).getFeatureText().equals("direction"))
				{
					nodes.add(new Node(toponyms.get(i)));
				}
			}
		}
		
		System.err.println("<-- nodes.size() : "+nodes.size());
		
		//on créer le graph
		System.out.println("--> buildCompleteGraph");
		completeGraph = buildCompleteGraph(nodes);
		
		
		
		try {
			createGPX(completeGraph,filename+"_completeGraph");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println("<-- completeGraph.size() : "+completeGraph.size());
		
		System.out.println("--> weightedGraph : "+criterion);
	
		graph = weightedGraph(completeGraph,filename,criterion,googleMapsAPIkey);
		
		
		
		
		
		System.err.println("<-- nodes.size() : "+nodes.size());
		System.err.println("<-- graph.size() : "+graph.size());
		
		System.out.println("--> minimumSpanningTree");
		//mst = maximumSpanningTree(nodes,graph);
		mst = minimumSpanningTree(nodes,graph);
		
		System.err.println("<-- nodes.size() : "+nodes.size());
		System.err.println("<-- mst.size() : "+mst.size());
		
		Vector<Edge> lpath = null;
		
		try {
			createGPX(mst,filename+"_mst");
		
		
			//suppression des branches en trop (lieu perception)
			//path = mst;
			path.clear();
			path.addAll(mst);
		
			
			//Node startingPoint = getStartingPoint(path,nodes);
			
			
			for(int i=0;i<path.size();i++)
			{
				System.out.println(i+" : "+path.get(i).getNodeStart().getGid()+" -> "+path.get(i).getNodeEnd().getGid());
			}
			
			//recherche du plus long chemin
			System.out.println("--> getLongestPath");
			lpath = getLongestPath(path,nodes,criterion);
			
			System.out.println("starting point : "+lpath.get(0).getNodeStart().getGid());
			System.out.println("ending point : "+lpath.get(lpath.size()-1).getNodeEnd().getGid());
			
			//ordonnancement des arêtes
			//path = path;
			
			for(int i=0;i<lpath.size();i++)
			{
				System.out.println(i+" : "+lpath.get(i).getNodeStart().getGid()+" -> "+lpath.get(i).getNodeEnd().getGid());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("End process ItineraryReconstruction");
		return lpath;
	}
	
	
	/**
	 * 
	 * @param nodes
	 * @return
	 */
	public static Vector<Edge> buildCompleteGraph(Vector<Node> nodes)
	{
		Vector<Node> nodes_tmp = new Vector<Node>(nodes);
		Vector<Edge> graph = new Vector<Edge>();
		Node n = nodes_tmp.get(0); //on commence par ajouter le node 1 à la liste des nodes parcourus
		    
		nodes_tmp.remove(0); //on supprime ce node de la liste des nodes qu'il reste à traiter.
		    
		
		while (true) 
	    {
	    	//on quitte la boucle lorsque tout les noeuds on été ajoutés
	    	if (nodes_tmp.isEmpty()) {
    	       break;
    	    }
	    	
	    	for(int i=0;i<nodes_tmp.size();i++)
	    	{
	    		//on ne rattache pas les toponymes qui sont associé au mot direction
	    		//if(!nodes_tmp.get(i).getToponym().getFeatureText().equals("direction"))
	    		{
	    			System.err.println(" **  n.getId() : "+n.getId());
	    			System.err.println(" **  nodes_tmp.get(i).getId() : "+nodes_tmp.get(i).getId());
	    			System.err.println(" **  n.getIid() : "+n.getIid());
	    			System.err.println(" **  nodes_tmp.get(i).getIid() : "+nodes_tmp.get(i).getIid());
	    			//modif
		    		if(n.getId() != nodes_tmp.get(i).getId() && n.getIid() != nodes_tmp.get(i).getIid())
		    		{
		    			System.err.println(" **  pass ** : ");
		    			graph.add(new Edge(n,nodes_tmp.get(i)));
		    		}
	    		}
	    			
	    	}
	    	
	    	n = nodes_tmp.get(0);
	    	nodes_tmp.remove(0);
	    }
		
		System.err.println(" **  graph.size() : "+graph.size());
		
		return graph;
	}
	
	public static Vector<Edge> getLongestPath(Vector<Edge> path1, Vector<Node> nodes1,String criterion)
	{
		Vector<Edge> lpath = new Vector<Edge>();
		
		Vector<Edge> path = new Vector<Edge>();
		Vector<Node> nodes = new Vector<Node>();
		
		
		//on supprime les noeud qui sont asscociés à perception ou negation
		if(criterion.equals("multicriteria_7bis") ||criterion.equals("multicriteria_8") || criterion.equals("multicriteria_9")  || criterion.equals("multicriteria_10") || criterion.isEmpty())
		{
			for(int i = 0;i<path1.size();i++)
			{
				if(path1.get(i).getNodeStart().getToponym().getPerception() != 1 && path1.get(i).getNodeEnd().getToponym().getPerception() != 1)
				{
					if(path1.get(i).getNodeStart().getToponym().getNegation() != 1 && path1.get(i).getNodeEnd().getToponym().getNegation() != 1)
					{
						path.add(path1.get(i));
					}
				}
			}
			
			for(int i=0;i<nodes1.size();i++)
			{
				if(nodes1.get(i).getToponym().getPerception() != 1 && nodes1.get(i).getToponym().getNegation() != 1)
				{
					nodes.add(nodes1.get(i));
				}
			}
		}
		else
		{
			path.addAll(path1);
			nodes.addAll(nodes1);
		}
		
		//création de la matrice d'adjacence
		int adjacency_matrix[][] = buildAdjacencyMatrix(path,nodes);
		
		//premier dfs 
		Vector<Integer> vec = dfs(adjacency_matrix,0);
		
		//System.out.println("size: "+vec.size());
		int index1 = 0, index2=0;
		if(vec.size() > 0)
		{
			index1 = vec.get(vec.size()-1);
			
			//System.out.println("index : "+nodes.get(index).getId());
			
			//deuxieme dfs : fourni un vecteur ordonner des indice des nodes du plus long chemin
			vec = dfs(adjacency_matrix,index1);
			
			index2 = vec.get(vec.size()-1);
			
			//System.out.println("index : "+nodes.get(index).getId());
		}
		
		/*System.out.println(" ** * vec.size() : "+vec.size());
		
		//supprimer les noeud feuille de l'arbre à part le depart et l'arrivée
		
		int cpt = 0;
		for(int i=0;i<nodes.size();i++)
		{
			cpt = 0;
			for(int j=0;j<vec.size();j++)
			{
				
				//compter les liens de chaque noeud, 
				// si un noeud a qu'un lien et qu'il n'est ni index1 ni index2 on le supprime.
				if(nodes.get(i).getGid() == nodes.get(vec.get(j)).getGid())
				{
					cpt++;
				}
			}
			
			if(cpt == 1)
			{
				if(nodes.get(i).getGid() != index1 && nodes.get(i).getGid() != index2)
				{
					for(int j=0;j<vec.size();j++)
					{
						if(nodes.get(i).getGid() == nodes.get(vec.get(j)).getGid())
						{
							System.out.println(" ** * remove : "+nodes.get(i).getGid());
							vec.remove(j);
							break;
						}
					}
				}
			}
			
		}
		
		System.out.println(" ** * vec.size() : "+vec.size());
		
		for(int i=0;i<vec.size();i++)
		{
			System.out.println(" ** * vec.get("+i+") : "+nodes.get(vec.get(i)).getGid());
		}
		*/
		for(int i=0;i<vec.size()-1;i++)
		{
			
			//System.out.println(i+" : "+nodes.get(vec.get(i)).+" -> "+e.getNodeEnd());
			Edge e = new Edge(nodes.get(vec.get(i)),nodes.get(vec.get(i+1)));
			
			//System.out.println(i+" : "+e.getNodeStart()+" -> "+e.getNodeEnd());
			lpath.add(e);
		}
		
		
		return lpath;
	}
	
	public static Node getStartingPoint(Vector<Edge> path, Vector<Node> nodes)
	{
		System.out.println("--> getStartingPoint");
		int index1 = -1, index2 = -1;
		int cpt2 = 0;
		
		System.err.println("<-- nodes.size() : "+nodes.size());
		
		for(int i=0;i<nodes.size();i++)
		{
			int cpt = 0;
			
			for(int j=0;j<path.size();j++)
			{
				if(path.get(j).contains(nodes.get(i)))
				{
					cpt++;
				}
			}
			System.err.println("<-- i : "+i+" cpt : "+cpt);
			if(cpt == 1)
			{
				cpt2++;
				if(cpt2 < 3)
				{
					if(index1 == -1)
						index1 = i;
					else
						index2 = i;
				}
				else
				{
					System.err.println("Erreur trop de noeud unique");
					return null;
				}
			}
			
		}
		
		System.err.println("<-- getStartingPoint ");
		return nodes.get(index1);
		//index1 et index2 sont les starting et ending points
		
	}
	
	
	
	
	public static int[][] buildAdjacencyMatrix(Vector<Edge> path, Vector<Node> nodes)
	{
		int matrix[][] = new int[nodes.size()+1][nodes.size()+1];
		
		for(int i=0;i<nodes.size();i++)
		{
			for(int j=0;j<nodes.size();j++)
			{
				matrix[i][j] = -1;
				
				for(int k=0;k<path.size();k++)
				{
					if(path.get(k).contains(nodes.get(i),nodes.get(j)))
					{
						matrix[i][j] = k;
						break;
					}
				}
			}
		}
		
		
		return matrix;
	}
	
	
	public static Vector<Integer> dfs(int adjacency_matrix[][], int source)
    {
		
		System.out.println("--> dfs ");
		Stack<Integer> stack = new Stack<Integer>();
		Vector<Integer> vec = new Vector<Integer>();
		//int source = 0;
        int number_of_nodes = adjacency_matrix[source].length - 1;
 
        int visited[] = new int[number_of_nodes + 1];		
        int element = source;		
        int i = source;		
        int max_size = 0;
        int index = -1;
        //System.out.print(element + "\t");		
        visited[source] = 1;		
        stack.push(source);
        
        
 
        while (!stack.isEmpty())
        {
            element = stack.peek();
            //i = element;	
            i = 0;
            while (i < number_of_nodes)
            {
            	//System.out.print("+");
     	        if (adjacency_matrix[element][i] > -1 && visited[i] == 0)
     	        {
                    stack.push(i);
                    visited[i] = 1;
                    element = i;
                    i = 0;
                   
                    
                   // System.out.print(nodes.get(element).getId() + "\t");
                    //System.out.print(adjacency_matrix[element][i] + "\t");
                    continue;
                }
                i++;
	    	}
            
            if(stack.size() > max_size)
            {
            	
            	index = stack.peek();
            	max_size = stack.size();
            	vec.clear();
            	vec.addAll(stack);
            	
            	
            //	System.out.println("max_size : "+max_size);
            }
            stack.pop();
           // System.out.println("pop");
            
        }	
        
        //System.out.println("<-- dfs ");
        
        
        return vec;
    }
	
	
	public static void createGPX(Vector<Edge> path, String filename) throws Exception
	{
		
		//Node n = startingPoint;
		
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>";
		xml += "<gpx>";
		
		
		//xml += "<trkpt lat=\""+n.getLng()+"\" lon=\""+n.getLat()+"\" />";
		
		
			for(int j=0;j<path.size();j++)
			{
				xml += "<trk><name>Test</name><trkseg>";
				xml += "<trkpt lat=\""+path.get(j).getNodeStart().getLng()+"\" lon=\""+path.get(j).getNodeStart().getLat()+"\" />";
				xml += "<trkpt lat=\""+path.get(j).getNodeEnd().getLng()+"\" lon=\""+path.get(j).getNodeEnd().getLat()+"\" />";
				xml += "</trkseg></trk>";
				
				
			}
			
		
		
		xml += "</gpx>";
		
		//System.out.println(xml);
		FileTools.createFile(filename+".gpx", xml);
		
		
	}
	
	
	
	/**
	 * 
	 * @param edges
	 * @return
	 */
	public static Vector<Edge> weightedGraph(Vector<Edge> edges, String filename,String criterion, String googleMapsAPIkey) throws Exception
	{
		
		//System.out.println("--> weightedGraph");
		//Vector<Edge> edges = new Vector<Edge>();
		Vector<Edge> edgesSER = new Vector<Edge>();
		//double distance = 0;
		double weight = 0;
		
		//double maxDistance = 0;
		double maxDistanceElevation = 0;
		double maxDistanceEuclidian = 0;
		double maxDistanceHaversine = 0;
		double maxDistanceEffort = 0;
		double maxEffort = 0;
		double maxCumulativeElevation = 0;
		double maxChangeCover = 0;
		double maxTextOrder = 0;
		
		/*
		 *  Affectation des différents critères aux arêtes 
		 */
		File f = new File (filename + ".ser");
		if (f.exists())
		{
			System.out.println("--> on récupère le graph sérialisé");
			//on récupère le graph depuis les données sérialisées
			final ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename + ".ser")));
			//edges.clear();
			edgesSER = (Vector<Edge>) in.readObject();
			
			for(int i=0;i<edges.size();i++)
			{
				edges.get(i).setDistanceHaversine(edgesSER.get(i).getDistanceHaversine());
				edges.get(i).setDistanceElevation(edgesSER.get(i).getDistanceElevation());
				edges.get(i).setDistanceEuclidian(edgesSER.get(i).getDistanceEuclidian());
				edges.get(i).setDistanceEffort(edgesSER.get(i).getDistanceEffort());
				//edges.get(i).setVisibility(edgesSER.get(i).getVisibility());
				edges.get(i).setTextOrder(edgesSER.get(i).getTextOrder());
				
				edges.get(i).setCumulativePositiveElevation(edgesSER.get(i).getCumulativePositiveElevation());
				edges.get(i).setCumulativeNegativeElevation(edgesSER.get(i).getCumulativeNegativeElevation());
				
				//edges.get(i).setCumulativeElevation(edgesSER.get(i).getCumulativeElevation());
				//edges.get(i).setChangeCover(edgesSER.get(i).getChangeCover());
				edges.get(i).setPerception(edgesSER.get(i).getPerception());
				edges.get(i).setNegation(edgesSER.get(i).getNegation());
				
				edges.get(i).setRelElevation(edgesSER.get(i).getRelElevation());
				
				edges.get(i).setRelSpatial(edgesSER.get(i).getRelSpatial());
				edges.get(i).setRelTemporal(edgesSER.get(i).getRelTemporal());
			}
			
		}
		else
		{
			System.out.println("--> pas de graph sérialisé on en créer un ...");
			for(int i=0;i<edges.size();i++)
			{
				
				
				System.out.println("  + ("+edges.get(i).getNodeStart().getName()+" - "+edges.get(i).getNodeEnd().getName()+")");
				
				
				
			//	System.out.println("  --> t1 - lat : "+edges.get(i).getNodeStart().getLat()+ " lng : "+edges.get(i).getNodeStart().getToponym().getLng());
			//	System.out.println("  --> t2 - lat : "+edges.get(i).getNodeEnd().getLat()+ " lng : "+edges.get(i).getNodeEnd().getToponym().getLng());
				
				System.out.println("  --> getPathElevation");
				
				Vector<Toponyme> samplesTopo = GoogleMapsAPI.getPathElevation(edges.get(i).getNodeStart().getToponym(),edges.get(i).getNodeEnd().getToponym(),50,googleMapsAPIkey);
				if(samplesTopo == null)
					throw new Exception("QUERY LIMIT");
				
				
				double positiveElevation = getCumulativePositiveElevation(samplesTopo);
				edges.get(i).setCumulativePositiveElevation(positiveElevation);
				double negativeElevation = getCumulativeNegativeElevation(samplesTopo);
				edges.get(i).setCumulativeNegativeElevation(negativeElevation);
				
				
				
				
				//distance
				
				
				
				double distanceEuclidian = distanceEuclidian(edges.get(i).getNodeStart().getToponym(),edges.get(i).getNodeEnd().getToponym());
				edges.get(i).setDistanceEuclidian(distanceEuclidian);
				System.out.println("  --> distanceEuclidian : "+distanceEuclidian);
				
				double distanceHaversine = distance(edges.get(i).getNodeStart().getToponym(),edges.get(i).getNodeEnd().getToponym());
				edges.get(i).setDistanceHaversine(distanceHaversine);
				System.out.println("  --> distanceHaversine : "+distanceHaversine);
				
				double distanceElevation = distance(samplesTopo);
				edges.get(i).setDistanceElevation(distanceElevation);
				System.out.println("  --> distanceElevation : "+distanceElevation);
				
				
				System.out.println("  --> positiveElevation : "+positiveElevation);
				System.out.println("  --> negativeElevation : "+negativeElevation);
				
				double distanceEffort = distanceEuclidian + (10 * positiveElevation) + (3 * negativeElevation);
				edges.get(i).setDistanceEffort(distanceEffort);
				System.out.println("  --> distanceEffort : "+distanceEffort);
				
				
				
				//double distanceElevation = distance;
				
				//on récupère la distance entre les deux points de cette arête
				//double distance = distance(edges.get(i).getNodeStart().getToponym(),edges.get(i).getNodeEnd().getToponym(),20);
				
				
				
				
				// visibility
				//System.out.println("  --> visibility");
				//int visibility = visibility(samplesTopo);
				//edges.get(i).setVisibility(visibility);
				
				
				//text order
				System.out.println("  --> setTextOrder");
				edges.get(i).setTextOrder(Math.abs(edges.get(i).getNodeStart().getId() - edges.get(i).getNodeEnd().getId()));
				
				/*
				if(edges.get(i).getNodeStart().getGid() == edges.get(i).getNodeEnd().getGid()+1 || edges.get(i).getNodeStart().getGid() == edges.get(i).getNodeEnd().getGid()-1)
					edges.get(i).setTextOrder(1);
				else
					edges.get(i).setTextOrder(0);
				*/
				
				
				// difficulty
				//System.out.println("  --> getCumulativeElevation");
				//double cumulativeElevation = getCumulativeElevation(samplesTopo);
				 
				//edges.get(i).setCumulativeElevation(cumulativeElevation);
				
				
				
				/*
				//System.out.println("----- Dénivelé : "+cumulativeElevation);
				int lc11 = 0;
				int lc21 = 0;
				int lc22 = 0;
				int lc12 = 0;
				//land cover
				System.out.println("  --> landCover");
				String clcCode = edges.get(i).getNodeStart().getToponym().getClcCode();
				if(clcCode.length() > 2)
				{
					lc11 = Integer.parseInt(edges.get(i).getNodeStart().getToponym().getClcCode().substring(0, 1));
					lc12 = Integer.parseInt(edges.get(i).getNodeStart().getToponym().getClcCode().substring(1, 2));
				}
				clcCode = edges.get(i).getNodeEnd().getToponym().getClcCode();
				if(clcCode.length() > 2)
				{
					lc21 = Integer.parseInt(edges.get(i).getNodeEnd().getToponym().getClcCode().substring(0, 1));
					lc22 = Integer.parseInt(edges.get(i).getNodeEnd().getToponym().getClcCode().substring(1, 2));
				}
				
				double changeCover = Math.abs(lc11 - lc21) + Math.abs(lc12 - lc22);
				edges.get(i).setChangeCover(changeCover);
				*/
				
				// perception
				System.out.println("  --> perception");
				if(edges.get(i).getNodeStart().getToponym().getPerception() == 1 || edges.get(i).getNodeEnd().getToponym().getPerception() == 1)
					edges.get(i).setPerception(1);
				else
					edges.get(i).setPerception(0);
				
				//negation
				System.out.println("  --> negation");
				if(edges.get(i).getNodeStart().getToponym().getNegation() == 1 || edges.get(i).getNodeEnd().getToponym().getNegation() == 1)
					edges.get(i).setNegation(1);
				else
					edges.get(i).setNegation(0);
				
				
				
				
				//elevation relations
				// monter, descendre, ...
				System.out.println("  --> elevation relation");
				edges.get(i).setRelElevation(compareRelElevation(edges.get(i)));
				
				//spatial relations
				// S, N, W, E, SE, SW, NE, NW 
				System.out.println("  --> spatial relation");
				edges.get(i).setRelSpatial(compareRelSpatial(edges.get(i)));
				
				
				
				//temporal relations
				System.out.println("  --> temporal relation");
				if(edges.get(i).getNodeStart().getToponym().getRelTemporal() == edges.get(i).getNodeEnd().getToponym().getId())
				{
					edges.get(i).setRelTemporal(0);
				}
				else
				{
					edges.get(i).setRelTemporal(1);
				}
				
				
				
				
			}
			
			//sérialisation du graph 
			final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename+".ser")));
			try {
				out.writeObject(edges);
			} finally {
				out.close();
		    }
		}
		
		
		
		
		//test visibilite
		
		
		
		/*
		 * Maximisation et normalisation des critères et calcul du poid 
		 */
		
		for(int i=0;i<edges.size();i++)
		{
		
			
			double effort = (10 * edges.get(i).getCumulativePositiveElevation()) + (3 * edges.get(i).getCumulativeNegativeElevation());
			edges.get(i).setEffort(effort);
			
			edges.get(i).setRelSpatial(compareRelSpatial(edges.get(i)));
			edges.get(i).setRelElevation(compareRelElevation(edges.get(i)));
			//temporal relations
			System.out.println("  --> temporal relation");
			if(edges.get(i).getNodeStart().getToponym().getRelTemporal() == edges.get(i).getNodeEnd().getToponym().getId())
			{
				edges.get(i).setRelTemporal(0);
			}
			else
			{
				edges.get(i).setRelTemporal(1);
			}
			
			//récupère la distance max
			if(edges.get(i).getDistanceElevation() > maxDistanceElevation)
				maxDistanceElevation = edges.get(i).getDistanceElevation();
			
			if(edges.get(i).getDistanceEuclidian() > maxDistanceEuclidian)
				maxDistanceEuclidian = edges.get(i).getDistanceEuclidian();
			
			if(edges.get(i).getDistanceEffort() > maxDistanceEffort)
				maxDistanceEffort = edges.get(i).getDistanceEffort();
			
			if(edges.get(i).getDistanceHaversine() > maxDistanceHaversine)
				maxDistanceHaversine = edges.get(i).getDistanceHaversine();
			
			if(edges.get(i).getEffort() > maxEffort)
				maxEffort = edges.get(i).getEffort();
			
			//récupère l'élévation max
		//	if(edges.get(i).getCumulativeElevation() > maxCumulativeElevation)
		//		maxCumulativeElevation = edges.get(i).getCumulativeElevation();
			
			//récupère l'écart maximum entre deux lieux dans le text
			if(edges.get(i).getTextOrder() > maxTextOrder)
				maxTextOrder = edges.get(i).getTextOrder();
			
			
			//récupère le changeCover max
		//	if(edges.get(i).getChangeCover() > maxChangeCover)
		//		maxChangeCover = edges.get(i).getChangeCover();
		}
		
		for(int i=0;i<edges.size();i++)
		{
			
			//double distanceM = (maxDistanceElevation - edges.get(i).getDistanceElevation()) / maxDistanceElevation;
			//double difficulty = (maxCumulativeElevation - edges.get(i).getCumulativeElevation()) / maxCumulativeElevation;
			//double textOrder = (maxTextOrder - edges.get(i).getTextOrder()) / maxTextOrder;
			double textOrder =  edges.get(i).getTextOrder() / maxTextOrder;
			//double changeCoverM = (maxChangeCover - edges.get(i).getChangeCover()) / maxChangeCover;
			
			//int visibility = edges.get(i).getVisibility();
			//System.err.println("visibility : "+visibility);
			
			int perception = edges.get(i).getPerception();
			int negation = edges.get(i).getNegation();
			
			double relSpatial = edges.get(i).getRelSpatial();
			double relTemporal = edges.get(i).getRelTemporal();
			double relElevation = edges.get(i).getRelElevation();
			
			double distanceM = 0;
			double effortM = 0;
			/*
			if(criterion.equals("multicriteria_7bis"))
				distanceM = edges.get(i).getDistanceElevation() / maxDistanceElevation;
			if(criterion.equals("multicriteria_8"))
				distanceM = edges.get(i).getDistanceEuclidian() / maxDistanceEuclidian;
			if(criterion.equals("multicriteria_9"))
				distanceM = edges.get(i).getDistanceEffort() / maxDistanceEffort;
			
			if(criterion.equals("multicriteria_10"))
			{*/
				distanceM =  edges.get(i).getDistanceEuclidian() / maxDistanceEuclidian;
				effortM = edges.get(i).getEffort() / maxEffort;
			//}
			
			/* Calcul du poid et affectation du poid aux arêtes */
			
			
			//weight = distanceM*5 + textOrder*5 + perception*20 + negation*20 + relTemporal*1 + relSpatial*1 + relElevation*1;
			
			/*
			if(criterion.equals("multicriteria_1"))
				weight = textOrder*5;
			if(criterion.equals("multicriteria_2"))
				weight = distanceM*5;
			if(criterion.equals("multicriteria_3"))
				weight = textOrder*5 + distanceM*5;
			if(criterion.equals("multicriteria_4"))
				weight = textOrder*5 + distanceM*5 + relTemporal*1;
			if(criterion.equals("multicriteria_5"))
				weight = textOrder*5 + distanceM*5 + relTemporal*1 + relSpatial*1;
			if(criterion.equals("multicriteria_6"))
				weight = textOrder*5 + distanceM*5 + relTemporal*1 + relSpatial*1 + relElevation*1;
			if(criterion.equals("multicriteria_7") || criterion.equals("multicriteria_7bis"))
				weight = textOrder*5 + distanceM*5 + relTemporal*1 + relSpatial*1 + relElevation*1 + perception*20 + negation*20;
			*/
	
			if(criterion.equals("multicriteria_1"))
				weight = textOrder*0.14;
			if(criterion.equals("multicriteria_2"))
				weight = distanceM*0.06;
			if(criterion.equals("multicriteria_3"))
				weight = textOrder*0.14 + distanceM*0.06;
			
			if(criterion.equals("multicriteria_4"))
				weight = textOrder*0.14 + distanceM*0.06 + effortM*0.04;
				
			
			//if(criterion.equals("multicriteria_4"))
			//	weight = textOrder*0.14 + distanceM*0.06;
			
			//if(criterion.equals("multicriteria_4"))
			//	weight = textOrder*4 + distanceM*3 + relElevation*1;
			//if(criterion.equals("multicriteria_4"))
			//	weight = textOrder*0.14 + distanceM*0.06 + relSpatial*0.04 + relElevation*0.04;
			if(criterion.equals("multicriteria_5"))
				weight = textOrder*0.14 + distanceM*0.06 + effortM*0.04 + relSpatial*0.04 + relElevation*0.04;
			
			if(criterion.equals("multicriteria_6"))
				weight = textOrder*0.14 + distanceM*0.06 + effortM*0.04 + relSpatial*0.04 + relElevation*0.04 + relTemporal*0.10;
			
			if(criterion.equals("multicriteria_7") || criterion.equals("multicriteria_7bis") || criterion.equals("multicriteria_8") || criterion.equals("multicriteria_9")|| criterion.equals("multicriteria_10"))
				weight = textOrder*0.14 + distanceM*0.06 + relTemporal*0.10 + relSpatial*0.04 + relElevation*0.04 + perception*0.29 + negation*0.29 + effortM*0.04;
				//weight = textOrder*4 + distanceM*2 + relTemporal*3 + relSpatial*1 + relElevation*1 + perception*6 + negation*6 + effortM*1;
			
			if(criterion.isEmpty())
				weight = textOrder*0.14 + distanceM*0.09 + relTemporal*0.10 + relSpatial*0.04 + relElevation*0.04 + perception*0.29 + negation*0.29 + effortM*0.04;
			
			edges.get(i).setWeight(weight);
			
			System.out.println("-+- ("+edges.get(i).getNodeStart().getId()+"-"+edges.get(i).getNodeEnd().getId()+") : MultiCriteria : "+weight+" \t\t | C1 : "+textOrder+" | C2 : "+distanceM+" | C3 : "+effortM+" \t|  C4 : "+relSpatial+" \t| C5 : "+relElevation+" \t| C6 : "+relTemporal+" \t|  C7 : "+perception+" \t| C8 : "+negation );
			
			
			try {
				
				FileTools.updateStat(filename + "_listDistanceEdges.csv",edges.get(i).getNodeStart().getId()+" -- "+edges.get(i).getNodeEnd().getId()+";"+edges.get(i).getDistanceHaversine());
					
				FileTools.updateStat(filename + "_listStatsDistancesEdges.csv",edges.get(i).getNodeStart().getId()+" -- "+edges.get(i).getNodeEnd().getId()+";"+edges.get(i).getNodeStart().getName()+" -- "+edges.get(i).getNodeEnd().getName()+";"+edges.get(i).getDistanceEuclidian()+";"+edges.get(i).getDistanceHaversine()+";"+edges.get(i).getDistanceElevation()+";"+edges.get(i).getDistanceEffort()+";"+edges.get(i).getCumulativePositiveElevation()+";"+edges.get(i).getCumulativeNegativeElevation());
				
			 
			 
			 } catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			 }
			
			//System.out.println(""+edges.get(i).getNodeStart().getId()+"-"+edges.get(i).getNodeEnd().getId()+";"+edges.get(i).getDistance());
			
			//System.out.println("distance "+edges.get(i).getNodeStart().getId()+"-"+edges.get(i).getNodeEnd().getId()+" = "+weight);
			
		}
		
		
		return edges;
	}
	
	
	public static Vector<Toponyme> setDirection(Vector<Toponyme> ts)
	{
		
		for(int i=0;i<ts.size();i++)
		{
			if(i>0)
			{
				System.err.println("getFeatureText : "+ts.get(i).getFeatureText());
				if(ts.get(i).getFeatureText().equals("direction"))
				{
					Toponyme t1 = ts.get(i-1);
					Toponyme t2 = ts.get(i);
					float angle = getAngle(t1.getLng(),t1.getLat(),t2.getLng(),t2.getLat());
					
					ts.get(i-1).setDirection(angle);
					System.out.println("direction angle : "+angle);
					
				}
			}
		}
		
		return ts;
	}
	
	/**
	 * Compare l'élévation entre les deux points en fonction de l'exrepssion associé dans le texte, monter, descendre...
	 * @param e
	 * @return
	 */
	public static double compareRelElevation(Edge e)
	{
		//retourne 0, 0.5 ou 1
		double r = 0.5;
		
		//vérifier que le précédent n'est pas un toponyme associé à perception ou negation
		
		//System.out.println("Begin compareRelElevation");
		if(e.getNodeEnd().getToponym().getVerb() != null && e.getNodeStart().getToponym().getVerb() != null)
		{
			if(e.getNodeEnd().getToponym().getVerb().getLemma().equals("monter"))
			{
				if(e.getNodeStart().getToponym().getElevation() < e.getNodeEnd().getToponym().getElevation())
					r = 0;
				else
					r = 1;
				System.out.println("-+- ("+e.getNodeStart().getId()+"-"+e.getNodeEnd().getId()+") : monter : "+r);
			}
			
			if(e.getNodeEnd().getToponym().getVerb().getLemma().equals("descendre"))
			{
				if(e.getNodeStart().getToponym().getElevation() > e.getNodeEnd().getToponym().getElevation())
					r = 0;
				else
					r = 1;
				System.out.println("-+- ("+e.getNodeStart().getId()+"-"+e.getNodeEnd().getId()+") : descendre : "+r);
			}
		}
		//System.out.println("End r : "+r); 
		return r;
	}
	
	public static double compareRelSpatial(Edge e)
	{
		double r = 0.5;
		double axe = -1;
		int al = 0;
		
		if(e.getNodeStart().getToponym().getDirection() != -1)
		{
			if(!e.getNodeEnd().getToponym().getFeatureText().equals("direction"))
			{
				axe = e.getNodeStart().getToponym().getDirection();
				al = 90;
			}
		}
		else
		{
			// S, N, W, E, SE, SW, NE, NW 
			if(e.getNodeStart().getToponym().getRelSpatial().equals("N"))
			{
				// de 0 à 90 et de 270 à 360
				axe = 0;
				al = 90;
			}
			if(e.getNodeStart().getToponym().getRelSpatial().equals("S"))
			{
				// de 90 à 270
				axe = 180;
				al = 90;
			}
			if(e.getNodeStart().getToponym().getRelSpatial().equals("W"))
			{
				// de 180 à 360
				axe = 270;
				al = 90;
			}
			if(e.getNodeStart().getToponym().getRelSpatial().equals("E"))
			{
				// de 0 à 180
				axe = 90;
				al = 90;
			}
			if(e.getNodeStart().getToponym().getRelSpatial().equals("SE"))
			{
				//90 o 180
				axe = 135;
				al = 45;
			}
			if(e.getNodeStart().getToponym().getRelSpatial().equals("SW"))
			{
				// de 180 à 270
				axe = 225;
				al = 45;
			}
			if(e.getNodeStart().getToponym().getRelSpatial().equals("NE"))
			{
				// de 0 à 90
				axe = 45;
				al = 45;
			}
			
			if(e.getNodeStart().getToponym().getRelSpatial().equals("NW"))
			{
				// de 270 à 360
				axe = 315;
				al = 45;	
			}
		}
		
		if(axe > -1)
		{
			float angle = getAngle(e.getNodeStart().getToponym().getLng(),e.getNodeStart().getToponym().getLat(),e.getNodeEnd().getToponym().getLng(),e.getNodeEnd().getToponym().getLat());
			double a = Math.abs(axe - angle);
			if(a > 180)
				a = Math.abs(a - 360);
			//r = 1 - (a / al);
			r = (a / al);
			
			System.out.println("-+- ("+e.getNodeStart().getId()+"-"+e.getNodeEnd().getId()+") : angle : "+angle +" r : "+r);
		}
		
		//if(r < -1)
		//	r = -1;
		
		if(r > 1)
			r = 1;
		
		return r;
	}
	
	/**
	 * Retourne l'angle entre deux points par rapport au nord
	 * @param latitudeOrigine
	 * @param longitudeOrigne
	 * @param latitudeDest
	 * @param longitudeDest
	 * @return
	 */
	protected static float getAngle(double latitudeOrigine,double longitudeOrigne, double latitudeDest,double longitudeDest) {
		double longDelta = longitudeDest - longitudeOrigne;
		double y = Math.sin(longDelta) * Math.cos(latitudeDest);
		double x = Math.cos(latitudeOrigine)*Math.sin(latitudeDest) - Math.sin(latitudeOrigine)*Math.cos(latitudeDest)*Math.cos(longDelta);
		double angle = Math.toDegrees(Math.atan2(y, x));
		while (angle < 0) {
			angle += 360;
		}
		return (float) angle % 360;
	}
	
	
	/**
	 * 
	 * @param topoSamples
	 * @return
	 */
	public static double getCumulativeElevation(Vector<Toponyme> topoSamples)
	{
		double h = 0;
		
		
			for(int i=0;i<topoSamples.size()-1;i++)
			{
				//on prend l'élévation cumulée pas que le dénivelé positif
				h += Math.abs(topoSamples.get(i).getElevation() - topoSamples.get(i+1).getElevation());	
			}
			
		
		return h;
	}
	
	
	public static double getCumulativePositiveElevation(Vector<Toponyme> topoSamples)
	{
		double h = 0;
		
		
			for(int i=0;i<topoSamples.size()-1;i++)
			{
				//on prend l'élévation cumulée pas que le dénivelé positif
				if(topoSamples.get(i+1).getElevation() > topoSamples.get(i).getElevation())
					h += Math.abs(topoSamples.get(i).getElevation() - topoSamples.get(i+1).getElevation());	
			}
			
		
		return h;
	}
	

	public static double getCumulativeNegativeElevation(Vector<Toponyme> topoSamples)
	{
		double h = 0;
		
		
			for(int i=0;i<topoSamples.size()-1;i++)
			{
				//on prend l'élévation cumulée pas que le dénivelé positif
				if(topoSamples.get(i+1).getElevation() < topoSamples.get(i).getElevation())
					h += Math.abs(topoSamples.get(i).getElevation() - topoSamples.get(i+1).getElevation());	
			}
			
		
		return h;
	}
	
	
	
	public static Vector<Edge> maximumSpanningTree(Vector<Node> n, Vector<Edge> g)
	{
		//System.out.println("--> maximumSpanningTree");
		
		Vector<Edge> mst = new Vector<Edge>();
		Vector<Node> nodes = new Vector<Node>(n);
		Vector<Node> nodesAdded = new Vector<Node>();
		Vector<Edge> graph = new Vector<Edge>(g);
	
		
	    
		nodesAdded.add(nodes.get(0)); //on commence par ajouter le node 1 à la liste des node parcouru
	    nodes.remove(0); //on supprime ce node de la liste des nodes qu'il reste à traiter.
	    
	    
	    try
	    {
	    
		    while (true) 
		    {	
		    	//on quitte la boucle lorsque tout les noeuds on été ajoutés
		    	if (nodes.isEmpty()) {
	    	       break; // all node are added
	    	    }
		    	
		    	double maxWeight = 0;
				int indexEdge = -1, indexNode = -1;
		    	
		    	// pour chaque noeud on regarde dans le graph restant quels est la meilleur arête
		    	for(int j=0;j<nodesAdded.size();j++)
	    		{
		    	
			    	for(int i=0;i<graph.size();i++)
			    	{
		    		
		    			if(graph.get(i).contains(nodesAdded.get(j)))
		    			{
		    				if(graph.get(i).getWeight() >= maxWeight)
		    				{
		    					maxWeight = graph.get(i).getWeight();
		    					indexEdge = i;
		    					indexNode = j;
		    				}
		    			}
		    		}
		    	}
		    	
		    	//on ajoute la meilleur arête au mst
		    	mst.add(graph.get(indexEdge));
		    	
		    	//on récupère le nouveau noeud
		    	Node tmpNode = graph.get(indexEdge).getOtherNode(nodesAdded.get(indexNode));
		    	
		    	
		    	//System.err.println(" **** nodesAdded.get(indexNode).getId() : "+nodesAdded.get(indexNode).getId());
		    	//System.err.println(" ++ tmpNode.getId() : "+tmpNode.getId());
		    	
		    	// on l'ajoute a la liste des noeuds déja ajoutés
		    	nodesAdded.add(tmpNode);
		    	
		    	//on supprime le noeud de la liste des noeuds restant
		    	nodes.remove(tmpNode);
		    	
		    	
		    	//on supprime l'arête du graph restant
		    	graph.remove(indexEdge);
		    	
		    	//System.err.println(" ++ mst.size() : "+mst.size());
		    	//System.err.println(" ++ graph.size() :"+graph.size());
		    	
		    	//System.err.println(" ++ nodes.size() :"+nodes.size());
		    	
		    	String s = "";
		    	for(int i=0; i<nodes.size();i++)
		    	{
		    		s += nodes.get(i).getGid()+" + ";
		    	}
		    	System.err.println(" ++ nodes :"+s);
		    	
		    	System.err.println(" ++ nodesAdded.size() :"+nodesAdded.size());
		    	s = "";
		    	for(int i=0; i<nodesAdded.size();i++)
		    	{
		    		s += nodesAdded.get(i).getGid()+" + ";
		    	}
		    	System.err.println(" ++ nodesAdded :"+s);
		    	
		    	s = "";
		    	for(int i=0; i<nodes.size();i++)
		    	{
		    		s += nodes.get(i).getGid()+" + ";
		    	}
		    	System.err.println(" ++ Noeuds restant :"+s);
		    	System.err.println(" ++ ");
		    	
		    	
		    	//Iterator it = graph.iterator();
		    	//while(it.hasNext())
		    	//on parcours le graph et on supprime les arêtes qui reli deux noeuds déja ajoutés
		    	//for(int i=0; i<graph.size();i++)
		    	for(int i = graph.size() - 1; i >= 0; i--)
		    	{
		    		//Edge ed = (Edge) it.next();
		    		
		    		if(nodesAdded.contains(graph.get(i).getNodeStart()) && nodesAdded.contains(graph.get(i).getNodeEnd()))
		    		{
		    			graph.remove(graph.get(i));
		    		}
		    	}
		    	
	    	}
	    }
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	    
	   // System.out.println("<-- maximumSpanningTree");
	    
	    return mst;
	}
	
	
	
	
	public static Vector<Edge> minimumSpanningTree(Vector<Node> n, Vector<Edge> g)
	{
		//System.out.println("--> maximumSpanningTree");
		
		Vector<Edge> mst = new Vector<Edge>();
		Vector<Node> nodes = new Vector<Node>(n);
		Vector<Node> nodesAdded = new Vector<Node>();
		Vector<Edge> graph = new Vector<Edge>(g);
	
		System.err.println(" ++ nodes.size() :"+nodes.size());
	    
		nodesAdded.add(nodes.get(0)); //on commence par ajouter le node 1 à la liste des node parcouru
	    nodes.remove(0); //on supprime ce node de la liste des nodes qu'il reste à traiter.
	    
	   
	    try
	    {
	    
		    while (true) 
		    {	
		    	System.err.println(" ++ nodes.size() :"+nodes.size());
			    System.err.println(" ++ nodesAdded.size() :"+nodesAdded.size());
			    System.err.println(" ++ graph.size() :"+graph.size());
		    	
		    	
		    	//on quitte la boucle lorsque tout les noeuds on été ajoutés
		    	if (nodes.isEmpty()) {
	    	       break; // all node are added
	    	    }
		    	
		    	double minWeight = 10000;
				int indexEdge = -1, indexNode = -1;
		    	
				
				
		    	// pour chaque noeud on regarde dans le graph restant quels est la meilleur arête
		    	for(int j=0;j<nodesAdded.size();j++)
	    		{
		    		 System.err.println(" j= "+j);
			    	for(int i=0;i<graph.size();i++)
			    	{
			    		System.err.println(" i= "+i);
		    			if(graph.get(i).contains(nodesAdded.get(j)))
		    			{
		    				if(graph.get(i).getWeight() <= minWeight)
		    				{
		    					minWeight = graph.get(i).getWeight();
		    					indexEdge = i;
		    					indexNode = j;
		    				}
		    			}
		    		}
		    	}
		    	
		    	//on ajoute la meilleur arête au mst
		    	mst.add(graph.get(indexEdge));
		    	
		    	 System.err.println(" ++ mst.size() :"+mst.size());
		    	
		    	//on récupère le nouveau noeud
		    	Node tmpNode = graph.get(indexEdge).getOtherNode(nodesAdded.get(indexNode));
		    	
		    	
		    	//System.err.println(" **** nodesAdded.get(indexNode).getId() : "+nodesAdded.get(indexNode).getId());
		    	//System.err.println(" ++ tmpNode.getId() : "+tmpNode.getId());
		    	
		    	// on l'ajoute a la liste des noeuds déja ajoutés
		    	nodesAdded.add(tmpNode);
		    	
		    	//on supprime le noeud de la liste des noeuds restant
		    	nodes.remove(tmpNode);
		    	
		    	
		    	//on supprime l'arête du graph restant
		    	graph.remove(indexEdge);
		    	
		    	//System.err.println(" ++ mst.size() : "+mst.size());
		    	//System.err.println(" ++ graph.size() :"+graph.size());
		    	
		    	//System.err.println(" ++ nodes.size() :"+nodes.size());
		    	
		    	String s = "";
		    	for(int i=0; i<nodes.size();i++)
		    	{
		    		s += nodes.get(i).getGid()+" + ";
		    	}
		    	System.err.println(" ++ nodes :"+s);
		    	
		    	System.err.println(" ++ nodesAdded.size() :"+nodesAdded.size());
		    	s = "";
		    	for(int i=0; i<nodesAdded.size();i++)
		    	{
		    		s += nodesAdded.get(i).getGid()+" + ";
		    	}
		    	System.err.println(" ++ nodesAdded :"+s);
		    	
		    	s = "";
		    	for(int i=0; i<nodes.size();i++)
		    	{
		    		s += nodes.get(i).getGid()+" + ";
		    	}
		    	System.err.println(" ++ Noeuds restant :"+s);
		    	System.err.println(" ++ ");
		    	
		    	
		    	//Iterator it = graph.iterator();
		    	//while(it.hasNext())
		    	//on parcours le graph et on supprime les arêtes qui reli deux noeuds déja ajoutés
		    	//for(int i=0; i<graph.size();i++)
		    	for(int i = graph.size() - 1; i >= 0; i--)
		    	{
		    		//Edge ed = (Edge) it.next();
		    		
		    		if(nodesAdded.contains(graph.get(i).getNodeStart()) && nodesAdded.contains(graph.get(i).getNodeEnd()))
		    		{
		    			graph.remove(graph.get(i));
		    		}
		    	}
		    	
	    	}
	    }
		catch(Exception e)
		{
			//System.out.println(e.toString());
			e.printStackTrace();
		}
	    
	   // System.out.println("<-- maximumSpanningTree");
	    
	    return mst;
	}
	
	
	
	
	
	
	/**
	 * Return the distance between two toponyms taking account the relief between them
	 * @param t1
	 * @param t2
	 * @param samples
	 * @return distance
	 */
	public static double distance(Toponyme t1, Toponyme t2)
	{
		//System.out.println("** Begin calcul distance ");
	
		double distance = 0;
		//Vector<Toponyme> samplesTopo = new Vector<Toponyme>();
		
		distance = MapsFunctions.getDistance(t1.getLat(),t1.getLng(),t2.getLat(),t2.getLng());
		
	
		//System.out.println("** End calcul distance ");
		
		return distance;
	}
	
	public static double distanceEuclidian(Toponyme t1, Toponyme t2)
	{
		//System.out.println("** Begin calcul distance ");
	
		double distance = 0;
		//Vector<Toponyme> samplesTopo = new Vector<Toponyme>();
		
		distance = MapsFunctions.getEuclidianDistance(t1.getLng(),t1.getLat(),t2.getLng(),t2.getLat());
		
		//System.out.println("++++ distance e : "+distance+" , e1 :  "+t1.getElevation()+" , e2 : "+t2.getElevation()+" ++++++");
	
		//System.out.println("** End calcul distance ");
		
		return distance;
	}
	
	
	
	public static double distance(Vector<Toponyme> samplesTopo)
	{
		
		double distance = 0;
		double d = 0;
		
		for(int j=0;j<samplesTopo.size()-1;j++)
		{
			d += MapsFunctions.getDistance(samplesTopo.get(j).getLat(), samplesTopo.get(j).getLng(), samplesTopo.get(j+1).getLat(), samplesTopo.get(j+1).getLng());
			distance += MapsFunctions.getDistanceUsingElevation(samplesTopo.get(j).getLat(),samplesTopo.get(j).getLng(),samplesTopo.get(j).getElevation(),samplesTopo.get(j+1).getLat(),samplesTopo.get(j+1).getLng(),samplesTopo.get(j+1).getElevation());
			
		}
	
	//	System.out.println("***** d : "+d+"  *******");
	//	System.out.println("***** distance : "+distance+"  *******");
		
		return distance;
	}
	
	public static int visibility(Vector<Toponyme> samplesTopo)
	{
		
		int visibility = 1;
		
		//System.err.println("Math.atan(45) : "+Math.tan(Math.toRadians(38)));
		//System.err.println("Math.atan2(1) : "+Math.toDegrees(Math.atan2(2,2)));
		
		Toponyme t1 = samplesTopo.firstElement();
		Toponyme t2 = samplesTopo.lastElement();
		
		double d = MapsFunctions.getDistance(t1.getLat(), t1.getLng(), t2.getLat(), t2.getLng());
		double h = Math.abs(t2.getElevation() - t1.getElevation());
		double alpha = Math.atan2(h,d); //radians
		
		System.out.println("debut "+t1.getElevation()+" fin :"+t2.getElevation());
		
		for(int j=0;j<samplesTopo.size()-1;j++)
		{
		
			double d2 = 0;
			double h2 = 0;
			double el = 0;
			
			if(t1.getElevation() < t2.getElevation())
			{
				d2 = MapsFunctions.getDistance(t1.getLat(), t1.getLng(), samplesTopo.get(j).getLat(), samplesTopo.get(j).getLng());

				el = samplesTopo.get(j).getElevation() - t1.getElevation();
			}
			else
			{
				d2 = MapsFunctions.getDistance(samplesTopo.get(j).getLat(), samplesTopo.get(j).getLng(), t2.getLat(), t2.getLng());
				
				el = samplesTopo.get(j).getElevation() - t2.getElevation();
			}
			
			
			h2 = Math.tan(alpha) * d2;
			
			if(el > (h2 + 5))
			{
				return 0;
			}
			
		}
	
		
		return visibility;
	}
	

	
	public static Vector<Toponyme> gpx2vector(String filename)
	{
		
		Vector<Toponyme> path = new Vector<Toponyme>();
		
		System.out.println("size : "+path.size());
		
		SAXBuilder sxb = new SAXBuilder();
		Document document;
		try {
			document = sxb.build(new File(filename));
		
			//Element racine = document.getRootElement();
			
			ElementFilter eFilter = new ElementFilter( "trkpt", null ); 
			Iterator<Element> iTrkpt = document.getRootElement().getDescendants(eFilter); //Gets the requested elements. 
			
			//FileTools.updateFile(fileName+"_statName.csv","id;verb;rs;feature;name;certainty;");
			
			while (iTrkpt.hasNext())
			{
				
				Element trkpt = (Element) iTrkpt.next();
				
				
				double lat = Double.parseDouble(trkpt.getAttributeValue("lat"));
				double lon = Double.parseDouble(trkpt.getAttributeValue("lon"));
				
				path.add(new Toponyme(lat,lon));
			
			}
			
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("size : "+path.size());
		
		return path;
		
	}
	
	
	
	public static void compareTrajectrories(Vector<Edge> autoTrajectory, Vector<Toponyme> realTrajectory, Postgis objPostgis, String filename, String name,Vector<Toponyme> toponyms)
	{
		
		String linestringAuto = "";
		String linestringReal = "";
		
		if(autoTrajectory.size()>0)
		{
			linestringAuto += autoTrajectory.get(0).getNodeStart().getLng()+" "+autoTrajectory.get(0).getNodeStart().getLat();
		}
		
		for(int i=0;i<autoTrajectory.size();i++)
		{
			linestringAuto += ", "+autoTrajectory.get(i).getNodeEnd().getLng()+" "+autoTrajectory.get(i).getNodeEnd().getLat();
		}
		
		
		for(int i=0;i<realTrajectory.size();i++)
		{
			if(i == 0)
				linestringReal += realTrajectory.get(i).getLat()+" "+realTrajectory.get(i).getLng();
			else
				linestringReal += ", "+realTrajectory.get(i).getLat()+" "+realTrajectory.get(i).getLng();
			
		}
		
		String stReq = "select greatest(st_area(ST_MakeValid(les2polys.poly1)), st_area(ST_MakeValid(les2polys.poly2))) from "+
						"(select st_makepolygon( ST_AddPoint(asso1.ligne12, st_startpoint(asso1.ligne12))) as poly1 , " +
						"st_makepolygon( ST_AddPoint(asso1.ligne12reverse, st_startpoint(asso1.ligne12reverse))) as poly2 "+
						"from("+
								"select st_makeline(ligne1.route1,ligne2.route2) as ligne12, st_makeline(ligne1.route1,st_reverse(ligne2.route2)) as ligne12reverse "+
								"from"+
								"(SELECT ST_LineFromText('LINESTRING("+linestringReal+")') as route1)ligne1, "+
								"(SELECT ST_LineFromText('LINESTRING("+linestringAuto+")') as route2)ligne2 "+
								")asso1 "+
								")les2polys ";
		
		//stReq = "(SELECT ST_LineFromText('LINESTRING(2 0, 4 0)') as route1";
		
		try {
			//objPostgis.connect("outputDemo");
			objPostgis.connect(objPostgis.db_results());
			Statement state = objPostgis.conn.createStatement();
			
			
			ResultSet res = state.executeQuery(stReq);
			res.next();
			double surface = res.getDouble(1);
			
			
			
			
			stReq = "select ST_length(ST_GeographyFromText('LINESTRING("+linestringReal+")'))";
			res = state.executeQuery(stReq);
			res.next();
			double l1 = res.getDouble(1);
			 
			 
			 
			stReq = "select ST_length(ST_GeographyFromText('LINESTRING("+linestringAuto+")'))";
			res = state.executeQuery(stReq);
			res.next();
			double l2 = res.getDouble(1);
			 
			 
			 
			 //ST_HausdorffDistance
			 
			 stReq = "select ST_HausdorffDistance(ST_LineFromText('LINESTRING("+linestringAuto+")'),ST_LineFromText('LINESTRING("+linestringReal+")'))";
			 //stReq = "select ST_HausdorffDistance(ST_LineFromText('LINESTRING(0 1, 2 -1)'),ST_LineFromText('LINESTRING(0 0, 2 0)'))";
			 res = state.executeQuery(stReq);
			 res.next();
			 double hausdorffDistance = res.getDouble(1);
			 
			 
			 
			 
			 /*
			   
			   SELECT ST_Buffer(
 ST_GeomFromText(
  'LINESTRING(50 50,150 150,150 50)'
 ), 10, 'endcap=round join=round');
			
			  */
			 
			 double buffer = 0;
			 double dif_buffer = 0;
			 
			 //SELECT AsEWKT(ST_Collect(Array[ST_MakePoint(0, 0), ST_MakePoint(10, 10),ST_MakePoint(15, 10)]));
			 String stArray = "Array[";
			 
			 for(int i=0;i<autoTrajectory.size();i++)
			 {
				 
				 if(i > 0)
					 stArray += ",";
					 
				 String seg_lineString = autoTrajectory.get(i).getNodeStart().getLng()+" "+autoTrajectory.get(i).getNodeStart().getLat()+", "+autoTrajectory.get(i).getNodeEnd().getLng()+" "+autoTrajectory.get(i).getNodeEnd().getLat();
				 stReq = "select ST_length(ST_GeographyFromText('LINESTRING("+seg_lineString+")'))";
				 res = state.executeQuery(stReq);
				 res.next();
				 double seg_length = res.getDouble(1);
				 
				 
				 //SELECT AsEWKT(ST_Collect(Array[ST_MakePoint(0, 0), ST_MakePoint(10, 10),ST_MakePoint(15, 10)]));
				 
				 double autoProportion = seg_length / l2;
				 
				 System.out.println("Auto proportion : "+autoProportion);
				 
				 buffer = (15 * seg_length ) / 100; 
				 
				 
				//stReq = "select ST_Length(ST_Difference(ST_LineFromText('LINESTRING("+linestringReal+")',4326),ST_Buffer(ST_LineFromText('LINESTRING("+linestringAuto+")',4326),50)))";
				 stReq = "select St_AsText(ST_Buffer(ST_GeographyFromText('LINESTRING("+seg_lineString+")'),"+buffer+"))";
				 
				 res = state.executeQuery(stReq);
				 res.next();
				 System.out.println("buffer coord : "+res.getString(1));
				 
				 
				 
				 stArray += "geometry(ST_Buffer(ST_GeographyFromText('LINESTRING("+seg_lineString+")'),"+buffer+"))";
			 
				 //stReq = "select ST_Length(ST_Difference(ST_LineFromText('LINESTRING("+linestringReal+")',4326),ST_Buffer(ST_LineFromText('LINESTRING("+linestringAuto+")',4326),50)))";
				 stReq = "select ST_Length(geography(ST_Difference(geometry(ST_GeographyFromText('LINESTRING("+linestringReal+")')),geometry(ST_Buffer(ST_GeographyFromText('LINESTRING("+seg_lineString+")'),"+buffer+")))))";
				 
				 res = state.executeQuery(stReq);
				 res.next();
				 double seg_length_dif_buffer = 1 - (res.getDouble(1)  / l1);
				 
				 System.out.println("Real proportion : "+seg_length_dif_buffer);
				 
				 dif_buffer += seg_length_dif_buffer;
			 
			 }
			 
			 stArray += "]";
			 
			 
			 stReq = "select ST_Length(geography(ST_Difference(geometry(ST_GeographyFromText('LINESTRING("+linestringReal+")')),ST_Union("+stArray+"))))";
			 res = state.executeQuery(stReq);
			 res.next();
			 double length_dif_buffer2 = 1 - (res.getDouble(1)  / l1);
			 
			 dif_buffer = 1 - dif_buffer;
			 
			 if(dif_buffer<0)
				 dif_buffer = 0;
			 
			 
			 
			 buffer = (3 * l2 ) / 100;
			 //ST_Length(ST_Difference(ST_LineFromText('LINESTRING(-7 1, 6 1)'),ST_Buffer(ST_LineFromText('LINESTRING(0 0, 2 0)'),2)));
			 
			 
			 //stReq = "select ST_Length(ST_Difference(ST_LineFromText('LINESTRING("+linestringReal+")',4326),ST_Buffer(ST_LineFromText('LINESTRING("+linestringAuto+")',4326),50)))";
			 stReq = "select ST_Length(geography(ST_Difference(geometry(ST_GeographyFromText('LINESTRING("+linestringReal+")')),geometry(ST_Buffer(ST_GeographyFromText('LINESTRING("+linestringAuto+")'),"+buffer+")))))";
			 
			 res = state.executeQuery(stReq);
			 res.next();
			 double length_dif_buffer = res.getDouble(1);
			 
			 
			 
			 
			 /*stReq = "select st_astext(ST_Difference(geometry(ST_GeographyFromText('LINESTRING("+linestringReal+")')),geometry(ST_Buffer(ST_GeographyFromText('LINESTRING("+linestringAuto+")'),600))))";
			 
			 //stReq = "select st_astext(ST_GeographyFromText('LINESTRING("+linestringAuto+")'))";
			 
			 
			 res = state.executeQuery(stReq);
			 res.next();
			 System.out.println("ST_difference :"+res.getString(1));
			 */
			 /*
			 stReq = "select st_astext(ST_GeographyFromText('LINESTRING("+linestringAuto+")'))";
			 res = state.executeQuery(stReq);
			 res.next();
			 System.out.println("auto linestring :"+res.getString(1));
			 
			 stReq = "select st_astext(ST_GeographyFromText('LINESTRING("+linestringReal+")'))";
			 res = state.executeQuery(stReq);
			 res.next();
			 System.out.println("real linestring :"+res.getString(1));
			 */
			 double difference = length_dif_buffer / l1;
			 
			 
				
			// System.out.println(" - surface : "+surface);
			 //System.out.println(" - l1 : "+l1);
			 
			 double distance1 = surface / l1;
			 
			 double distance2 = surface / l2;
			 System.out.println(" - distance : "+distance1);
			 
			 System.out.println(" - hausdorffDistance : "+hausdorffDistance);
			 System.out.println(" - difference : "+difference);
			 
			 
			 FileTools.updateStat(filename,name+";"+l1+";"+l2+";"+distance1+";"+distance2+";"+hausdorffDistance+";"+difference+";"+dif_buffer+";"+length_dif_buffer2+";"+toponyms.size());
			
			res.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.print(e.toString());
			e.printStackTrace();
		}
		
		/*
		select greatest(st_area(ST_MakeValid(les2polys.poly1)), st_area(ST_MakeValid(les2polys.poly2))) from
(select st_makepolygon( ST_AddPoint(asso1.ligne12, st_startpoint(asso1.ligne12))) as poly1 , st_makepolygon( ST_AddPoint(asso1.ligne12reverse, st_startpoint(asso1.ligne12reverse))) as poly2
from(
select st_makeline(ligne1.route1,ligne2.route2) as ligne12, st_makeline(ligne1.route1,st_reverse(ligne2.route2)) as ligne12reverse
from
(SELECT ST_LineFromText('LINESTRING(2 0, 4 0)') as route1)ligne1,
(SELECT ST_LineFromText('LINESTRING(2 -1, 4 1)') as route2)ligne2
)asso1
)les2polys
		*/
		
		
		
		
	}
	
}
