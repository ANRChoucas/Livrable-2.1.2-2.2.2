/**
 * Processing class : combine method of annotation and toponyms resolution to reconstruct an itinerary
 * @author 	Ludovic Moncla
 * 			CHOUCAS (University of Pau)
 */
package choucas.perdido.processing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.json.JSONException;

import corineLandCover.CLCArtifactFactory;
import corineLandCover.CLCFinder;
import corineLandCover.FrenchCLCArtifactFactory;

import choucas.perdido.disambiguation.DensityClustering;
import choucas.perdido.disambiguation.DuplicatePoints;
import choucas.perdido.elements.Edge;
import choucas.perdido.elements.Toponyme;
import choucas.perdido.parsing.ParsingPerdidoTEI;
import choucas.perdido.postagger.Freeling;
import choucas.perdido.postagger.POStagger;
import choucas.perdido.postagger.StanfordNLP;
import choucas.perdido.postagger.Talismane;
import choucas.perdido.postagger.Treetagger;
import choucas.perdido.postgresql.Postgis;
import choucas.perdido.postgresql.User;
import choucas.perdido.tools.FileTools;
import choucas.perdido.tools.StringTools;
import choucas.perdido.tools.XMLindent;
import choucas.perdido.tools.XmlTools;
import choucas.perdido.unitex.Unitex;
import choucas.perdido.unitex.UnitexTools;
import choucas.perdido.maps.GoogleMapsAPI;




/**
 * @author lmoncla
 *
 */
public class Perdido  {

	
	private String _user = "";
	private String _lang = "";
	private String _analyserPOS = "";
	
	private String _inputDir = "";
	private String _outputDir = "";
	
	private String _outputDirWebServices = "";
	
	
	private boolean _geonames = false;
	private boolean _osm = false;
	private boolean _nationalGazetteer = false;
	
	private boolean _doDelete = false;
	
	private boolean _doAnnotation = false;
	private boolean _doToponymsResolution = false;
	private boolean _doToponymsDisambiguation = false;
	
	
	//JMA 05/07/2017 DEBUT
	private boolean _doUnitex = false;
	//JMA 05/07/2017 FIN
	
	private boolean _strictQuery = false;
	private String _maxResults = "";
	
	//private String _ignKey = "";
	
	private boolean _doPOS = false;
	private boolean _useUnitexDictionary = false;
	
	
	
	private boolean _getElevation = false;
	private boolean _getCorineLandCover = false;
	
	private boolean _doClustering = false;
	private boolean _doTestClustering = false;
	
	private boolean _doSpanningTree = false;
	private boolean _doBoundingBox = false;
	
	private boolean _showMissingPoints = false;
	private boolean _showAmbiguities = false;
	
	
	private boolean _doStats = false;
	private boolean _doItineraryReconstruction = false;
	
	private String[] _listTaggers = null;
	private String[] _mailContact = null;
	
	private String[] _ign_bdd_tables = null;
	
	private boolean _suffixPOS = false;
	
	private String _uriUnitexApp = "";
	private String _uriUnitex = "";
	private String _unitexVersion = "";
	
	private String _analysisCascade = "";
	private String _synthesisCascade = "";
	
	private String _uriTreeTagger = "";
	
	
	//lmoncla 19.10.2018 ajout du mode client/server de Talismane
	private String _talismanePort = "";
	private String _talismaneHost = "";
	
		
	private String _uriMelt = "";
	private String _uriFreeling = "";
	private String _uriLangFreeling = "";
	private String _uriPOStags = "";
	
	private String _uriStanfordNLP_EN ="";
	private String _uriStanfordNLP_FR ="";
	private String _uriStanfordNLP_SP ="";
	
	private String _suffixeTablePgsql = "";

	private String _characterEncoding = "";
	
	private String _geom = "";
	private String _pgsql2shp = "";
	
	private String _urlPstg = "";
	private String _portPstg = "";
	private String _userPstg = "";
	private String _passwordPstg = "";
	
	private String _db_users = "";
	private String _db_results = "";
	
	
	private String _googleMapsAPIkey = "";
	private String _geonamesAPIkey = "";
	
	private String _ignAPIkey = "";
	
	private Vector<Edge> _path = new Vector<Edge>();
	

	private Postgis _objPostgis = null;
	
	

	
	
	/**
	 * initialize variables from config.properties
	 * @throws Exception
	 */
	public void loadParams() throws Exception
	{
		System.out.println("-- loadParams");
		
		Class<?> currentClass = new Object() { }.getClass().getEnclosingClass();
		
		InputStream is = currentClass.getResourceAsStream("/perdido/resources/config.properties");
		Properties props = new Properties();
		
		props.load(is);
		
		_user = props.getProperty("user");
		_lang = props.getProperty("lang");
		_analyserPOS = props.getProperty("analyserPOS");
		
		_inputDir = props.getProperty("inputDir");
		_outputDir = props.getProperty("outputDir");
		_outputDirWebServices = props.getProperty("outputDirWebServices");
		
		if(props.getProperty("geonames").equals("true"))
			_geonames = true;
	
		if(props.getProperty("osm").equals("true"))
			_osm = true;
		
		if(props.getProperty("nationalGazetteer").equals("true"))
			_nationalGazetteer = true;
		
		
		if(props.getProperty("doDelete").equals("true"))
			_doDelete = true;
		
		if(props.getProperty("doAnnotation").equals("true"))
			_doAnnotation = true;
		
		//JMA 05/07/2017 DEBUT
		if(props.getProperty("doUnitex").equals("true"))
			_doUnitex = true;
		//JMA 05/07/2017 FIN

		
		if(props.getProperty("doToponymsResolution").equals("true"))
			_doToponymsResolution = true;
		
		if(props.getProperty("doToponymsDisambiguation").equals("true"))
			_doToponymsDisambiguation = true;
		
		
		if(props.getProperty("getElevation").equals("true"))
			_getElevation = true;
		
		if(props.getProperty("getCorineLandCover").equals("true"))
			_getCorineLandCover = true;
		
		if(props.getProperty("strictQuery").equals("true"))
			_strictQuery = true;
			
		
		_maxResults = props.getProperty("maxResults");
		
		
		_geonamesAPIkey = props.getProperty("geonamesAPIkey");
		_googleMapsAPIkey = props.getProperty("googleMapsAPIkey");
		_ignAPIkey = props.getProperty("ignAPIkey");
		
		
		if(props.getProperty("doPOS").equals("true"))
			_doPOS = true;
		
		
		
		if(props.getProperty("useUnitexDictionary").equals("true"))
			_useUnitexDictionary = true;
		
		
		
		if(props.getProperty("doClustering").equals("true"))
			_doClustering = true;
		if(props.getProperty("doTestClustering").equals("true"))
			_doTestClustering = true;
		
		
		if(props.getProperty("doSpanningTree").equals("true"))
			_doSpanningTree = true;
		if(props.getProperty("doBoundingBox").equals("true"))
			_doBoundingBox = true;
		
		if(props.getProperty("showMissingPoints").equals("true"))
			_showMissingPoints = true;
		
		if(props.getProperty("showAmbiguities").equals("true"))
			_showAmbiguities = true;
		
		
		if(props.getProperty("doStats").equals("true"))
			_doStats = true;
		
		if(props.getProperty("suffixPOS").equals("true"))
			_suffixPOS = true;
		
		if(props.getProperty("doItineraryReconstruction").equals("true"))
			_doItineraryReconstruction = true;


		_uriUnitex = props.getProperty("rootNameUnitex");
		_uriUnitexApp = props.getProperty("uriUnitexApp");
		_unitexVersion = props.getProperty("unitexVersion");
		
		_analysisCascade = props.getProperty("analysisCascade");
		_synthesisCascade = props.getProperty("synthesisCascade");
		
		
		_suffixeTablePgsql = props.getProperty("suffixeTablePgsql");
		
		
		String listTaggers = props.getProperty("listTaggers");
		
		_listTaggers = listTaggers.split("/");
		
		
		String ign_bdd_tables = props.getProperty("ign_bdd_tables");
		
		_ign_bdd_tables = ign_bdd_tables.split(";;");
		
		
		String mailContact = props.getProperty("mailContact");
		
		_mailContact = mailContact.split("/");
		
		

		_uriTreeTagger = props.getProperty("rootNameTreeTagger");
		
		//lmoncla 19.10.2018 ajout du mode client/server de Talismane
		_talismanePort = props.getProperty("talismanePort");
		_talismaneHost = props.getProperty("talismaneHost");
		

		_uriMelt = props.getProperty("rootNameMelt");
		_uriFreeling = props.getProperty("rootNameFreeling");
		_uriLangFreeling = props.getProperty("langFreeling");
		
		
		_uriStanfordNLP_EN = props.getProperty("rootNameStanfordNLP_EN");
		_uriStanfordNLP_FR = props.getProperty("rootNameStanfordNLP_FR");
		_uriStanfordNLP_SP = props.getProperty("rootNameStanfordNLP_SP");
		
		_uriPOStags = props.getProperty("tagsPOStagger");
				
		//_version = props.getProperty("version");
		
		_geom = props.getProperty("geom");
		_pgsql2shp = props.getProperty("pgsql2shp");
		
		_characterEncoding = props.getProperty("characterEncoding");
		
		
		
		_urlPstg = props.getProperty("urlPstg");
		_portPstg = props.getProperty("portPstg");
		_userPstg = props.getProperty("userPstg");
		_passwordPstg = props.getProperty("passwordPstg");
		
		_db_users = props.getProperty("db_users");
		_db_results = props.getProperty("db_results");
		

		_objPostgis = new Postgis(_urlPstg,_portPstg,_userPstg,_passwordPstg,_db_users,_db_results);
		
		
		try{
			_objPostgis.connect(_db_users);
		
		}
		catch(SQLException e)
		{
			
			e.getErrorCode();
			System.err.println(e.toString()+" code = "+e.getErrorCode());
			
			if(e.getErrorCode() == 0)
			{
				_objPostgis.createDB(_objPostgis.db_users());
				System.out.println("1");
				User user = new User(_objPostgis); 
				
				System.out.println("2");
				
				user.createTableUser();
				
				System.out.println("3");
				
				user.addUser("admin","admin","admin",1,"admin","admin","admin",100000,1);
				
		
				
			}
			
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		
		
		
		System.out.println("-- end loadParams");
	}
	
	

	
	
	public String launchAnnotation(String content, String outputDir, String name, String fileName, String inputCascade) throws Exception
	{	
		
		System.out.println("-- launchAnnotation");
		
		String err = "";
		
		FileTools.createDir(outputDir);
		
		//filter le content
		content = StringTools.filtreString(content);
		
		
		FileTools.createFile(fileName+".txt", content);
		
		
		POStagger pos = null;

		if (analyserPOS().equals("treetagger"))
			pos = new Treetagger(uriTreeTagger(),lang());
			
		if (analyserPOS().equals("talismane"))
		{
			//lmoncla 19.10.2018 ajout du mode client/server de Talismane
			
			
			System.out.println("-- talismaneHost : "+talismaneHost());
			System.out.println("-- talismanePort : "+talismanePort());
			System.out.println("-- lang : "+lang());
			
			pos = new Talismane(talismaneHost(),talismanePort(), lang());
			/*
			//JMA 23/06/2017 ajout fichier conf pour version talismane 4.1.0
			if (uriTalismane().contains("4.1.0")){
				pos = new Talismane(uriTalismane(),lang(),uriTalismaneLang(),uriTalismaneConf());
			}
			else 
			{
				pos = new Talismane(uriTalismane(),lang(),uriTalismaneLang());
			}
			*/
		}
		if (analyserPOS().equals("freeling"))
			pos = new Freeling(uriFreeling(),lang());
		
		if (analyserPOS().equals("stanfordNLP"))
			pos = new StanfordNLP(uriStanfordNLP(lang()),lang());
		
		
			
		System.out.println(" +  doPOS : "+doPOS());
		
		if(doPOS())
		{
			System.out.println(" -- run POStagger : "+analyserPOS());
			
			//lmoncla 19.10.2018 ajout du mode client/server de Talismane
			if (analyserPOS().equals("treetagger") || analyserPOS().equals("talismane"))
				pos.run(content, fileName+"_"+analyserPOS()+".txt");
			else
				pos.run(fileName+".txt", fileName+"_"+analyserPOS()+".txt");
			
		
		}
		else
		{
			if(!useUnitexDictionary())
			{
				//copy
				FileUtils.copyFile(new File(inputDir() + user()+"/"+name+"_"+analyserPOS()+".txt"), new File(fileName+"_"+analyserPOS()+".txt"));
			}
		}
		
		
		String pos2 = pos.tagger2pivot(fileName+"_"+analyserPOS()+".txt");
		
		//JMA 21/07/2017 DEBUT
		FileTools.createFile(fileName+"_POSTEI.txt", pos2);
		//JMA 21/07/2017 FIN

		
		System.out.println("pos2 : "+pos2);
		
	
		
		String posPivot = pos.tagger2unitex(pos2);
		

		//create the file containing the result of the POS tagging with the Unitex format
		//JMA 05/07/2017 DEBUT
		/*FileTools.createFile(inputCascade+".txt", posPivot);
		
		
		Unitex unitex = new Unitex(uriUnitex(), uriUnitexApp(), lang());
		
		unitex.setAnalysisCascade(analysisCascade());
		unitex.setSynthesisCascade(synthesisCascade());
		unitex.setUseUnitexDictionary(useUnitexDictionary());
		
		unitex.launchCascade(inputCascade);
		
		
		
		UnitexTools.txt2tei(inputCascade+"_csc_csc.txt", fileName+".xml");

		*/
		if (doUnitex())
		{	
		 err = launchUnitex(posPivot, outputDir,name,fileName,inputCascade);
		 return err;
		}
		else return posPivot;
		//JMA 05/07/2017 FIN
	}
	
	
	
	
	//JMA 03/07/2017 DEBUT
		public String launchUnitex(String content, String outputDir, String name, String fileName, String inputCascade) throws Exception
		{	
			
			System.out.println("-- launchUnitex");
			
			String err = "";
			
			FileTools.createDir(outputDir);
			
			
			//create the file containing the result of the POS tagging with the Unitex format
			FileTools.createFile(inputCascade+".txt", content);
			
			
			Unitex unitex = new Unitex(uriUnitex(), uriUnitexApp(), lang(), unitexVersion());
			
			unitex.setAnalysisCascade(analysisCascade());
			unitex.setSynthesisCascade(synthesisCascade());
			unitex.setUseUnitexDictionary(useUnitexDictionary());
			
			unitex.launchCascade(inputCascade);
			
			
			
			UnitexTools.txt2tei(inputCascade+"_csc_csc.txt", fileName+".xml");

			
			
			
			return err;
		}
		//JMA 03/07/2017 FIN
	
	
	public String launchAnnotation2(String content, String fileName, String inputCascade) throws Exception
	{	
		
		System.out.println("-- launchAnnotation");
		
		String err = "";
		
			
		FileTools.createFile(fileName+".txt", content);
		
				
		
		Unitex unitex = new Unitex(uriUnitex(), uriUnitexApp(), lang(), unitexVersion());
		
		unitex.setAnalysisCascade(analysisCascade());
		unitex.setSynthesisCascade(synthesisCascade());
		unitex.setUseUnitexDictionary(useUnitexDictionary());
		
		unitex.launchCascade(inputCascade);
		
		
		UnitexTools.txt2xmlLike(inputCascade+"_csc_csc.txt");

		
		
		
		return err;
	}
	
	public void launchProcess2(String content, String outputDir, String name, String criterion) throws Exception
	{			
		System.out.println("--  launchProcess");
		
		
		//recovering number of words
		int nbWords = content.split(" ").length;
		
		
		
		String fileName = outputDir+"/"+name;
		
		String inputCascade = fileName+"_inputUnitex";
		
		System.out.println(" +  name : "+name);
		
		
		/**
		 * Text annotation
		 */
		System.out.println(" +  doAnnotation : "+doAnnotation());
		if(doAnnotation())
		{
			
			launchAnnotation2(content,fileName,inputCascade);
			
			
		} // end of the annotation
		
		
		
		
		XMLindent.indentXMLFile(fileName+".xml");
		
		
		
	}
	
	
	
	
	/**
	 * launch the process annotation and toponyms resolution
	 * @param content				textual description 
	 * @param outputDir				path of the output directory
	 * @param name					name of the document
	 * @throws Exception
	 */
	public void launchProcess(String content, String outputDir, String name, String criterion) throws Exception
	{			
		System.out.println("--  launchProcess");
		long beginLocal = System.currentTimeMillis();
		
		
		//recovering number of words
		int nbWords = content.split(" ").length;
		
		String err = "";

		
		String fileName = outputDir+"/"+name;
		String tableName = user()+"_"+name+"_clust";
		
		String inputCascade = fileName+"_inputUnitex";
		
		System.out.println(" +  name : "+name);
		
		
		/**
		 * Text annotation
		 */
		System.out.println(" +  doAnnotation : "+doAnnotation());
		if(doAnnotation())
		{
			
			err = launchAnnotation(content, outputDir,name,fileName,inputCascade);
			
		} // end of the annotation
		
		//JMA 05/07/2017 DEBUT
		//XMLindent.indentXMLFile(fileName+".xml");
		//JMA 05/07/2017 FIN
		
		
		long timeAnnotation = System.currentTimeMillis();
		
		
		/**
		 * Toponym resolution
		 */
		
		
		Vector<Toponyme> toponyms = null;
		//JMA 05/07/2017 DEBUT
		/* 
System.out.println(" +  doToponymsResolution : "+doToponymsResolution());
System.out.println(" --  fileName : "+fileName);
System.out.println(" --  lang : "+lang());
System.out.println(" --  strictQuery : "+strictQuery());
System.out.println(" --  maxResult : "+Integer.parseInt(maxResults()));
System.out.println(" --  geonames : "+geonames());
System.out.println(" --  nationalGazetteer : "+nationalGazetteer());
System.out.println(" --  osm : "+osm());
System.out.println(" --  ign_bdd_tables : "+ign_bdd_tables());
System.out.println(" --  geom : "+geom());
System.out.println(" --  objPostgis : "+objPostgis());
System.out.println(" --  geonamesAPIkey : "+geonamesAPIkey());

//ToponymResolution tr = new ToponymResolution(strictQuery(), Integer.parseInt(maxResults()), lang(), geonames(), nationalGazetteer(), osm(),ign_bdd_tables(), geom(), objPostgis());
//ParsingPerdidoTEI parsing = new ParsingPerdidoTEI(fileName+".xml", lang(), strictQuery(), Integer.parseInt(maxResults()), geonames(), nationalGazetteer(), osm(),ign_bdd_tables(), geom(), objPostgis()) 
try{
	
	if(doToponymsResolution())
	{
		ParsingPerdidoTEI parsing = new ParsingPerdidoTEI(fileName+".xml", lang(), strictQuery(), Integer.parseInt(maxResults()), geonames(), nationalGazetteer(), osm(),ign_bdd_tables(), geom(), objPostgis(),geonamesAPIkey()); 

		//tr.parseOutputUnitexTei(fileName+".xml",fileName,statsFile,name,statsFileToponyms,statsFileToponyms2);
		toponyms = parsing.execute();
		
		System.out.println("size toponyms : "+toponyms.size());
		
		System.out.println(" --  store toponyms in postgis database");
		objPostgis().connect(objPostgis().db_results());
		objPostgis().storeToponyms(tableName, toponyms);
		objPostgis().close();
		
	}
	
}
catch(Exception e)
{

	System.out.println("Filename = "+name);
 */	
	//suppression des fichiers
	/*
	File f = new File (_outputDir + _user + "/" + name + "/");
	if (f.exists())
	{
		FileTools.delete(f);
	}
	*/
/*
	System.err.println("Erreur test : "+e.toString());
	System.exit(1);
}
	*/
		System.out.println(" +  doToponymsResolution : "+doToponymsResolution());
		if(doToponymsResolution())
		{
				toponyms = launchToponymsResolution(outputDir, name);
		}
//JMA 05/07/2017 FIN
		
		
		
		
		long timeResolution = System.currentTimeMillis();
		
		/**
		 * Toponym disambiguation
		 */
		System.out.println(" +  doToponymsDisambiguation : "+doToponymsDisambiguation());
		
		if(doToponymsDisambiguation())
		{
			
			if(!doToponymsResolution())
			{
				toponyms = ParsingPerdidoTEI.loadToponymsFromJson(fileName+".json");
				
				System.out.println("size toponyms : "+toponyms.size());
				
				System.out.println(" --  store toponyms in postgis database");
				objPostgis().connect(objPostgis().db_results());
				objPostgis().storeToponyms(tableName, toponyms);
				objPostgis().close();
				//tr.loadTopo(fileName+".json"); //dans _vecTopo
			}
			System.out.println(" +  doClustering : "+doClustering());
			
			if(doClustering())
			{					
				
				//tr.vector2Bdd(tableName,toponyms);
				//store toponyms in postgis database

						
				System.out.println(" --- nb of toponyms before clustering :"+toponyms.size());
				
				
				//System.out.println("debug 2");
			//	tr.clustering(tableName);
				DensityClustering dc = new DensityClustering(objPostgis(),tableName);
				dc.runClustering();
				
				int bestCluster = DensityClustering.selectBestCluster(objPostgis(),tableName);
				
				//int bestCluster = tr.bestCluster(tableName);
				
				
				System.out.println(" + bestcluster : "+ bestCluster);
				
				//affect le numéro de cluster à chaque toponyms
				toponyms = updateToponymsCluster(objPostgis(), tableName, bestCluster, toponyms);
				//toponyms = tr.updateVecTopo(tableName, bestCluster, toponyms);
				
			
				/*
				 * test clustering : how many toponyms do we need to find the same best cluster
				 */
				if(doTestClustering())
				{
				//	int size = testClustering(tr,name,toponyms);
				}
				
					
				Vector<Toponyme> topoFilter = new Vector<Toponyme>();
				//on ne garde que les toponyms qui sont dans le meilleur cluster
				for(int i=0;i<toponyms.size();i++)
				{
					if(toponyms.get(i).isBest())
						topoFilter.add(toponyms.get(i));
				}
				
				toponyms.clear();
				toponyms.addAll(topoFilter);
				
						
				System.out.println(" --- nb of toponyms after clustering :"+toponyms.size());
				
	
			}
				
			
			//conserve uniquement les toponyms qui sont dans un élément spatial dans tr._vecTopoFilter
			//tr.filterTopo(fileName+".xml"); 

			//tr.filterTopo(fileName+".xml"); //étendre cette fonction pour ne garder les toponyms en fonction de leur type
			
			//statsDisambiguation += toponyms.size()+";";
			
			System.out.println(" --- nb of toponyms before removeDuplicatePoints :"+toponyms.size());
			toponyms = DuplicatePoints.removeDuplicatePoints(toponyms);
			
			//toponyms = tr.toponymMatching(toponyms);
			System.out.println(" --- nb of toponyms after removeDuplicatePoints :"+toponyms.size());
		}
			
		long timeDisambiguation = System.currentTimeMillis();
	
		/**
		 * Itinerary reconstruction
		 */
		System.out.println(" +  doItineraryReconstruction : "+doItineraryReconstruction());
		
		if(doItineraryReconstruction())
		{
			if(toponyms.size() > 1)	
			{
				//armonisation des ID, lorsqu'un toponyms existe plusieurs fois
				System.out.println(" --> set ID");
				toponyms = setID(toponyms);
				
				
				//récupère l'élévation
				//System.out.println(" --> get Elevation");
				if(getElevation())
				{
					System.out.println(" --> set Elevation");
					toponyms = setElevations(toponyms);
				}
				
				//récupère le code du land cover
				//System.out.println(" --> get Corine Land Cover");
				if(getCorineLandCover())
				{
					System.out.println(" --> set Corine Land Cover");
					toponyms = setCLC(toponyms);
				}
			
				
				//reconstruction de l'itinéraire
				Vector<Edge> path = ItineraryReconstruction.process(toponyms,fileName,criterion, googleMapsAPIkey());
				
				if(path != null)
				{
					//_path = path;
					System.out.println("--> createGPX");
					ItineraryReconstruction.createGPX(path,fileName+"_auto");
					
					
					System.out.println("--> gpx2vector");
					//JMA 28/06/2016	
					//	Vector<Toponyme> realPath = ItineraryReconstruction.gpx2vector(fileName+".gpx");
						Vector<Toponyme> realPath = ItineraryReconstruction.gpx2vector(fileName+"_auto.gpx");
					
					
					ItineraryReconstruction.compareTrajectrories(path, realPath, objPostgis(), outputDir()+user()+"/statsReconstruction.csv",name, toponyms);
				
					
					toponyms = setIdPath(toponyms,path);
					
					//String criterion = "";
					/*
					criterion = "multicriteria_1"; //distance + text order
					criterion = "multicriteria_2";
					criterion = "multicriteria_3";
					criterion = "multicriteria_4";
					criterion = "multicriteria_5";
					criterion = "multicriteria_6";
					criterion = "multicriteria_7";
					//criterion = "multicriteria_7bis";
					*/
					FileTools.delete(new File(fileName + "_autoEdges_"+criterion+".csv"));
					
				}
			}
		}
		//renseigner le path dans un fichier json ou gpx
		//pour ensuite le lire directement sur la google maps
		
		//GoogleMapsAPI.getElevation(toponyms.get(0));
		
		
		if(doToponymsResolution() || doToponymsDisambiguation())
		{
			createJSON_TEI(fileName+".json",toponyms);
		}
		
		//cptES_text = tr.getCptES_text();
		//cptVT_text = tr.getCptVT_text();
		//nbTopoVerb = tr.getNbTopoVerb();
		//cptES = tr.getCptES();
		
		//System.err.println("timeDisambiguation : "+timeDisambiguation);
		//System.err.println("System.currentTimeMillis() : "+System.currentTimeMillis());
		
		long timeReconstruction = System.currentTimeMillis();

		long endLocal = System.currentTimeMillis();
		
		System.out.println("**************************************");
		System.out.println("Annotation duration : "+((float) (timeAnnotation - beginLocal)) / 1000f +" s");
		System.out.println("Resolution duration : "+((float) (timeResolution - timeAnnotation)) / 1000f +" s");
		System.out.println("Disambiguation duration : "+((float) (timeDisambiguation - timeResolution)) / 1000f +" s");
		System.out.println("Reconstruction duration : "+((float) (timeReconstruction - timeDisambiguation)) / 1000f +" s");
		System.out.println("Total duration : "+((float) (endLocal - beginLocal)) / 1000f +" s");
		System.out.println("**************************************");
		
		
	}
	
	
	
	
	
	
	
	//JMA 05/07/2017 DEBUT
		/**
		 * launch the toponyms resolution
		 * @param content				textual description 
		 * @param outputDir				path of the output directory
		 * @param name					name of the document
		 * @throws Exception
		 */
		public Vector<Toponyme> launchToponymsResolution(String outputDir, String name) throws Exception
		{			
			System.out.println("--  launchToponymsResolution");
			
			String fileName = outputDir+"/"+name;
			String tableName = user()+"_"+name+"_clust";
			
			System.out.println(" +  name : "+name);
					
			
			XMLindent.indentXMLFile(fileName+".xml");
			
			
			
			
			/**
			 * Toponym resolution
			 */
			Vector<Toponyme> toponyms = null;
			
			System.out.println(" +  doToponymsResolution : "+doToponymsResolution());
			System.out.println(" --  fileName : "+fileName);
			System.out.println(" --  lang : "+lang());
			System.out.println(" --  strictQuery : "+strictQuery());
			System.out.println(" --  maxResult : "+Integer.parseInt(maxResults()));
			System.out.println(" --  geonames : "+geonames());
			System.out.println(" --  nationalGazetteer : "+nationalGazetteer());
			System.out.println(" --  osm : "+osm());
			System.out.println(" --  ign_bdd_tables : "+ign_bdd_tables());
			System.out.println(" --  geom : "+geom());
			System.out.println(" --  objPostgis : "+objPostgis());
			System.out.println(" --  geonamesAPIkey : "+geonamesAPIkey());
			
			//ToponymResolution tr = new ToponymResolution(strictQuery(), Integer.parseInt(maxResults()), lang(), geonames(), nationalGazetteer(), osm(),ign_bdd_tables(), geom(), objPostgis());
			//ParsingPerdidoTEI parsing = new ParsingPerdidoTEI(fileName+".xml", lang(), strictQuery(), Integer.parseInt(maxResults()), geonames(), nationalGazetteer(), osm(),ign_bdd_tables(), geom(), objPostgis()) 
			try{
				
				if(doToponymsResolution())
				{
					ParsingPerdidoTEI parsing = new ParsingPerdidoTEI(fileName+".xml", lang(), strictQuery(), Integer.parseInt(maxResults()), geonames(), nationalGazetteer(), osm(),ign_bdd_tables(), geom(), objPostgis(),geonamesAPIkey()); 

					//tr.parseOutputUnitexTei(fileName+".xml",fileName,statsFile,name,statsFileToponyms,statsFileToponyms2);
					toponyms = parsing.execute();
					
					System.out.println("size toponyms : "+toponyms.size());
					
					System.out.println(" --  store toponyms in postgis database");
					objPostgis().connect(objPostgis().db_results());
					objPostgis().storeToponyms(tableName, toponyms);
					objPostgis().close();
					
				}
				
			}
			catch(Exception e)
			{

				System.out.println("Filename = "+name);
				//suppression des fichiers
				/*
				File f = new File (_outputDir + _user + "/" + name + "/");
				if (f.exists())
				{
					FileTools.delete(f);
				}
				*/
				System.err.println("Erreur test : "+e.toString());
				System.exit(1);
			}
			return toponyms;
		}

		//JMA 05/07/2017 FIN
	
	
	
	/**
	 * createJSON : create a JSON file from the vector of toponyms
	 * @param output 			path of the output JSON file to create
	 */
	protected void createJSON_TEI(String output,Vector<Toponyme> toponyms) throws Exception
	{
		System.out.println("Begin createJSON");
		String content = "";

		PrintWriter out = new PrintWriter(new FileWriter(output));

		out.println("[");
		//parcours le vecTopo pour remplir le fichier JSON
		//ajouter le nombre de chaque toponyme

		Iterator<Toponyme> it = toponyms.iterator();
		while(it.hasNext())
		{
		    Toponyme toponyme = it.next();
		    
		    String description = "";
		    String verbValue = "";
		    String verbPolarity = "";
		    String verbLemma = "";
		    if(toponyme.getVerb() != null)
		    {
		    	verbValue = toponyme.getVerb().getValue();
		    	verbPolarity = toponyme.getVerb().getPolarity();
		    	verbLemma = toponyme.getVerb().getLemma();
		    	
		    	description += "Verbe : "+verbValue+"("+toponyme.getVerb().getType();
		    
			    if(toponyme.getVerb().getType().equals("deplacement"))
			    	description += " "+verbPolarity;
			    	
			    description += ")";
		    }
		    
		    /*
		     * on compte le nombre de toponyme egale au courant.
		     */
		    int compteur = 0;
		    Iterator<Toponyme> it2 = toponyms.iterator();
			while(it2.hasNext())
			{
				if(toponyme.getId() == it2.next().getId())
					compteur++;
			}
		    
		    content = "{" +
		    		"\"gid\": \""+toponyme.getGid()+"\"," +
					"\"id\": \""+toponyme.getId()+"\"," +
					"\"iid\": \""+toponyme.getIid()+"\"," +
					"\"idPath\": \""+toponyme.getIdPath()+"\"," +
		    		"\"title\": \""+toponyme.getName()+"\"," +
		    		"\"findName\": \""+toponyme.getValue()+"\","+
		    		"\"country\": \""+toponyme.getCountry()+"\"," +
		    		"\"continent\": \""+toponyme.getContinent()+"\","+
		    		"\"feature\": \""+toponyme.getFeature()+"\","+
		    		"\"featureText\": \""+toponyme.getFeatureText()+"\","+
					"\"lat\": \""+toponyme.getLat()+"\"," +
					"\"lng\": \""+toponyme.getLng()+"\"," +
					"\"elevation\": \""+toponyme.getElevation()+"\"," +
					"\"clc\": \""+toponyme.getClcCode()+"\"," +
					"\"perception\": \""+toponyme.getPerception()+"\"," +
					"\"negation\": \""+toponyme.getNegation()+"\"," +
					"\"src\": \""+toponyme.getSource()+"\"," +
					"\"nb\": \""+compteur+"\"," +
					"\"cluster\": \""+toponyme.getCluster()+"\"," +
					"\"isBest\": \""+toponyme.isBest()+"\","+
					"\"verbe\": \""+verbValue+"\","+
					"\"vlemma\": \""+verbLemma+"\","+
					"\"polarite\": \""+verbPolarity+"\","+
		    		"\"localise\": \""+toponyme.getLocalise()+"\","+
		    "\"relSpatial\": \""+toponyme.getRelSpatial()+"\","+
		    "\"relTemporal\": \""+toponyme.getRelTemporal()+"\",";
		    		
		    content += "\"description\": \""+description+"\"" + "},";
		    
		    out.println(content);
		}
		
		out.println("{}]");
		out.close();
	
		System.out.println("End createJSON");
	}
	
	
	
	/**
	 * updateVecTopo : update the vecTopo with the number of cluster
	 * @param tableName 		name of the table of the postgis database
	 * @param bestCluster		id of the best cluster
	 */
	protected Vector<Toponyme> updateToponymsCluster(Postgis objPostgis, String tableName, int bestCluster, Vector<Toponyme> toponyms) throws Exception {
		
		System.out.println("### UPADTE TOPONYM CLUSTERS ###");
	
		
		//_objPostgis.connect("outputDemo");
		objPostgis.connect(objPostgis.db_results());
		
		Statement state = objPostgis.conn.createStatement();
		ResultSet res = state.executeQuery("SELECT gid,id, clusterid FROM "+tableName);

		while(res.next())
		{ 			
			toponyms.get(res.getInt("gid")).setCluster(res.getInt("clusterid"));
			Statement state2 = objPostgis.conn.createStatement();
			ResultSet res2 = state2.executeQuery("SELECT count(*) FROM "+tableName+" WHERE id="+res.getInt("id"));
			res2.next();
			toponyms.get(res.getInt("gid")).setNb(res2.getInt(1));
			
			toponyms.get(res.getInt("gid")).setGid(res.getInt("gid"));
			
			if(res.getInt("clusterid")==bestCluster)	
			{
				toponyms.get(res.getInt("gid")).setIsBest(true);
				//_vecTopoFilter.add(toponyms.get(res.getInt("gid")));
			}
			
			res2.close();
		}
		res.close();
	
		objPostgis.close();
		
		return toponyms;
	
	}	
	
	
	
	
	
	
	protected Vector<Toponyme> setCLC(Vector<Toponyme> toponyms)
	{
		
	
		CLCArtifactFactory factory;
		factory = new FrenchCLCArtifactFactory();
		CLCFinder finder = factory.makeCLCFinder(1);
	  
		String clcCode = "";
		// factory = new FrenchToponymArtifactFactory();
		// ToponymFinder finder = factory.makeToponymFinder(100);
		for(int i=0;i<toponyms.size();i++)
		{
			if(toponyms.get(i).isBest())
			{
				clcCode = finder.searchCLC(toponyms.get(i).getLng(),toponyms.get(i).getLat(),false);
				toponyms.get(i).setClcCode(clcCode);
				//System.out.println("CLC code : "+clcCode); 
				//System.out.println(finder.searchToponym(45.619064,0.831909,false)); //FR
			}
		}
		
		return toponyms;
	 
	}
	
	protected Vector<Toponyme> setID(Vector<Toponyme> toponyms)
	{
		
		int iid = 0;
		
		System.out.println("toponyms.size() : "+toponyms.size()); 
		
		
		for(int i=0;i<toponyms.size();i++)
		{
	
			
			System.err.println("toponyms.get(i).getIid() : "+toponyms.get(i).getIid()); 
			
			if(toponyms.get(i).getIid() == -1)
			{
				toponyms.get(i).setIid(iid);
				System.out.println("i : "+i+" change iid : "+iid); 
				for(int j=0;j<toponyms.size();j++)
				{
					if(toponyms.get(j).getIid() == -1)
					{
						if(toponyms.get(i).getName().equals(toponyms.get(j).getName()))
						{
							toponyms.get(j).setIid(iid);
							System.out.println("j : "+j+" change iid : "+iid); 
							//System.out.println(finder.searchToponym(45.619064,0.831909,false)); //FR
						}
						
					}
				}
			}
			iid++;
		}
		
		
		
		return toponyms;
	 
	}
	
	protected Vector<Toponyme> setIdPath(Vector<Toponyme> toponyms, Vector<Edge> path)
	{
		System.out.println(" --> Begin setIdPath");
		
		int idPath = -1;
		int both = 0;
		
		
		for(int i=0;i<toponyms.size();i++)
		{
			toponyms.get(i).setIdPath(-1);
		}
		
		for(int j=0;j<path.size();j++)
		{
			for(int i=0;i<toponyms.size();i++)
			{
				if(toponyms.get(i).isBest())
				{
					if(path.get(j).getNodeStart().getGid() == toponyms.get(i).getGid())
					{
							toponyms.get(i).setIdPath(idPath+1);
							both++;
					}
					
					if(path.get(j).getNodeEnd().getGid() == toponyms.get(i).getGid())
					{
							toponyms.get(i).setIdPath(idPath+2);
							both++;
					}
				}
				if(both == 2)
					continue;
			}
			idPath++;
			both = 0;
		}
		
		/*
		
		for(int j=0;j<path.size();j++)
		{
			for(int i=0;i<toponyms.size();i++)
			{
				
				if(path.get(j).getNodeStart().getGid() == toponyms.get(i).getGid())
					toponyms.get(i).setIdPath(idPath+1);
				
				if(path.get(j).getNodeEnd().getGid() == toponyms.get(i).getGid())
					toponyms.get(i).setIdPath(idPath+2);
					
					
				if(toponyms.get(i).isBest())
				{
					if(path.get(j).getNodeStart().getGid() == toponyms.get(i).getGid())
					{
							toponyms.get(i).setIdPath(idPath+1);
							both++;
					}
					
					if(path.get(j).getNodeEnd().getGid() == toponyms.get(i).getGid())
					{
							toponyms.get(i).setIdPath(idPath+2);
							both++;
					}
				}
				if(both == 2)
					continue;
			}
			idPath++;
			both = 0;
		}
		*/
		
		System.out.println(" <-- End setIdPath");
		return toponyms;
	}
	
	protected Vector<Toponyme> setElevations(Vector<Toponyme> toponyms) throws InterruptedException
	{
		//System.out.println(" Begin addElevation");
		
		for(int i=0;i<toponyms.size();i++)
		{
			try {
				if(toponyms.get(i).isBest())
				{
					Thread.sleep(500);
					toponyms.get(i).setElevation(GoogleMapsAPI.getElevation(toponyms.get(i),googleMapsAPIkey()));
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
		
		//System.out.println(" End addElevation");
		return toponyms;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	/**
	 * @return the _user
	 */
	public String user() {
		return _user;
	}




	/**
	 * @param user the _user to set
	 */
	public void user(String user) {
		_user = user;
	}




	/**
	 * @return the _lang
	 */
	public String lang() {
		return _lang;
	}




	/**
	 * @param lang the _lang to set
	 */
	public void lang(String lang) {
		_lang = lang;
	}




	/**
	 * @return the _analyserPOS
	 */
	public String analyserPOS() {
		return _analyserPOS;
	}




	/**
	 * @param analyserPOS the _analyserPOS to set
	 */
	public void analyserPOS(String analyserPOS) {
		_analyserPOS = analyserPOS;
	}




	/**
	 * @return the _inputDir
	 */
	public String inputDir() {
		return _inputDir;
	}




	/**
	 * @param inputDir the _inputDir to set
	 */
	public void inputDir(String inputDir) {
		_inputDir = inputDir;
	}




	/**
	 * @return the _outputDir
	 */
	public String outputDir() {
		return _outputDir;
	}




	/**
	 * @param outputDir the _outputDir to set
	 */
	public void outputDirWebServices(String outputDirWebServices) {
		_outputDirWebServices = outputDirWebServices;
	}

	

	/**
	 * @return the _outputDir
	 */
	public String outputDirWebServices() {
		return _outputDirWebServices;
	}




	/**
	 * @param outputDir the _outputDir to set
	 */
	public void outputDir(String outputDir) {
		_outputDir = outputDir;
	}




	/**
	 * @return the _geonames
	 */
	public boolean geonames() {
		return _geonames;
	}




	/**
	 * @param geonames the _geonames to set
	 */
	public void geonames(boolean geonames) {
		_geonames = geonames;
	}




	/**
	 * @return the _osm
	 */
	public boolean osm() {
		return _osm;
	}




	/**
	 * @param osm the _osm to set
	 */
	public void osm(boolean osm) {
		_osm = osm;
	}




	/**
	 * @return the _nationalGazetteer
	 */
	public boolean nationalGazetteer() {
		return _nationalGazetteer;
	}




	/**
	 * @param nationalGazetteer the _nationalGazetteer to set
	 */
	public void nationalGazetteer(boolean nationalGazetteer) {
		_nationalGazetteer = nationalGazetteer;
	}




	/**
	 * @return the _doDelete
	 */
	public boolean doDelete() {
		return _doDelete;
	}




	/**
	 * @param doDelete the _doDelete to set
	 */
	public void doDelete(boolean doDelete) {
		_doDelete = doDelete;
	}




	/**
	 * @return the _doAnnotation
	 */
	public boolean doAnnotation() {
		return _doAnnotation;
	}




	/**
	 * @param doAnnotation the _doAnnotation to set
	 */
	public void doAnnotation(boolean doAnnotation) {
		_doAnnotation = doAnnotation;
	}


	
	//JMA 05/07/2017 DEBUT
		/**
		 * @return the _doUnitex
		 */
		public boolean doUnitex() {
			return _doUnitex;
		}

		/**
		 * @param doUnitex the _doUnitex to set
		 */
		public void doUnitex(boolean doUnitex) {
			_doUnitex = doUnitex;
		}
		//JMA 05/07/2017 FIN
	
	

	/**
	 * @return the _getElevation
	 */
	public boolean getElevation() {
		return _getElevation;
	}




	/**
	 * @param getElevation the _getElevation to set
	 */
	public void getElevation(boolean getElevation) {
		_getElevation = getElevation;
	}
	
	/**
	 * @return the _getCorineLandCover
	 */
	public boolean getCorineLandCover() {
		return _getCorineLandCover;
	}




	/**
	 * @param getCorineLandCover the _getCorineLandCover to set
	 */
	public void getCorineLandCover(boolean getCorineLandCover) {
		_getCorineLandCover = getCorineLandCover;
	}
	
	
	
	
	
	/**
	 * @return the _doToponymsResolution
	 */
	public boolean doToponymsResolution() {
		return _doToponymsResolution;
	}




	/**
	 * @param doToponymsResolution the _doToponymsResolution to set
	 */
	public void doToponymsResolution(boolean doToponymsResolution) {
		_doToponymsResolution = doToponymsResolution;
	}




	/**
	 * @return the _doToponymsDisambiguation
	 */
	public boolean doToponymsDisambiguation() {
		return _doToponymsDisambiguation;
	}




	/**
	 * @param doToponymsDisambiguation the _doToponymsDisambiguation to set
	 */
	public void doToponymsDisambiguation(
			boolean doToponymsDisambiguation) {
		_doToponymsDisambiguation = doToponymsDisambiguation;
	}




	/**
	 * @return the _strictQuery
	 */
	public boolean strictQuery() {
		return _strictQuery;
	}




	/**
	 * @param strictQuery the _strictQuery to set
	 */
	public void strictQuery(boolean strictQuery) {
		_strictQuery = strictQuery;
	}




	/**
	 * @return the _maxResults
	 */
	public String maxResults() {
		return _maxResults;
	}




	/**
	 * @param maxResults the _maxResults to set
	 */
	public void maxResults(String maxResults) {
		_maxResults = maxResults;
	}




	/**
	 * @return the _ignKey
	 */
	public String ignAPIkey() {
		return _ignAPIkey;
	}




	/**
	 * @param ignKey the _ignKey to set
	 */
	public void ignAPIkey(String ignAPIkey) {
		_ignAPIkey = ignAPIkey;
	}
	
	
	/**
	 * @return the _ignKey
	 */
	public String geonamesAPIkey() {
		return _geonamesAPIkey;
	}




	/**
	 * @param ignKey the _ignKey to set
	 */
	public void geonamesAPIkey(String geonamesAPIkey) {
		_geonamesAPIkey = geonamesAPIkey;
	}
	
	/**
	 * @return the _ignKey
	 */
	public String googleMapsAPIkey() {
		return _googleMapsAPIkey;
	}




	/**
	 * @param ignKey the _ignKey to set
	 */
	public void googleMapsAPIkey(String googleMapsAPIkey) {
		_googleMapsAPIkey = googleMapsAPIkey;
	}




	/**
	 * @return the _doPOS
	 */
	public boolean doPOS() {
		return _doPOS;
	}




	/**
	 * @param doPOS the _doPOS to set
	 */
	public void doPOS(boolean doPOS) {
		_doPOS = doPOS;
	}




	/**
	 * @return the _useUnitexDictionary
	 */
	public boolean useUnitexDictionary() {
		return _useUnitexDictionary;
	}




	/**
	 * @param useUnitexDictionary the _useUnitexDictionary to set
	 */
	public void useUnitexDictionary(boolean useUnitexDictionary) {
		_useUnitexDictionary = useUnitexDictionary;
	}


	/**
	 * @return the _doClustering
	 */
	public boolean doClustering() {
		return _doClustering;
	}




	/**
	 * @param doClustering the _doClustering to set
	 */
	public void doClustering(boolean doClustering) {
		_doClustering = doClustering;
	}




	/**
	 * @return the _doTestClustering
	 */
	public boolean doTestClustering() {
		return _doTestClustering;
	}




	/**
	 * @param doTestClustering the _doTestClustering to set
	 */
	public void doTestClustering(boolean doTestClustering) {
		_doTestClustering = doTestClustering;
	}




	/**
	 * @return the _doSpanningTree
	 */
	public boolean doSpanningTree() {
		return _doSpanningTree;
	}




	/**
	 * @param doSpanningTree the _doSpanningTree to set
	 */
	public void doSpanningTree(boolean doSpanningTree) {
		_doSpanningTree = doSpanningTree;
	}




	/**
	 * @return the _doBoundingBox
	 */
	public boolean doBoundingBox() {
		return _doBoundingBox;
	}




	/**
	 * @param doBoundingBox the _doBoundingBox to set
	 */
	public void doBoundingBox(boolean doBoundingBox) {
		_doBoundingBox = doBoundingBox;
	}




	/**
	 * @return the _showMissingPoints
	 */
	public boolean showMissingPoints() {
		return _showMissingPoints;
	}




	/**
	 * @param showMissingPoints the _showMissingPoints to set
	 */
	public void showMissingPoints(boolean showMissingPoints) {
		_showMissingPoints = showMissingPoints;
	}




	/**
	 * @return the _showAmbiguities
	 */
	public boolean showAmbiguities() {
		return _showAmbiguities;
	}




	/**
	 * @param showAmbiguities the _showAmbiguities to set
	 */
	public void showAmbiguities(boolean showAmbiguities) {
		_showAmbiguities = showAmbiguities;
	}




	/**
	 * @return the _doStats
	 */
	public boolean doStats() {
		return _doStats;
	}




	/**
	 * @param doStats the _doStats to set
	 */
	public void doStats(boolean doStats) {
		_doStats = doStats;
	}




	/**
	 * @return the _doItineraryReconstruction
	 */
	public boolean doItineraryReconstruction() {
		return _doItineraryReconstruction;
	}




	/**
	 * @param doItineraryReconstruction the _doItineraryReconstruction to set
	 */
	public void doItineraryReconstruction(
			boolean doItineraryReconstruction) {
		_doItineraryReconstruction = doItineraryReconstruction;
	}




	/**
	 * @return the _listTaggers
	 */
	public String[] listTaggers() {
		return _listTaggers;
	}
	
	
	/**
	 * @return the _listTaggers
	 */
	public String[] ign_bdd_tables() {
		return _ign_bdd_tables;
	}


	public void ign_bdd_tables(String[] ign_bdd_tables){
		_ign_bdd_tables = ign_bdd_tables;
	}


	/**
	 * @param listTaggers the _listTaggers to set
	 */
	public void listTaggers(String[] listTaggers) {
		_listTaggers = listTaggers;
	}




	/**
	 * @return the _mailContact
	 */
	public String[] mailContact() {
		return _mailContact;
	}




	/**
	 * @param mailContact the _mailContact to set
	 */
	public void mailContact(String[] mailContact) {
		_mailContact = mailContact;
	}




	/**
	 * @return the _suffixPOS
	 */
	public boolean suffixPOS() {
		return _suffixPOS;
	}




	/**
	 * @param suffixPOS the _suffixPOS to set
	 */
	public void suffixPOS(boolean suffixPOS) {
		_suffixPOS = suffixPOS;
	}




	/**
	 * @return the _uriUnitexApp
	 */
	public String uriUnitexApp() {
		return _uriUnitexApp;
	}




	/**
	 * @param uriUnitexApp the _uriUnitexApp to set
	 */
	public void uriUnitexApp(String uriUnitexApp) {
		_uriUnitexApp = uriUnitexApp;
	}




	/**
	 * @return the _uriUnitex
	 */
	public String uriUnitex() {
		return _uriUnitex;
	}
	



	/**
	 * @param uriUnitex the _uriUnitex to set
	 */
	public void uriUnitex(String uriUnitex) {
		_uriUnitex = uriUnitex;
	}

	
	
	/**
	 * @return the _unitexVersion
	 */
	public String unitexVersion() {
		return _unitexVersion;
	}

	/**
	 * @param uriUnitex the _uriUnitex to set
	 */
	public void unitexVersion(String unitexVersion) {
		_unitexVersion = unitexVersion;
	}

	/**
	 * @return the _analysisCascade
	 */
	public String analysisCascade() {
		return _analysisCascade;
	}




	/**
	 * @param analysisCascade the _analysisCascade to set
	 */
	public void analysisCascade(String analysisCascade) {
		_analysisCascade = analysisCascade;
	}




	/**
	 * @return the _synthesisCascade
	 */
	public String synthesisCascade() {
		return _synthesisCascade;
	}




	/**
	 * @param synthesisCascade the _synthesisCascade to set
	 */
	public void synthesisCascade(String synthesisCascade) {
		_synthesisCascade = synthesisCascade;
	}




	/**
	 * @return the _uriTreeTagger
	 */
	public String uriTreeTagger() {
		return _uriTreeTagger;
	}




	/**
	 * @param uriTreeTagger the _uriTreeTagger to set
	 */
	public void uriTreeTagger(String uriTreeTagger) {
		_uriTreeTagger = uriTreeTagger;
	}

	
	//lmoncla 19.10.2018 ajout du mode client/server de Talismane

	/**
	 * @return the _talismanePort
	 */
	public int talismanePort() {
		return Integer.parseInt(_talismanePort);
	}


	/**
	 * @return the _talismaneHost
	 */
	public String talismaneHost() {
		return _talismaneHost;
	}



	/**
	 * @return the _uriMelt
	 */
	public String uriMelt() {
		return _uriMelt;
	}




	/**
	 * @param uriMelt the _uriMelt to set
	 */
	public void uriMelt(String uriMelt) {
		_uriMelt = uriMelt;
	}




	/**
	 * @return the _uriFreeling
	 */
	public String uriFreeling() {
		return _uriFreeling;
	}
	
	public String uriStanfordNLP(String lang) {
		
		if(lang.equals("English"))
			return _uriStanfordNLP_EN;
		if(lang.equals("French"))
			return _uriStanfordNLP_FR;
		if(lang.equals("Spanish"))
			return _uriStanfordNLP_SP;
		
		return _uriStanfordNLP_EN;
		
	}
	



	/**
	 * @param uriFreeling the _uriFreeling to set
	 */
	public void uriFreeling(String uriFreeling) {
		_uriFreeling = uriFreeling;
	}




	/**
	 * @return the _uriLangFreeling
	 */
	public String uriLangFreeling() {
		return _uriLangFreeling;
	}




	/**
	 * @param uriLangFreeling the _uriLangFreeling to set
	 */
	public void uriLangFreeling(String uriLangFreeling) {
		_uriLangFreeling = uriLangFreeling;
	}




	/**
	 * @return the _uriPOStags
	 */
	public String uriPOStags() {
		return _uriPOStags;
	}




	/**
	 * @param uriPOStags the _uriPOStags to set
	 */
	public void set_uriPOStags(String uriPOStags) {
		_uriPOStags = uriPOStags;
	}




	/**
	 * @return the _characterEncoding
	 */
	public String characterEncoding() {
		return _characterEncoding;
	}




	/**
	 * @param characterEncoding the _characterEncoding to set
	 */
	public void characterEncoding(String characterEncoding) {
		_characterEncoding = characterEncoding;
	}




	/**
	 * @return the _geom
	 */
	public String geom() {
		return _geom;
	}




	/**
	 * @param geom the _geom to set
	 */
	public void geom(String geom) {
		_geom = geom;
	}




	/**
	 * @return the _pgsql2shp
	 */
	public String pgsql2shp() {
		return _pgsql2shp;
	}




	/**
	 * @param pgsql2shp the _pgsql2shp to set
	 */
	public void pgsql2shp(String pgsql2shp) {
		_pgsql2shp = pgsql2shp;
	}




	/**
	 * @return the _urlPstg
	 */
	public String urlPstg() {
		return _urlPstg;
	}




	/**
	 * @param urlPstg the _urlPstg to set
	 */
	public void urlPstg(String urlPstg) {
		_urlPstg = urlPstg;
	}




	/**
	 * @return the _portPstg
	 */
	public String portPstg() {
		return _portPstg;
	}




	/**
	 * @param portPstg the _portPstg to set
	 */
	public void portPstg(String portPstg) {
		_portPstg = portPstg;
	}




	/**
	 * @return the _userPstg
	 */
	public String userPstg() {
		return _userPstg;
	}




	/**
	 * @param userPstg the _userPstg to set
	 */
	public void userPstg(String userPstg) {
		_userPstg = userPstg;
	}




	/**
	 * @return the _passwordPstg
	 */
	public String passwordPstg() {
		return _passwordPstg;
	}




	/**
	 * @param passwordPstg the _passwordPstg to set
	 */
	public void passwordPstg(String passwordPstg) {
		_passwordPstg = passwordPstg;
	}




	/**
	 * @return the _db_users
	 */
	public String db_users() {
		return _db_users;
	}




	/**
	 * @param db_users the _db_users to set
	 */
	public void db_users(String db_users) {
		_db_users = db_users;
	}




	/**
	 * @return the _db_results
	 */
	public String db_results() {
		return _db_results;
	}




	/**
	 * @param db_results the _db_results to set
	 */
	public void db_results(String db_results) {
		_db_results = db_results;
	}





	/**
	 * @return the _db_users
	 */
	public String suffixeTablePgsql() {
		return _suffixeTablePgsql;
	}




	/**
	 * @param db_users the _db_users to set
	 */
	public void suffixeTablePgsql(String suffixeTablePgsql) {
		_suffixeTablePgsql = suffixeTablePgsql;
	}
	
	
	
	
	

	/**
	 * @return the _path
	 */
	public Vector<Edge> path() {
		return _path;
	}




	/**
	 * @param path the _path to set
	 */
	public void path(Vector<Edge> path) {
		_path = path;
	}




	/**
	 * @return the _objPostgis
	 */
	public Postgis objPostgis() {
		return _objPostgis;
	}




	/**
	 * @param objPostgis the _objPostgis to set
	 */
	public void objPostgis(Postgis objPostgis) {
		_objPostgis = objPostgis;
	}
	
	
	
	
}
