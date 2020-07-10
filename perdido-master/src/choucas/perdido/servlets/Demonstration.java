package choucas.perdido.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet; // inconnu en tomcat 5.5
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;

import choucas.perdido.postgresql.Postgis;
import choucas.perdido.postgresql.User;

import choucas.perdido.disambiguation.DensityClustering;
import choucas.perdido.elements.Toponyme;
import choucas.perdido.elements.Verb;

import choucas.perdido.tools.FileTools;
import choucas.perdido.tools.Mail;
import choucas.perdido.tools.StringTools;
import choucas.perdido.tools.XmlTools;
import choucas.perdido.maps.GoogleMaps;
import choucas.perdido.processing.Perdido;
import choucas.perdido.tools.MapsFunctions;


/**
 * Servlet implementation class HelloServlet
 */
@WebServlet("/Demonstration")
// inconnu en tomcat 5.5
public class Demonstration extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public static final String URL = "/demonstration.jsp";
	public static final String CHAMP_CONTENT = "content";
	
	
	
	
	public static int _nbCharacterMax = 0;

	public long _begin;
	public float _timePOS;
	public float _timeTag;
	public float _timeTR; 
	public float _timeInit;
	public float _timeSave; 
	public long _end;
		
	public boolean _verbose = false;


	/**
	 * 
	 */
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

		System.out.println("** GET ** ");
		
		// Recupere la session
    	HttpSession session = request.getSession(true);
    	
    	try {
	    	Perdido pr = new Perdido();
			pr.loadParams();
			
			
			
			String gpsUri = getServletContext().getRealPath("CorpusRando"); 
			
			//request.setAttribute("resultat", viewAnnotation(pr.getOutputDir(),"/example"));
			//request.setAttribute("map", viewMap("example",pr.getOutputDir()+"/example/example.json",0,false));
			
			request.setAttribute("action", "");
			
			
	    	if(request.getParameter("act") != null)
			{
	    		String action =  request.getParameter("act");
	    		
	    		if(action.equals("example"))	
	    		{
	    			System.out.println("  -- example");
	    			
	    			if(request.getParameter("view") != null)
	    			{
	    	    		String name =  request.getParameter("view");
	    	    		
		    			request.setAttribute("action", "example");
		    			request.setAttribute("list", "");
		    			
		    			String result = viewAnnotation(pr.outputDir()+"/example/",name);
		    			if(result.isEmpty())
		    			{
		    				request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured.\");})</script>");
		    				//request.setAttribute("resultExample", "");
		    				//request.setAttribute("mapExample", "");
		    				//request.setAttribute("option", "");
		    				
		    			}
		    			else
		    			{
		    				request.setAttribute("resultExample", result);
		    			
		    			
			    			int bestCluster = -1;
			    			/*
			    			try{ 
			    				bestCluster = pr.bestCluster(name+"_clust");
							}
							catch(Exception e){}
			    			*/
			    			
			    			String tableName = pr.user()+"_"+name+pr.suffixeTablePgsql();
			    			
			    			String viewMap = viewMap(name,pr.outputDir()+"/example/"+name+".json",bestCluster,false,"mapExample",false,false,pr.objPostgis(),tableName,pr.ignAPIkey(),gpsUri);
			    			
			   
			    		
			    			request.setAttribute("mapExample", viewMap);
			    			request.setAttribute("option", viewOption("example"));
		    			}
	    			}
	    		}
	    		else
	    		{
	    		
	    		
	    		
	    			try{ 
	    				pr.user(session.getAttribute("username").toString()); 
					}
					catch(Exception e)
					{					
						//request.setAttribute("authentification","You must be logged in to view this page!");
						this.getServletContext().getRequestDispatcher(URL).forward(request, response);
						return;
					}
		    		System.out.println("  -- OutputDir : "+pr.outputDir()+pr.user());
		    		
		    		
		    		if(action.equals("logout"))	
		    		{
		    			session.invalidate();
				    	//request.setAttribute("authentification","You're now logged out.");
				    	request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"success\",\"You're now logged out.\");})</script>");
				    	
				    	this.getServletContext().getRequestDispatcher(URL).forward(request, response);
				    	return;
		    		}
		    		
		    		if(action.equals("list"))	
		    		{
		    			System.out.println("  -- list");
		    			request.setAttribute("action", "list");
		    			request.setAttribute("list", FileTools.listeFile(pr.outputDir()+pr.user(),pr.listTaggers()));
		    			request.setAttribute("resultat", "");
		    			request.setAttribute("map", "");
		    		}
		    		
		    		if(action.equals("new"))	
		    		{
		    			System.out.println("  -- new document");
		    			request.setAttribute("action", "new");
		    			request.setAttribute("resultat", "");
		    			request.setAttribute("map", "");
		    		}
		    		
		    		if(action.equals("view"))	
		    		{
		    			if(request.getParameter("view") != null)
		    			{
		    	    		
		    	    		request.setAttribute("action", "view");
		    				request.setAttribute("list", FileTools.listeFile(pr.outputDir()+pr.user(),pr.listTaggers()));
		    				
		    				String name =  request.getParameter("view");
		    				
		    				System.out.println("   -- view file : "+ name);
		    				
		    				if(FileTools.fileExist(pr.outputDir()+pr.user()+"/"+name))
		    				{
		    					
		    					String result = viewAnnotation(pr.outputDir()+pr.user()+"/"+name+"/",name);
				    			if(result.isEmpty())
				    			{
				    				request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured.\");})</script>");
				    				request.setAttribute("resultat", "");
				    				request.setAttribute("resultat2", "");
				    				request.setAttribute("map", "");
				    				request.setAttribute("option", "");
				    			}
				    			else
				    			{
				    				
				    				
				    				String r = "<div class='resultat' ><h3>Result ("+name+")</h3>";
				    				
				    				r += viewDownloadButtons(name);
				    				
				    				r += result;
				    				
				    				
				    				
				    				r += "<div id='legende' data-role=\"collapsible\" data-collapsed-icon=\"carat-d\" data-expanded-icon=\"carat-u\" data-mini=\"true\">" +
				    						
								"<h4>Caption</h4>" +
								"<div>Verb of <span class=\"verbeDeplacement\">displacement</span> | <span class=\"verbePerception\">perception</span>" +
								" | <span class=\"verbeTopographique\">topographic</span> | <span class=\"verbePosition\">position</span><br/>" +
								"Spatial named entity <span class=\"es\">candidate</span> | <span class=\"esC\">confirmed</span> <br/>" +
								"Toponym <span class=\"topoCandidat\">candidat</span> | <span class=\"nomToponymique\">confirmed</span><br/>" +
								"<span class=\"vpe\">VT structure</span><br/><span class=\"indirection\">Indirection</span><br/>" +
								"<span class=\"nomCommun\">Common Noun</span><br/><span class=\"nomPropre\">Proper Noun</span></div>" +
								"</div>";
				    				
				    				request.setAttribute("resultat", r);
				    			
		    					
			    					//request.setAttribute("resultat", viewAnnotation(pr.getOutputDir()+pr.getUser()+"/"+name+"/",name,pr.getVersion()));
			    					request.setAttribute("resultat2", "<h3>Map</h3>");
			    				//	String tableName = pr.user()+"_"+name+"_clust";
			    					String tableName = pr.user()+"_"+name+pr.suffixeTablePgsql();
			    					//System.out.println("   -- pr.getTableName() : "+ pr.getTableName());
			    					int bestCluster = -1;
					    			
					    			try{ 
					    				
					    				
					    				bestCluster = DensityClustering.selectBestCluster(pr.objPostgis(), tableName);
					    				//bestCluster = pr.bestCluster(pr.getTableName());
					 
									}
									catch(Exception e){}
					    			
					    			System.out.println("   -- bestCluster : "+ bestCluster);
					    			
					    			//bestCluster = -1;
					    			//boolean showAmbiguities = true;
					    			String view_map = "";
					    			try
		    						{
		    							view_map = viewMap(name,pr.outputDir()+pr.user()+"/"+name+"/"+name+".json",bestCluster,pr.showAmbiguities(),"map_canvas",pr.doSpanningTree(),pr.doBoundingBox(),pr.objPostgis(),tableName,pr.ignAPIkey(),gpsUri);
		    						}
		    						catch(Exception e)
		    						{
		    							System.err.println(" Not enough points : viewMap()!");
		    						}
					    			
			    					request.setAttribute("map", view_map);
			    					
			    					
			    					if(session.getAttribute("admin") != null)
			    					{	
			    						request.setAttribute("option", viewOption("admin"));
			    					}
			    					else
			    						request.setAttribute("option", viewOption("user"));
			    					
			    					String missingPoints_listHtml = "";
			    					if(pr.showMissingPoints())
			    					{
			    						missingPoints_listHtml = viewMissingPoints(pr.outputDir()+pr.user()+"/"+name+"/"+name+".json",pr.objPostgis(),tableName,bestCluster);
			    					}
		    					
				    			}
		    					//request.setAttribute("missingPoints", missingPoints_listHtml);
		    					
		    				}
		    				else
		    				{
		    					request.setAttribute("resultat", "<div class='resultat'><span class='erreur'>This file doesn't exist!</span></div>");
		    					request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured.\");})</script>");
		    					this.getServletContext().getRequestDispatcher(URL).forward(request, response);
		    					return;
		    				}	
		    			}
		    		}
				}	
			}
    	}
		catch(Exception e){
			request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured.\");})</script>");
			this.getServletContext().getRequestDispatcher(URL).forward(request, response);
			return;
		}
    	
    	
    	System.out.println("** END GET ** ");
    	
    	this.getServletContext().getRequestDispatcher(URL).forward(request, response);
		return;
	}
	

	
	/**
	 * 
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
                                                                                   
		System.out.println("** POST ** ");
	
		
		HttpSession session = request.getSession(true);
    	
		
		
		
		//.out.println("*referer; : "+referer);
		
		
		Perdido pr = new Perdido();
		try {
			pr.loadParams();
			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(request.getParameter("act") != null)
		{
    		String action =  request.getParameter("act");
    		
    		System.out.println("act : "+action);
    		
    		if(action.equals("askPassword"))	
    		{
    			System.out.println("Ask Password");
    			
    			
    			String name = request.getParameter("name");
    			String family = request.getParameter("family");
    			String company = request.getParameter("company");
    			String email = request.getParameter("email");
    			String login = request.getParameter("login");
    			String password = request.getParameter("password");
    			
    			
    			if(!name.isEmpty() && !family.isEmpty() && !company.isEmpty() && !email.isEmpty() && !login.isEmpty() && !password.isEmpty())
    			{
    				
    				User user = new User();
    				try {
    					
    					//tester si le username est déjà utilisé
    					
    					if(!user.checkUser(login))
    					{
    					
							user.addUser(email, login, password, 0, name, family, company, 50, 0);
							request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"success\",\"Your request has been sent.\");})</script>");
							
							
							String msg = "Name : "+name+"\n";
			    			msg += "Family name : "+family+"\n";
			    			msg += "Company/Institution : "+company+"\n";
			    			msg += "email : "+email+"\n";
			    			msg += "login : "+login+"\n";
			    			//msg += "password : "+password+"\n";
			    			
			    			String[] mails = pr.mailContact();
			    			for(int i = 0;i<mails.length;i++)
			    				Mail.sendMail("PERDIDO - New user",msg,mails[i]);
		    			
    					}
    					else
    					{
    						request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"This username already exists.\");})</script>");
    					}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured.\");})</script>");
					}
    				
	    			
	    			
	    		//	request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"success\",\"Your request has been sent.\");})</script>");
    			}
    			else
    			{
    				//retourner un msg d'erreur
    				request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"Please fill in all fields.\");})</script>");
    			}
    			
    			this.getServletContext().getRequestDispatcher(URL).forward(request, response);
 			    return;
    		}
    		
    		if(action.equals("contact"))	
    		{
    			System.out.println("contact");
    			
    			
    			
    			String email = request.getParameter("email");
    			String text = request.getParameter("text");
    			
    			
    			if(!email.isEmpty() && !text.isEmpty() )
    			{
    				
	    			String msg = "Message from : "+email+"\n\n";
	    			
	    			msg += text;
	    			
	    			
	    			
	    			String[] mails = pr.mailContact();
	    			for(int i = 0;i<mails.length;i++)
	    				Mail.sendMail("PERDIDO - Contact",msg,mails[i]);
	    			
	    			request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"success\",\"Your message has been sent.\");})</script>");
	    			
    			}
    			else
    			{
    				//retourner un msg d'erreur
    				request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"Please fill in all fields.\");})</script>");
    			}
    			
    			
    			this.getServletContext().getRequestDispatcher(URL).forward(request, response);
 			    return;
    			
    		}
    		
    		if(action.equals("changePassword"))	
    		{
    			
    			System.out.println("Change password");
    			try{
    				String oldPassword = request.getParameter("oldPassword");
    			    String newPassword = request.getParameter("newPassword");
    				
    			    if(!oldPassword.isEmpty() && !newPassword.isEmpty() )
    			    {
    			    	User user = new User();
	    				if(user.changePassword(session.getAttribute("username").toString(), oldPassword, newPassword))
	    				{
	    					request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"success\",\"Your password has been changed successfully.\");})</script>");
	    					//request.setAttribute("msg","OK");
	    				}
	    				else
	    				{
	    					//request.setAttribute("msg","Failed");
	    					request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured. Please verify your old password.\");})</script>");
	    				}
    			    }
    			    else
    			    	request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"Please fill in all fields.\");})</script>");
    			
    			}
    			catch(Exception e){
    				request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured\");})</script>");
    			}
    			this.getServletContext().getRequestDispatcher(URL).forward(request, response);
			    return;
    			
    		}
    		
    		if(action.equals("login"))	
    		{
    			String usrLogin = request.getParameter("login");
			    String usrPassword = request.getParameter("password");
			    
			    System.out.println("[form] login = "+usrLogin);
			    
			    
			    if(!usrLogin.isEmpty() && !usrPassword.isEmpty() )
			    {
				    try {
				    	
				    	User user = new User();
				    	
						if(user.checkUser(usrLogin,usrPassword))
						{
							user.connexion(usrLogin);
							System.out.println("connexion");
							    
							session.setAttribute("username",usrLogin);
							
							String usrEmail = user.getEmail(usrLogin);
							String usrApiKey = user.getApiKey(usrLogin);
							
							session.setAttribute("usrEmail",usrEmail);
							session.setAttribute("usrApiKey",usrApiKey);
							
							if(user.getUserType(usrLogin) == 1)
								session.setAttribute("admin","1");
							
							
							//request.setAttribute("action","new");
							request.setAttribute("authentification","1");
							request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"success\",\"You're now logged in!\");})</script>");
							
						}
						else
						{
							session.setAttribute("username","");
							session.setAttribute("usrEmail","");
							session.setAttribute("usrApiKey","");
							
							request.setAttribute("authentification","Username or password are incorrect!.");
							request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"Username or password are incorrect!\");})</script>");
							//request.setAttribute("resultat","Echec authentification!");
						}
					} 
				    catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						session.setAttribute("username","");
						session.setAttribute("email","");
						session.setAttribute("api_key","");
						
						request.setAttribute("authentification","Authentification failed.");
						request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"Authentification failed.\");})</script>");
					}
			    }
			    else
			    	request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"Please fill in all fields.\");})</script>");
			    
			  
			    
			    this.getServletContext().getRequestDispatcher(URL).forward(request, response);
			    return;
    		}
		}
		else
		{
    		
			System.out.println("LaunchProcess");
	    	try {
		    	//Processing pr = new Processing();
				//pr.init();
				System.err.println("  -- username "+session.getAttribute("username").toString());
				
				pr.user(session.getAttribute("username").toString());
		    	//pr.setUser("lmoncla");
				System.out.println("  -- OutputDir : "+pr.outputDir()+pr.user());
				
				request.setAttribute("action", "new");
				
				String content = "";
				
				String name = StringTools.generate(10);
				String gpsUri = getServletContext().getRealPath("CorpusRando"); 
				
				System.out.println(" -- name : "+name);
			
		
				if (ServletFileUpload.isMultipartContent(request)) {
		
					System.out.println("** UPLOAD");
		
					FileItemFactory factory = new DiskFileItemFactory();
					ServletFileUpload upload = new ServletFileUpload(factory);
		
					upload.setHeaderEncoding(pr.characterEncoding());
					
					List<FileItem> fields = upload.parseRequest(request);
					System.out.println("Number of fields: " + fields.size());
					Iterator<FileItem> it = fields.iterator();
					if (!it.hasNext()) {
						System.out.println("No fields found");
						return;
					}
					
					
					System.out.println("  - Retrieving form values");
	
					while (it.hasNext()) {
	
						FileItem fileItem = it.next();
	
						boolean isFormField = fileItem.isFormField();
						if (isFormField) {
	
							if (fileItem.getFieldName().equals("content")) {
								System.out.println("** champs text");
								content = fileItem.getString(pr.characterEncoding());
								
								
								
							}
							
							
						/*	
							if (fileItem.getFieldName().equals("version")) {
								System.err.println("version = "+fileItem.getString());
								pr.setVersion(fileItem.getString());
								
								//System.err.println(pr.getLang()+"_nationalGazetteer :"+ fileItem.getString());
							}
							*/
							
							if (fileItem.getFieldName().equals("lang")) {
								System.err.println("lang = "+fileItem.getString());
								pr.lang(fileItem.getString());
								
								System.err.println(pr.lang()+"_nationalGazetteer :"+ fileItem.getString());
							}
							
	
							if (fileItem.getFieldName().equals(pr.lang()+"_analyseur")) {
								System.err.println("analyseur = "+fileItem.getString());
								pr.analyserPOS(fileItem.getString());
							}
							
							if (fileItem.getFieldName().equals(pr.lang()+"_geonames")) 
							{
								System.err.println(pr.lang()+"_geonames :"+ fileItem.getString());
								if (fileItem.getString().equals("on"))
									pr.geonames(true);
								else
									pr.geonames(false);
					
							}
							
							
							if (fileItem.getFieldName().equals(pr.lang()+"_nationalGazetteer")) {
								System.err.println(pr.lang()+"_nationalGazetteer :"+ fileItem.getString());
								if (fileItem.getString().equals("on"))
									pr.nationalGazetteer(true);
								else
									pr.nationalGazetteer(false);
								
								
							}
							
							
							if (fileItem.getFieldName().equals(pr.lang()+"_openstreetmap")) {
								System.err.println(pr.lang()+"_openstreetmap :"+ fileItem.getString());
								if (fileItem.getString().equals("on")) 
									pr.osm(true);
								else
									pr.osm(false);
								
							}
							
							
							if (fileItem.getFieldName().equals("disambiguationLevel")) {
								System.err.println("disambiguatiionLevel :"+ fileItem.getString());
								if (fileItem.getString().equals("0"))
								{
									pr.doToponymsDisambiguation(false);
									pr.doClustering(false);
									pr.doItineraryReconstruction(false);
								}
								if (fileItem.getString().equals("1")) 
								{
									pr.doToponymsDisambiguation(true);
									pr.doClustering(false);
									pr.doItineraryReconstruction(false);
								}
								
								if (fileItem.getString().equals("2")) 
								{
									pr.doToponymsDisambiguation(true);
									pr.doClustering(true);
									pr.doItineraryReconstruction(true);
								}
								
							}
							

						} 
						else 
						{
							System.out.println("** UPLOAD file");
							System.out.println("fileItem.getName():"+fileItem.getName());
							if(fileItem.getName() != "")
							{
								
								
								String format = StringTools.getExtension(fileItem.getName());
								
								System.out.println("fileItem.getName():"+fileItem.getName());
								
								if(format.equals(".gpx") || format.equals(".kml"))
								{
								
									String gpsContent = Streams.asString(fileItem.getInputStream(), pr.characterEncoding());
								
									
									System.err.println("context:"+gpsUri);
									
									
									FileTools.createFile(gpsUri+"/"+name+format, gpsContent);
									

								}
								else
								{
									System.err.println("Wrong format!");
								}
							}
					
						}
					}
					/*
					if(session.getAttribute("admin") == null)
					{
						System.err.println("Utilisateur");
						pr.setGeonames("distant");
						pr.setNationalGazetteer(true);
						pr.setOSM(true);
					}
					*/
				} 
				else 
				{
					System.out.println("** PAS D'UPLOAD");
		
					content = request.getParameter(CHAMP_CONTENT);
				
					pr.analyserPOS(request.getParameter("analyseur"));
				}
				
				
				
				if(content.isEmpty())
				{
					request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"Please fill in all fields.\");})</script>");
				  
				    this.getServletContext().getRequestDispatcher(URL).forward(request, response);
				    return;
				}
				
				request.setAttribute("content", content);
				
				System.err.println("output : "+pr.outputDir());
				System.err.println("user : "+pr.user());
				
				FileTools.createDir(pr.outputDir()+pr.user());
				
				pr.launchProcess(content, pr.outputDir()+pr.user()+"/"+name, name,"");
				
				String result = viewAnnotation(pr.outputDir()+pr.user()+"/"+name+"/",name);
    			if(result.isEmpty())
    			{
    				request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured.\");})</script>");
    				request.setAttribute("resultat", "");
    				request.setAttribute("resultat2", "");
    				request.setAttribute("map", "");
    				request.setAttribute("option", "");
    			}
    			else
    			{
    				
    				String r = "<div class='resultat' ><h3>Result ("+name+")</h3>";
    				
    				
    				r += viewDownloadButtons(name);
    				r += result;
    				
    				//r += viewDownloadButtons(name);
    				
    				r += "<div id='legende' data-role=\"collapsible\" data-collapsed-icon=\"carat-d\" data-expanded-icon=\"carat-u\" data-mini=\"true\">" +
    						
				"<h4>Caption</h4>" +
				"<div>Verb of <span class=\"verbeDeplacement\">displacement</span> | <span class=\"verbePerception\">perception</span>" +
				" | <span class=\"verbeTopographique\">topographic</span> | <span class=\"verbePosition\">position</span><br/>" +
				"Spatial named entity <span class=\"es\">candidate</span> | <span class=\"esC\">confirmed</span> <br/>" +
				"Toponym <span class=\"topoCandidat\">candidat</span> | <span class=\"nomToponymique\">confirmed</span><br/>" +
				"<span class=\"vpe\">VT structure</span><br/><span class=\"indirection\">Indirection</span><br/>" +
				"<span class=\"nomCommun\">Common Noun</span><br/><span class=\"nomPropre\">Proper Noun</span></div>" +
				"</div>";
	
    				
					request.setAttribute("resultat", r);
					
					request.setAttribute("resultat2", "<h3>Map</h3>");
						
					
					//int bestCluster = ToponymsResolution.bestCluster(pr.tableName());
					String tableName = pr.user()+"_"+name+pr.suffixeTablePgsql();
					
					int bestCluster = DensityClustering.selectBestCluster(pr.objPostgis(),tableName);
					
					request.setAttribute("map", viewMap(name,pr.outputDir()+pr.user()+"/"+name+"/"+name+".json",bestCluster,false,"map_canvas",false,false,pr.objPostgis(),tableName,pr.ignAPIkey(),gpsUri));
					
					if(session.getAttribute("admin") != null)
					{
						request.setAttribute("option", viewOption("admin"));
					}
					else
						request.setAttribute("option", viewOption("user"));
					
				
    			}
				
				System.out.println(" End process : "+name);
				
	    	}
	    	catch(Exception e){
	    		request.setAttribute("toast","<script language=javascript>$(document).ready(function() {toast(\"error\",\"An error occured.\");})</script>");
				//request.setAttribute("resultat", "<div class='resultat'><span class='erreur'>An error has occurred, try again later.</span></div>");
	    		request.setAttribute("resultat", "");
				request.setAttribute("resultat2", "");
				request.setAttribute("map", "");
				request.setAttribute("option", "");
				
				System.err.println("catch:"+e.toString());
				
				this.getServletContext().getRequestDispatcher(URL).forward(request, response);
				return;
			}
		
		}
		
    	this.getServletContext().getRequestDispatcher(URL).forward(request, response);
		
		
	}
	
	/**
	 * 
	 * @param name
	 * @return html
	 * @throws Exception
	 */
	protected static String viewDownloadButtons(String name) throws Exception
	{

		System.out.println(" Begin viewDownloadButtons ");
		String result = "";
		
		/**
		 * BOUTON DE DOWNLOAD
		 * 
		 * 
		 * <a href="#" class="ui-btn ui-btn-inline">Anchor</a>
			<button class="ui-btn ui-btn-inline">Button</button>
		 */
		
		
		result = "<br/><div id=\"link\">";
		//XML
		result += "<a data-ajax=\"false\" class=\"ui-btn ui-btn-inline ui-mini\" href=\"./files/"+name+".xml\">XML</a>";
		//JSON
		result += "<a data-ajax=\"false\" class=\"ui-btn ui-btn-inline ui-mini\" href=\"./files/"+name+".json\" >JSON</a>";
		//ZIP shapefile + csv
		//result += "<li><a data-ajax=\"false\" href=\"./fichier/"+name+".zip\" >SHAPE</a></li>";

		result += "</div><br/>";
		
		
		System.out.println(" End viewDownloadButtons ");
		return result;
	
	}

	/**
	 * 
	 * @param outputDir
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static String viewAnnotation(String outputDir, String name) throws Exception
	{

		System.out.println(" Begin viewAnnotation ");
		String result = "";


		result = readFinalOutputTei(outputDir,name);
		
		System.out.println(" End viewAnnotation ");
		
		return result;
	}
	
	
	/**
	 * 
	 * @param name
	 * @param uriJson
	 * @param bestCluster
	 * @param showAmbiguities
	 * @return
	 * @throws Exception
	 */
	protected static String viewMap(String name, String uriJson, int bestCluster, boolean showAmbiguities, String div, boolean doSpanningTree, boolean doBoundingBox, Postgis objPostgis, String table, String ignKey, String gpsUri) throws Exception
	{
		System.out.println(" Begin viewMap");
		String result = "";

		GoogleMaps gm = new GoogleMaps(46.6494362, 2.5048828, 5, "ROADMAP");
		gm.loadMarkers(uriJson); 	// en paramètre l'uri du fichier JSON à charger
		
		
		
		
		//gm.loadMarkers(vecTopo); 	// en paramètre le vecteur de toponymes
		
		
		
		//result = gm.getMapHtml(div,bestCluster, showAmbiguities,"i4m7o270k2lswyww0dyp8ma2");
		result = gm.getMapHtml(div,bestCluster, showAmbiguities,ignKey);
		
		
		//result += gm.getGPXHtml_test("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx><trk><name>Test</name><trkseg><trkpt 45.3805943\" lon=\"6.7336952\" /><trkpt lat=\"45.38951849357794\" lon=\"6.752727985937432\" /></trkseg><trkseg><trkpt lat=\"45.38951849357794\" lon=\"6.752727985937432\" /><trkpt lat=\"45.3898289\" lon=\"6.7538015\" /></trkseg><trkseg><trkpt lat=\"45.3898289\" lon=\"6.7538015\" /><trkpt lat=\"45.39188889037437\" lon=\"6.758482537324991\" /></trkseg><trkseg><trkpt lat=\"45.39188889037437\" lon=\"6.758482537324991\" /><trkpt lat=\"45.3952253\" lon=\"6.7676101\" /></trkseg><trkseg><trkpt lat=\"45.3952253\" lon=\"6.7676101\" /><trkpt lat=\"45.39763787659063\" lon=\"6.775926470740161\" /></trkseg><trkseg><trkpt lat=\"45.39188889037437\" lon=\"6.758482537324991\" /><trkpt lat=\"45.40540025677638\" lon=\"6.755405495860727\" /></trkseg><trkseg><trkpt lat=\"45.39763787659063\" lon=\"6.775926470740161\" /><trkpt lat=\"45.39607995\" lon=\"6.79070021876092\" /></trkseg><trkseg><trkpt lat=\"45.39607995\" lon=\"6.79070021876092\" /><trkpt lat=\"45.390187459561844\" lon=\"6.791927399327482\" /></trkseg><trkseg><trkpt lat=\"45.390187459561844\" lon=\"6.791927399327482\" /><trkpt lat=\"45.38812995\" lon=\"6.79950720679997\" /></trkseg><trkseg><trkpt lat=\"45.38812995\" lon=\"6.79950720679997\" /><trkpt lat=\"45.396946\" lon=\"6.8822055\" /></trkseg><trkseg><trkpt lat=\"45.4151845\" lon=\"6.9071388\" /><trkpt lat=\"45.396946\" lon=\"6.8822055\" /></trkseg></trk></gpx>");
		
		
		
	    //on test si le fichier gpx existe, si il existe on recupere le nom pour récupérer le fichier en ligne.
		if (FileTools.fileExist(gpsUri+"/"+name+".gpx")) 
		{
			System.err.println(" GPX :"+name);
			result += gm.getGPXHtml("./CorpusRando/"+name+".gpx");
		}
		if (FileTools.fileExist(gpsUri+"/"+name+".kml")) 
		{
			System.err.println(" KML :"+name);
			result += gm.getKMLHtml("http://erig.univ-pau.fr/PERDIDO/CorpusRando/"+name+".kml");
		}
		
		
		result += gm.showPath();
		
		//result += gm.showBuffer(objPostgis);

		
	
		if(doSpanningTree)
		{
			
			//Vector<Arc> arcs = SpanningTree.minimumSpannigTree(points);
			try
			{
			//	result += gm.getLineHtml(bestCluster);
			}
			catch(Exception e)
			{
				System.err.println(" error ");
			}
			
		}
		
		
		if(doBoundingBox)
		{
			try
			{
				
				result += gm.getPolygonHtml(objPostgis,table,bestCluster);
				result += gm.getCircumscribedCircle(objPostgis,table,bestCluster);
				//result += gm.getBoundingBox(objPostgis,table,bestCluster);
				
				
			}
			catch(Exception e)
			{
				System.err.println(" error ");
			}
			
			//result += gm.viewRadius();
			
		}
		
		
		
		//result += gm.declarElevationFunction();
		//result += gm.viewElevation();
		
		
		System.out.println(" End viewMap ");
		return result;
	}
	
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected static String viewMissingPoints(String uriJson, Postgis objPostgis, String table, int bestCluster) throws Exception
	{
		
		String result = "";
		
		Vector<Toponyme> missingPoints = loadMissingPoints(uriJson,bestCluster,objPostgis, table);
		
		result += "<script type=\"text/javascript\">";
		
			result += "var old_id = 0;";
			
			result += "var missingPoint = new Array();";
			    	    	
		result += "</script>";
		
		try{
			
		
		
		for(int i=0;i<missingPoints.size();i++)
		{
		
			
				//objPostgis.connect("outputDemo");
				objPostgis.connect(objPostgis.db_results());
			
				//select count(distinct id), clusterid from routine_1e_jour_de_champagny_le_haut_au_refuge_d where clusterid>-1 group by clusterid
				Statement state = objPostgis.conn.createStatement();
				ResultSet res = state.executeQuery("SELECT ST_AsText(ST_Centroid(ST_Envelope((SELECT ST_Collect(coord) FROM "+table+" WHERE clusterid="+bestCluster+"))))");
				res.next();
					
					
				String point = res.getString(1);
				
				
				
							ResultSet res2 = state.executeQuery("SELECT ST_AsText(ST_Envelope(ST_ConvexHull((SELECT ST_Collect(coord) FROM "+table+" WHERE clusterid="+bestCluster+"))))");
							res2.next();
								
								
							String poly = res2.getString(1);
								
			//				System.out.println("### poly : "+poly+" ###");
							
							poly = poly.replaceAll("POLYGON\\(\\(", "");
							poly = poly.replaceAll("\\)\\)", "");
							
							String coords[] = poly.split(",");
							String coord[] = coords[0].split(" ");
							
							

			//	System.out.println("### point : "+point+" ###");
				
				point = point.replaceAll("POINT\\(", "");
				point = point.replaceAll("\\)", "");
				
				String centroid[] = point.split(" ");
				
			/*	
				html += "var myLatlng = new google.maps.LatLng("+centroid[1]+","+centroid[0]+");";
				html += "var marker = new google.maps.Marker({"+
				      "position: myLatlng,"+
				      "map: map,"+
				      "title: 'Hello World!'"+
				 "});";
				 
				 */
				
				//System.out.println("### point1 : lat: "+centroid[1]+" lng: "+centroid[0]+"###");
				//System.out.println("### point1 : lat: "+coord[1]+" lng: "+coord[0]+"###");
				
				
				//Node p = new Node(new Toponyme(Double.parseDouble(centroid[0]),Double.parseDouble(centroid[1])));
				
				//Double dist = p.getDistanceInMeters(new Node(new Toponyme(Double.parseDouble(coord[0]),Double.parseDouble(coord[1]))));
				Double dist = MapsFunctions.getDistance(Double.parseDouble(centroid[0]), Double.parseDouble(centroid[1]), Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
				
				
			result += GoogleMaps.getCircleHTML(i,Double.parseDouble(centroid[1]),Double.parseDouble(centroid[0]),dist);
			
			
			res.close();			
			objPostgis.close();
			
			
		}
		
		}
		catch(Exception e)
		{
			System.err.println(" Not enough points : viewMissingPoints()!");
		}
		
	
	
		result += "<fieldset data-role=\"controlgroup\" data-mini=\"true\">";
		result += "<legend>Not located by gazetteers</legend>";
		for(int i=0;i<missingPoints.size();i++)
		{
			result += "<input type=\"radio\" name=\"radio-mini\" id=\"radio-mini-"+i+"\" value=\"choice-"+i+"\" onclick=\"showMissingPoint('"+i+"')\"/>";
			result += "<label for=\"radio-mini-"+i+"\">"+missingPoints.get(i).getName()+"</label>";
		}
    		
    	result += "</fieldset>";
    	
		
		return result;
	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected static String viewOption(String userType) throws Exception
	{
		String result = "";
		
		if(userType == "admin")
		{
		 result = "<fieldset data-role=\"controlgroup\" data-type=\"horizontal\" data-mini=\"true\">"+
				 "<label><input type=\"checkbox\" name=\"opt-gps\" id=\"checkbox-opt-gps\" data-mini=\"true\" onclick=\"changeOpt('gps')\"/>"+
				    "GPS</label>"+
				 "<label><input type=\"checkbox\" name=\"opt-path\" id=\"checkbox-opt-path\" data-mini=\"true\" onclick=\"changeOpt('path')\"/>"+
				 	"Path</label>"+
				 	"<label><input type=\"checkbox\" name=\"opt-buffer\" id=\"checkbox-opt-buffer\" data-mini=\"true\" onclick=\"changeOpt('buffer')\"/>"+
				 	"Buffer</label>"+
				 "<label><input type=\"checkbox\" name=\"opt-convexHull\" id=\"checkbox-opt-convexHull\" class=\"custom\" data-mini=\"true\" onclick=\"changeOpt('convexHull')\"/>"+
				 	"ConvexHull</label>"+
				 "<label><input type=\"checkbox\" name=\"opt-circle\" id=\"checkbox-opt-circle\" class=\"custom\" data-mini=\"true\" onclick=\"changeOpt('circle')\"/>"+
				 	"Circle</label>"+
				 //"<label><input type=\"checkbox\" name=\"opt-showAmbiguities\" id=\"checkbox-opt-showAmbiguities\" class=\"custom\" data-mini=\"true\" onclick=\"changeOpt('showAmbiguities')\"/>"+
				 //	"Show ambiguities</label>"+

		    "</fieldset>";
		}
		if(userType == "user")
		{
			result = "<fieldset data-role=\"controlgroup\" data-type=\"horizontal\">"+
					 //"<label><input type=\"checkbox\" name=\"opt-gps\" id=\"checkbox-opt-gps\" data-mini=\"true\" onclick=\"changeOpt('gps')\"/>"+
					 //   "Show GPS track</label>"+
					 "<label><input type=\"checkbox\" name=\"opt-path\" id=\"checkbox-opt-path\" data-mini=\"true\" onclick=\"changeOpt('path')\"/>"+
					 	"Path</label>"+
					// "<label><input type=\"checkbox\" name=\"opt-showAmbiguities\" id=\"checkbox-opt-showAmbiguities\" class=\"custom\" data-mini=\"true\" onclick=\"changeOpt('showAmbiguities')\"/>"+
					// 	"Show ambiguities</label>"+

			    "</fieldset>";
		}
		if(userType == "example")
		{
		
			result = "<fieldset data-role=\"controlgroup\" data-type=\"horizontal\">"+
			
			 	"<label><input type=\"checkbox\" name=\"opt-gps\" id=\"checkbox-opt-gps\" data-mini=\"true\" onclick=\"changeOpt('gps')\"/>"+
			    "Show GPS track</label>"+
			    "<label><input type=\"checkbox\" checked=\"checked\" name=\"opt-path\" id=\"checkbox-opt-path\" data-mini=\"true\" onclick=\"changeOpt('path')\"/>"+
			 	"Path</label>"+
			    "</fieldset>";
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param outputDir
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static String readFinalOutput(String outputDir, String name){
		System.out.println(" Begin ReadFinalOutput ");
		String resultString = "";
		try{
		SAXBuilder sxb = new SAXBuilder();
		
		System.out.println(" 1 ");
		resultString = "<div class='resultat' data-role=\"collapsible-set\" data-theme=\"c\" data-content-theme=\"d\" data-mini=\"true\">" +
				"<div data-role=\"collapsible\" data-collapsed=\"false\">" +
				"<h3>Result ("+name+")</h3>" +
				"<div>"; // html

		// On crée un nouveau document JDOM avec en argument le fichier XML
		// Le parsing est terminé ;)
		
		Document document = sxb.build(new File(outputDir +"/" + name + ".xml"));
		System.out.println(" 2 ");
		// On initialise un nouvel élément racine avec l'élément racine du
		// document.
		Element racine = document.getRootElement();
		
		List<?> listLss = racine.getChildren("sentence");

		// On crée un Iterator sur notre liste
		Iterator<?> i = listLss.iterator();
		System.out.println(" 3 ");
		while (i.hasNext()) 
		{
			// On recrée l'Element courant à chaque tour de boucle afin de
			// pouvoir utiliser les méthodes propres aux Element comme :
			// selectionner un noeud fils, modifier du texte, etc...
			Element courant = (Element) i.next();			
			List<?> listLss2 = courant.getChildren();
			
			resultString += xml2html(listLss2);
			
		}
		
		System.out.println(" 4 ");
		
		resultString += "</div></div>";
		resultString += "<div id='legende' data-role=\"collapsible-set\" data-theme=\"c\" data-content-theme=\"d\" data-mini=\"true\">" +
				"<div data-role=\"collapsible\" data-collapsed=\"true\">" +
				"<h3>Caption</h3>" +
				"<div>Verb of <span class=\"verbeDeplacement\">displacement</span> | <span class=\"verbePerception\">perception</span>" +
				" | <span class=\"verbeTopographique\">topographic</span> | <span class=\"verbePosition\">position</span><br/>" +
				"Spatial named entity <span class=\"es\">candidate</span> | <span class=\"esC\">confirmed</span> <br/>" +
				"Toponym <span class=\"topoCandidat\">candidat</span> | <span class=\"nomToponymique\">confirmed</span><br/>" +
				"<span class=\"vpe\">VT structure</span><br/><span class=\"indirection\">Indirection</span><br/>" +
				"<span class=\"nomCommun\">Common Noun</span><br/><span class=\"nomPropre\">Proper Noun</span></div>" +
				"</div></div>" +
				"</div>";
	

		System.out.println(" End ReadFinalOutput ");
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		return resultString;
	}

	/**
	 * 
	 * @param outputDir
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static String readFinalOutputTei(String outputDir, String name){
		System.out.println(" Begin ReadFinalOutputTei");
		String resultString = "";
		try{
			SAXBuilder sxb = new SAXBuilder();
			
			
			
			resultString +=	"<div>"; // html
	
			// On crée un nouveau document JDOM avec en argument le fichier XML
			// Le parsing est terminé ;)
			
			Document document = sxb.build(new File(outputDir +"/" + name + ".xml"));
			//System.out.println(" 2 ");
			// On initialise un nouvel élément racine avec l'élément racine du
			// document.
			Element racine = document.getRootElement();
			
			ElementFilter eFilter = new ElementFilter( "s", null ); 
			Iterator<Element> i = document.getRootElement().getDescendants(eFilter); //Gets the requested elements. 
			
			
			//List listLss = racine.getChildren("s");
			//Iterator i = listLss.iterator();
			
			
		//	System.out.println(" 3 ");
			while (i.hasNext()) 
			{
				//System.out.println(" next ");
				// On recrée l'Element courant à chaque tour de boucle afin de
				// pouvoir utiliser les méthodes propres aux Element comme :
				// selectionner un noeud fils, modifier du texte, etc...
				Element courant = (Element) i.next();			
				List<?> listLss2 = courant.getChildren();
				
				//resultString += xml2html(listLss2);
				resultString += tei2html(listLss2,false, true);
			}
			
			//System.out.println(" 4 ");
			
			resultString += "</div>";
			

			System.out.println(" End ReadFinalOutputTei ");
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			return "";
		}
		return resultString;
	}

	
	

	protected static String xml2html(Element elt)
	{
		
		String result = "";
		
		if(elt.getName().equals("token"))
		{
			result += "<span>"+elt.getText()+"</span> ";
		}
		else
		{
			if(elt.getName().equals("verb"))
			{
				
				String polarite = "";
				String type = elt.getAttribute("type").getValue();
				if(type.equals("perception"))
					result += "<span class=\"verbePerception\">";	
				
				if(type.equals("topographic"))
					result += "<span class=\"verbeTopographique\">";
				
				if(type.equals("location"))
					result += "<span class=\"verbePosition\">";
				
				if(type.equals("motion"))
				{
					result += "<span class=\"verbeDeplacement\">";
					polarite = elt.getAttribute("polarity").getValue();
				}	
					
				result += "<span class=\"infobulle\">"+XmlTools.getValueElt(elt)+"<span class=\"bulle\">V ";
				
				if(!polarite.isEmpty())
					result += "/ Polarity : "+polarite;
				result += "</span></span></span> ";
				
			
				
			}
			
			if(elt.getName().equals("VT") || elt.getName().equals("commonNoun") || elt.getName().equals("indirection") || elt.getName().equals("properNoun"))
			{
				
				result += "<span class=\""+elt.getName()+"\">";
				result += xml2html(elt.getChildren());
				result += "</span> ";
			}
			
			
			if(elt.getName().equals("SE"))
			{
				
				if(elt.getAttribute("valide") != null)
					if(elt.getAttribute("valide").equals("true"))
						result += "<span class=\"esC\">";
					else
						result += "<span class=\"es\">";
				else
					result += "<span class=\"es\">";
					
				result += xml2html(elt.getChildren());
				result += "</span> ";
			}
			if(elt.getName().equals("destination"))
			{
				
				result += "<span class=\"destination\">";
				result += xml2html(elt.getChildren());
				result += "</span> ";
			}
			if(elt.getName().equals("origin"))
			{
				
				result += "<span class=\"origin\">";
				result += xml2html(elt.getChildren());
				result += "</span> ";
			}
			
			if(elt.getName().equals("toponym"))
			{
				
				result += "<span class=\"topoCandidat\">";
				result += xml2html(elt.getChildren());
				result += "</span> ";
			}
			
			if(elt.getName().equals("roadname"))
			{
				result += "<span class=\"roadName\">";
				result += xml2html(elt.getChildren());
				result += "</span> ";
			}
			
			if(elt.getName().equals("subToponym"))
			{
				
				result += xml2html(elt.getChildren());
			}
			
			if(elt.getName().equals("candidat"))
			{
				
				//result += "<span class=\"topoCandidat\">";
				result += xml2html(elt.getChildren());
				//result += "</span> ";
			}
			
			if(elt.getName().equals("confirme"))
			{
				
				result += "<span class=\"nomToponymique\">";
				String value = elt.getChildText("value");
				String ressources = elt.getChildText("ressources");
				
				result += "<span class=\"infobulle\">"+value+"<span class=\"bulle\">"+value+" :<br />"+ressources+"</span></span>";
				result += "</span>";
			}
			
	//		<span class=\"nomToponymique\"></span> 

			
		}
		return result;
	}
	
	protected static String xml2html(List<?> Listelt)
	{
		
		String result = "";
		// On crée un Iterator sur notre liste
		Iterator<?> i = Listelt.iterator();
		while (i.hasNext()) 
		{
			Element courant = (Element) i.next();
			result += xml2html(courant);
		}
		return result;
	}
	
	/**
	 * 
	 * @param elt
	 * @param isGeo
	 * @param withW
	 * @return
	 */
	protected static String tei2html(Element elt, boolean isGeo, boolean withW)
	{
		
		String result = "";
		//System.out.println("** Debut tei2html balise: "+elt.getName());
		
		if(elt.getName().equals("w"))
		{
			//System.out.println("w : "+elt.getText());
			
			String pos = elt.getAttributeValue("type");
			String lemma = elt.getAttributeValue("lemma");
			if(pos.equals("V"))
			{
				String type = "";
				String polarity = "";
				String subtype = "";
				try{
					subtype = elt.getAttributeValue("subtype");
					if(subtype.equals("motion_initial"))
					{
						type = "motion";
						polarity = "initial";
					}
					if(subtype.equals("motion_median"))
					{
						type = "motion";
						polarity = "median";
					}
					if(subtype.equals("motion_final"))
					{
						type = "motion";
						polarity = "final";
					}
					
					if(subtype.equals("perception"))
						type = "perception";
					if(subtype.equals("topographic"))
						type = "topographic";
				}
				catch(Exception e)
				{
					
				}
				
				if(type != "")
				{
					if(type.equals("perception"))
						result += "<span class=\"verbePerception\">";	
					
					if(type.equals("topographic"))
						result += "<span class=\"verbeTopographique\">";
					
					if(type.equals("location"))
						result += "<span class=\"verbePosition\">";
					
					if(type.equals("motion"))
					{
						result += "<span class=\"verbeDeplacement\">";
						//polarite = elt.getAttribute("polarity").getValue();
					}	
					
					if(withW)
						result += "<span class=\"infobulle\">"+elt.getText()+"<span class=\"bulle\">POS : "+pos+"<br>Lemma : "+lemma+"<br>Polarity : "+polarity+"</span></span>";
					else
						result += elt.getText();
					result += "</span> ";
				}
				else
				{
					if(withW)
						result += "<span class=\"infobulle\">"+elt.getText()+"<span class=\"bulle\">POS : "+pos+"<br>Lemma : "+lemma+"<br>Polarity : "+polarity+"</span></span> ";
					else
						result += elt.getText()+" ";
				}
			}
			else
			{
				if(withW)
					result += "<span class=\"infobulle\">"+elt.getText()+"<span class=\"bulle\">POS : "+pos+"<br>Lemma : "+lemma+"</span></span> ";
				else
					result += ""+elt.getText()+" ";
			}
		}
		else
		{
			
			
			if(elt.getName().equals("phr") )
			{
				
				result += "<span class=\"phr\">";
				result += tei2html(elt.getChildren(), false, withW);
				result += "</span>";
			}
			
			if(elt.getName().equals("placeName") )
			{
				Namespace namespace = Namespace.getNamespace("http://www.tei-c.org/ns/1.0");
				
				String name = XmlTools.getValueEltTei(elt);
				String degree = null;
				Element e = elt.getChild("certainty",namespace);//.getAttributeValue("degree");
				
				if(e != null)
				{
					degree = e.getAttributeValue("degree");
				}
				
				if(degree != null)
				{
					//affiche le certainty dans l'infobulle
					result += "<span class=\"name_geo\">";
					if(withW)
						result += "<span class=\"infobulle\">";
					result += tei2html(elt.getChildren(),true,false);
					if(withW)
						result += "<span class=\"bulle\">"+name+"<br>Class : spatial<br>Certainty : "+degree+" </span></span>";
					result += "</span>";
				}
				else
				{
					//affiche le certainty dans l'infobulle
					result += "<span class=\"placeName\">";
					result += tei2html(elt.getChildren(),true,withW);
					result += "</span>";
				}
			}
			
			if(elt.getName().equals("rs") )
			{
				
				String degree = null;
				String name = XmlTools.getValueEltTei(elt);
				try{
					Namespace namespace = Namespace.getNamespace("http://www.tei-c.org/ns/1.0");
					Element e = elt.getChild("certainty",namespace);//.getAttributeValue("degree");
					degree = e.getAttributeValue("degree");
				}
				catch(Exception e){}
				
				if(degree!= null)
				{
					//affiche le certainty dans l'infobulle
					result += "<span class=\"rs\">";
					if(withW)
						result += "<span class=\"infobulle\">";
					result += tei2html(elt.getChildren(),false,false);
					if(withW)
						result += "<span class=\"bulle\">"+name+"<br>Class : non-spatial<br>Certainty : "+degree+" </span></span>";
					result += "</span>";
				}
				else
				{
					result += "<span class=\"rs\">";
					result += tei2html(elt.getChildren(),false,withW);
					result += "</span>";
				}
				
			}
			
			if(elt.getName().equals("term") )
			{
				
				if(elt.getAttributeValue("type").equals("N"))
				{
					//affiche le certainty dans l'infobulle
					result += "<span class=\"commonNoun\">";
					result += tei2html(elt.getChildren(),false,withW);
					result += "</span>";
				}
				else
				{
					if(elt.getAttributeValue("type").equals("offset"))
					{
						result += "<span class=\"offset\">";
						result += tei2html(elt.getChildren(),false,withW);
						result += "</span> ";
					}
					else
						result += tei2html(elt.getChildren(),false,withW);
				}
			}
			
			if(elt.getName().equals("offset") )
			{
				result += "<span class=\"offset\">";
				result += tei2html(elt.getChildren(),false,withW);
				result += "</span> ";
				
			}
			
			if(elt.getName().equals("geogFeat"))
			{
				
				result += "<span class=\"geogFeat\">";
				result += tei2html(elt.getChildren(),false,withW);
				result += "</span>";
			}
			
			if(elt.getName().equals("geogName"))
			{
				String degree = null;
				String name = XmlTools.getValueEltTei(elt);
				try{
					Namespace namespace = Namespace.getNamespace("http://www.tei-c.org/ns/1.0");
					Element e = elt.getChild("certainty",namespace);//.getAttributeValue("degree");
					degree = e.getAttributeValue("degree");
				}
				catch(Exception e){}
				
				if(degree!= null)
				{
					//System.err.println("true");
					//affiche le certainty dans l'infobulle
					result += "<span class=\"name_geo\">";
					if(withW)
						result += "<span class=\"infobulle\">";
					result += tei2html(elt.getChildren(),false,false);
					if(withW)
						result += "<span class=\"bulle\">"+name+"<br>Class : spatial<br>Certainty : "+degree+" </span></span>";
					result += "</span>";
				}
				else
				{
					//System.err.println("false");
					result += "<span class=\"geogName\">";
					result += tei2html(elt.getChildren(),true,withW);
					result += "</span>";
				}
				
				/*
				result += "<span class=\"geogName\">";
				result += tei2html(elt.getChildren(),true,false);
				result += "</span>";
				*/
			}
			
			if(elt.getName().equals("name"))
			{
				if(isGeo)
					result += "<span class=\"name_geo\">";
				else
					result += "<span class=\"name\">";
				
				result += tei2html(elt.getChildren(),false,false);
				result += "</span>";
			}
			
		}
		
		//System.out.println("** Fin tei2html");
		return result;
	}
	
	/**
	 * 
	 * @param Listelt
	 * @param isGeo
	 * @param withW
	 * @return
	 */
	protected static String tei2html(List<?> Listelt, boolean isGeo, boolean withW)
	{
		
		String result = "";
		// On crée un Iterator sur notre liste
		Iterator<?> i = Listelt.iterator();
		while (i.hasNext()) 
		{
			Element courant = (Element) i.next();
			result += tei2html(courant,isGeo,withW);
		}
		return result;
	}
	
	
	/**
	 * loadMarkers loads missing toponyms from a JSON file
	 * @param toponyms			Json file uri
	 */
	private static Vector<Toponyme> loadMissingPoints(String uriJson, int bestCluster, Postgis objPostgis, String table) throws Exception
	{
		System.out.println(" Begin loadMissingPoints");
	
		Vector<Toponyme> missingPoints = new Vector<Toponyme>();
		/*
		JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(uriJson)));
		
		reader.beginArray();
	       while (reader.hasNext()) {
	    	   String title = "" ,description = "", src="", polarite ="", verbe="", findName= "",country="",continent="",feature="";
	    	   double lat=0,lng = 0;
	    	   int cluster = 0, nb=1,localise = -1,id=0;
	    	   reader.beginObject();
	    	   
	    	   while (reader.hasNext()) {
		    	 
	    		 String name = reader.nextName();
				 if (name.equals("title")) {
				   title = reader.nextString();
				 } else if (name.equals("id")) {
				    id = reader.nextInt();
				 } else if (name.equals("lat")) {
				        lat = reader.nextDouble();
				 } else if (name.equals("lng")) {
				    lng = reader.nextDouble();
				 } else if (name.equals("description")) {
				       description = reader.nextString();
				 } else if (name.equals("polarite")) {
				       polarite = reader.nextString(); 
				 } else if (name.equals("verbe")) {
					 	verbe = reader.nextString();
				 } else if (name.equals("src")) {
				       src = reader.nextString();
				 } else if (name.equals("nb")) {
				       nb = reader.nextInt();
				 } else if (name.equals("findName")) {
				       findName = reader.nextString();
				 } else if (name.equals("country")) {
				       country = reader.nextString();
				 } else if (name.equals("continent")) {
				       continent = reader.nextString();
				 } else if (name.equals("feature")) {
				       feature = reader.nextString();
				 } else if (name.equals("localise")) {
				       localise = reader.nextInt();
				 } else if (name.equals("cluster")) {
				       cluster = reader.nextInt();
				 } else{
				     reader.skipValue();
				 }
		         
	    	   }
	    	   reader.endObject();
		     
		     if(id == -1)
		     {
		    	 Verb verb = new Verb(verbe,"",polarite);
		    	 //cptMarker++;
			     //System.out.println("id : "+id+" point : "+title+" lat : "+lat+" lng : "+lng+" src : "+src);
		    	 System.out.println(" Name : "+findName);
		    	 missingPoints.add(new Toponyme(id,findName,title,verb,lat,lng,src,nb,country,continent,feature,localise,cluster,false));
		     }
	     }
	     reader.endArray();
	     */
	     
	     /**
	      * ajouter les missing points que ont des résultats retournés pas les gazetteers mais qui ne sont pas dans le meilleur cluster
	      */
	     
	     try {
				//objPostgis.connect("outputDemo");
				objPostgis.connect(objPostgis.db_results());
			
				//select count(distinct id), clusterid from routine_1e_jour_de_champagny_le_haut_au_refuge_d where clusterid>-1 group by clusterid
				Statement state = objPostgis.conn.createStatement();
				ResultSet res = state.executeQuery("SELECT DISTINCT(lower(name)) FROM "+table+" WHERE id NOT IN (SELECT id FROM "+table+" WHERE clusterid="+bestCluster+")");
				
				while(res.next())
				{
					
					System.out.println(" Name : "+res.getString(1));
					
					Statement state2 = objPostgis.conn.createStatement();
					ResultSet res2 = state2.executeQuery("SELECT count(DISTINCT(id)) FROM "+table+" WHERE lower(name)='"+res.getString(1)+"'");
					
					res2.next();
					
					int count = res2.getInt(1);
					
					res2 = state2.executeQuery("SELECT gid,id,name,verb,polarity,country,continent,feature,featureText,localise FROM "+table+" WHERE lower(name)='"+res.getString(1)+"'");
					
					res2.next();
					//while(res2.next())
					{
					
						System.out.println(" Name : "+res2.getString(3)+" count : "+count);
					
						Verb verb = new Verb(res2.getString(4), "", res2.getString(5), "");
						String displayName = res2.getString(3)+" (x"+count+")";
						
						Toponyme t = new Toponyme(res2.getInt(2),-1,displayName,displayName,verb,0.0,0.0,"",0,res2.getString(6),res2.getString(7),res2.getString(8),res2.getString(9),res2.getInt(10),bestCluster,false);
						
						missingPoints.add(t);
					}
				}
					
					
				
	     }
	     catch(Exception e)
	     {
	    	 
	     }
	     
	     
	     
	     System.out.println(" End loadMissingPoints");
	     
	     return missingPoints;
	}
	
	

	
}
