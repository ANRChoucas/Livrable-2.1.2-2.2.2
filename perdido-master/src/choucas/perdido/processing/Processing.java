/**
 * Processing class : combine method of annotation and toponyms resolution to reconstruct an itinerary
 * @author 	Ludovic Moncla
 * 			CHOUCAS (University of Pau)
 */
package choucas.perdido.processing;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import choucas.perdido.postgresql.Postgis;
import choucas.perdido.tools.FileTools;
import choucas.perdido.tools.StringTools;
import choucas.perdido.tools.XmlTools;




public class Processing {

	
	
	
	
	

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
			
		System.out.println("## Begin Main");
		
		try {
			
			Perdido perdido = new Perdido();
		
			perdido.loadParams();
			
			String name = StringTools.generate(6);
			
			
			String content = "Je vais à Pau.";
			
			
			String outputDir = perdido.outputDir() + perdido.user() + "/" +name;
			
		
			perdido.launchProcess(content, outputDir, name,"");
			
			
				
			//routine(perdido,"multicriteria_1","");
			//routine(perdido,"multicriteria_2","");
			//routine(perdido,"multicriteria_3","");
			
			
			//routine(perdido,"multicriteria_4","");
			
			//routine(perdido,"multicriteria_5","");
			
			//routine(perdido,"multicriteria_6","");
			
			
			//routine(perdido,"multicriteria_7","");
			
			//routine(perdido,"multicriteria_7bis","");
			
			//routine(perdido,"multicriteria_8","");
			
			//routine(perdido,"multicriteria_9","");
			
			//routine(perdido,"multicriteria_10","CONTENU");
			
			
			
			
			//routine(perdido,"","description");
			
			
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("## End Main");
	}


	
	
	
	

	/**
	 * launch a routine process 
	 * @throws Exception
	 */
	public static void routine(Perdido perdido, String multicriteria, String XmlElement) throws Exception
	{
		System.out.println("## Begin routine");
		
		long beginTime = System.currentTimeMillis();
		
		

		//updateStat("stats","Name;Time;Number of words;Number of toponyms;Number of topo in VT found;Number of topo in VT in text;Errors","","csv");
		//parcours tous les fichiers du dossier input
		File fl[] = FileTools.listFiles(perdido.inputDir() + perdido.user());

		for (int j = 0; j < fl.length; j++) {
			
			String fileName = "";
			String extension = "";
			
			try {
				fileName = StringTools.getNameWithoutExtention(fl[j].getName().toString());
				extension = StringTools.getExtension(fl[j].getName().toString());
				
			}
			catch(Exception e)
			{
				continue;
			}
			
			System.out.println("## fileName : "+fileName+" ext : "+extension);
			
			String content = "";
			
			//on ne parcours pas les fichiers issus de l'analyse POS 
			if(!fileName.endsWith("treetagger") && !fileName.endsWith("melt") && !fileName.endsWith("talismane") && !fileName.endsWith("freeling"))
			{
				if(extension.equals(".xml") || extension.equals(".txt"))
				{
					try {
						if(perdido.doAnnotation())
						{
						
							File f = new File (perdido.outputDir() + perdido.user() + "/" + fileName + "/");
							if (f.exists())
							{
								//si on ne fait pas l'annotation le dossier existe deja dans le dossier output
							
					            	if(perdido.doDelete())
					            	{
					            		FileTools.delete(f);
					            		
				            			Postgis pstgis = new Postgis(perdido.urlPstg(),perdido.portPstg(),perdido.userPstg(),perdido.passwordPstg(),perdido.db_users(),perdido.db_results());
									System.out.println("### Suppression de la BD Postgis");
									//com.lmoncla.postgresql.Json2Postgis js = new com.lmoncla.postgresql.Json2Postgis();
									//pstgis.connect("outputDemo");
									pstgis.connect(pstgis.db_results());
									
									try {
										Statement state = pstgis.conn.createStatement();
										String table = perdido.user()+"_"+fileName+"_clust";
									
										ResultSet res = state.executeQuery("DROP TABLE "+table);
										res.close();
									} catch (Exception e) { }	
									
									pstgis.close();
							            
						        }
					            	else
					            	{
					            		System.out.println("     !! Files already exist !!");
									continue;
					            	}
							}
							
							if(extension.equals(".xml"))
							{
								content = XmlTools.getContent(perdido.inputDir()+perdido.user()+"/"+fileName+extension,XmlElement);
								
								System.out.println("file : "+perdido.inputDir()+perdido.user()+"/"+fileName+extension);
								
								System.out.println("content : "+content);
							}
							if(extension.equals(".txt"))
							{
								content = FileTools.getContent(perdido.inputDir()+perdido.user()+"/"+fileName+extension);
								System.out.println("content : "+content);
							}
							
							
							
							
						}
					}
					catch(Exception e)
					{
						continue;
					}
					
					perdido.launchProcess(content, perdido.outputDir()+perdido.user()+"/"+fileName, fileName, multicriteria);
				}
			}
		}
		
		System.out.println("## End routine");
	}
	
	

	
	
	

	
	
	

}
