package choucas.perdido.toponymDiscovery;


import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * @author Javier Nogueras
 * This class provides methods for searching toponyms in gazetteers providing a WFS API
 */
public abstract class ToponymFinder {

	protected WFSClient _wfsClient;

	public ToponymFinder(WFSClient wfsClient) {
		_wfsClient = wfsClient;
	}
	
	/**
	 * Search toponyms listed in an input file, and generates the corresponding output file.
	 * @param inputFileName path of the file containing a line for each toponym to look for
	 * @param outputFileName path of the file that will be generated with the results. 
	 *         A set of results consists of: a first line with the toponym name to be discovered, and a line with any of the toponyms found.
	 *         The line of each toponym found contains the following values separated by tab characters: name, latitude, longitude, country
	 *         , continent, type (general type), local type (specific of the gazetteer).
	 *         Each set of results is separated by a blank line.
	 */
	public void searchToponymInFile(String inputFileName, String outputFileName) {
	  try{
		Scanner inputScanner = new Scanner (new File(inputFileName));
		
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
	
		while (inputScanner.hasNextLine()) {
		  String toponymName = inputScanner.nextLine().trim();
		  
		  if ((toponymName!=null)&&(!toponymName.isEmpty())) {
		  
		  pw.println(toponymName); // insert a line with the toponym name
		  List<ToponymInfo> toponyms = searchToponym(toponymName,false);
		  
		  if (toponyms!=null) {
		    for (ToponymInfo toponym: toponyms)
			  pw.println(toponym); // insert a line for each found toponym
		  }
		  pw.println(); // insert a blank line to distinguish the next toponym
		  }
		}
		inputScanner.close();
		pw.close();
		
	  } catch(Exception ex) {
		  
	  }
	}
	
	/** 
	 * Search toponyms listed in the files contained in an input folder, and generates the corresponding output files in an output folder.
	 * @param inputFolderName path of the input folder
	 * @param outputFolderName path of the output folder
	 */
	public void searchToponymInFolder(String inputFolderName, String outputFolderName) {
		
		File folder = new File(inputFolderName);
		if (folder.isDirectory()) {
			String[] files = folder.list();
			int i=0;
			for (String file:files) {
				
				String inputFileName = inputFolderName+"/"+file;
				String outputFileName = outputFolderName+"/"+file;
				
				System.out.println(i+":" + inputFileName); i++;
				searchToponymInFile(inputFileName, outputFileName);
				
				
			}
		}
	}
	
	/** 
	 * Search the toponyms associated with a toponym name
	 * @param toponymName name of the toponym to look up
	 * @return List of toponyms
	 * @see ToponymInfo
	 */
	public List<ToponymInfo> searchToponym(String toponymName, boolean strictQuery){
		
		String featureCollection = _wfsClient.getFeaturesPost(toponymName,strictQuery);
		
		
		List<ToponymInfo> toponymInfoList = processFeatureCollection(featureCollection);
		
		// Take into account that we should check the number of features returned by WFS. Currently, we only process 10 as maximum
		if (toponymInfoList.size()==_wfsClient.getMaxResults()){
			System.err.println("Warning: "+ toponymInfoList.size() + " results (the maximum) reached with toponym "+toponymName);
		}
	
		return toponymInfoList;
	}
	
	/** 
	 * Process the response to the WFS GetFeatures request
	 * @param featureCollection response to the WFS GetFeatures request
	 * @return list of ToponymInfo objects
	 */
	protected List<ToponymInfo> processFeatureCollection(String featureCollection) {
		List<ToponymInfo> result = new LinkedList<ToponymInfo>();
		try{
		  DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		  //domFactory.setNamespaceAware(false); // false es con namesapace
		  DocumentBuilder builder = domFactory.newDocumentBuilder();
		  
		  ByteArrayInputStream input = new ByteArrayInputStream(featureCollection.getBytes());
		  
		  org.w3c.dom.Document xmlDoc = builder.parse(input);
		  
		  NodeList list = xmlDoc.getElementsByTagName(getToponymNodeName());
		  
		  for (int i=0; i< list.getLength();i++) {
			  
			  Node node = list.item(i);
			  
			  ToponymInfo toponym = new ToponymInfo();
			  // name
			  toponym.setName(getName(node));
			  // latitude
			  toponym.setLatitude(getLatitude(node));
			  // longitude
			  toponym.setLongitude(getLongitude(node));
			  // country
			  toponym.setCountry(getCountry(node));
			  // continent
			  toponym.setContinent(getContinent(node));
			  // type
			  toponym.setType(getType(node));
			  // localType
			  toponym.setLocalType(getLocalType(node));
	  
			  
			  result.add(toponym);
			  // result = list.item(0).getTextContent();
		  }
			  
		  
		  } catch( Exception ex ) {
			  System.err.println(ex.getMessage());
		  }
		return result;
		
		
	}
	
	protected abstract String getToponymNodeName();
		
	protected abstract String getName(Node input);
	
	protected abstract String getLatitude(Node input);

	protected abstract String getLongitude(Node input);
	
	protected abstract String getCountry(Node input);
	
	protected abstract String getContinent(Node input);

	protected abstract String getType(Node input);
	
	protected abstract String getLocalType(Node input);
	
	protected static Node firstChildNode (Node inputNode, String tag ) {
		Node result = null;
		
		NodeList list = inputNode.getChildNodes();
		for (int i=0; i< list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeName().equals(tag))
				return node;
		}
		return result;
	}
	

	
}
