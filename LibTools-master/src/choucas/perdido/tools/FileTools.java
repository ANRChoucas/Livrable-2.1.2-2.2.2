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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.stream.JsonReader;


/**
 * FileTools class, provides some methods to create or modify files
 * @author Ludovic Moncla
 * @version 1.0
 */
public class FileTools  {

	
	public static File[] listFiles(String dir) {
		
		File di = new File(dir);
		File fl[] = di.listFiles();

		Arrays.sort(fl);

		return fl;
	}
	
	public static void createDir(String name)  throws Exception{
		
		try {

			new File(name).mkdir();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void createFile(String outputFile, String content)  throws Exception{
		//String txtFile = output + "." + ext;
		try {
			
			PrintWriter out = new PrintWriter(new FileWriter(outputFile));

			out.println(content);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	/**
	 * updateStat : add some content in a file (used for statistics)
	 * @param name 			name of the file
	 * @param content		content to add in the file
	 */
	public static void updateStat(String output, String content) throws Exception {
		
		PrintWriter out = new PrintWriter(new FileWriter(output,true));
		out.println(content);
		out.close();
	}
	
	
public static void updateFileTxt(String name, String content) {
		
		try {

			
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name,true), "UTF-8"));		
			out.append(content);
			

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	/*
	 * V2.1 
	 * 	- permet de mettre à jour le fichier json des coord des ES
	 * 	- créer un fichier txt contenant toute les ES et le nombre de fois où elles ont été trouvée
	 */
	public static void updateFile(String name, String content) {
		
		try {

			
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name,true), "UTF-8"));		
			out.append(content).append("\r\n");
			

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	
	public void deleteFiles(String name) {
		
		File txtFile = new File(name);
		txtFile.delete();
	}
	
	
	public static String getContent(String filePath) throws Exception
	{
		String content = "";
		try {	
		
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			String ligne = "";
			while ((ligne = br.readLine()) != null) {
				content += ligne+"\r\n";
			}
			
			br.close();
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
		return content;
	}
	
	

	public static void delete(File file)
	    	throws IOException{
	 
	    	if(file.isDirectory()){
	 
	    		//directory is empty, then delete it
	    		if(file.list().length==0){
	 
	    		   file.delete();
	    		  // System.out.println("Directory is deleted : " + file.getAbsolutePath());
	 
	    		}else{
	 
	    		   //list all the directory contents
	        	   String files[] = file.list();
	 
	        	   for (String temp : files) {
	        	      //construct the file structure
	        	      File fileDelete = new File(file, temp);
	 
	        	      //recursive delete
	        	     delete(fileDelete);
	        	   }
	 
	        	   //check the directory again, if empty then delete it
	        	   if(file.list().length==0){
	           	     file.delete();
	        	  //   System.out.println("Directory is deleted : " 
	                 //                                 + file.getAbsolutePath());
	        	   }
	    		}
	 
	    	}else{
	    		//if file, then delete it
	    		file.delete();
	    	//	System.out.println("File is deleted : " + file.getAbsolutePath());
	    	}
	    }
	
	

	public static File zipFile(String uri, String[] sources, String destination) throws FileNotFoundException, IOException {
		File destinationFile = new File(uri+destination);

		// On ouvre les flux entrants/sortants
		FileOutputStream fos = new FileOutputStream(destinationFile);
		ZipOutputStream zos = new ZipOutputStream(fos);

		for (String source : sources) {
			
			FileInputStream fis = new FileInputStream(uri+source);

			// On ajoute un nouveau fichier au zip
			zos.putNextEntry(new ZipEntry(source));

			// On ecrit le contenu du fichier ajoute
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = fis.read(buf)) > 0) {
				zos.write(buf, 0, len);
			}

			// On termine l'ajout du nouveau fichier
			zos.closeEntry();
			fis.close();
		}

		zos.close();

		return destinationFile;
	}
	
	
	private static String getTagger(String pathFilename, String[] taggers)
	{
		String tagger = "unknown";
	
		for(int i=0;i<taggers.length;i++)
		{
			File f = new File(pathFilename + "_"+taggers[i]+".txt");
			
			if (f.exists()) {
				return taggers[i];
			}
		}	
		
		return tagger;
	}
	
	
	public static String listeFile(String output, String[] listTaggers) {
		File di = new File(output);
		File fl[] = di.listFiles();
		int j;

		Arrays.sort(fl, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f2.lastModified()).compareTo(
						f1.lastModified());
			}
		});
		
/*	EXEMPLE LISTE JQUERY
		<ul data-role="listview" data-split-icon="gear" data-split-theme="d">
		<li><a href="index.html">
			<img src="images/album-bb.jpg" />
			<h3>Broken Bells</h3>
			<p>Broken Bells</p>
			</a><a href="#purchase" data-rel="popup" data-position-to="window" data-transition="pop">Purchase album</a>
		</li>
	*/	
		
	//	<input type=\"hidden\" name=\"act\" value=\"consult\"/>
		

		String totalinfo = "<ul data-role=\"listview\" rel=\"external\">";
		String s = "";
		for (j = 0; j < fl.length; j++) {

			String filename = fl[j].getName().toString();
			
			
			
			//on test l'extension pour connaitre l'analyseur utilisé
			if(!filename.endsWith(".csv"))
			{
				/*
				if(filename.endsWith("_treetagger.txt"))
					s = "TreeTagger";
				if(filename.endsWith("_melt.txt"))
					s = "melt";
				if(filename.endsWith("_talismane.txt"))
					s = "talismane";
				if(filename.endsWith("_freeling.txt"))
					s = "Freeling";
				 */
				
				
				s = getTagger(output + "/" + filename + "/" + filename, listTaggers);
				

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
				Date d = new Date(fl[j].lastModified());
	
				// totalinfo += filename+"............"+d+"<br/>";
				totalinfo += "<li onclick=\"valider()\"><a href=\"./demonstration?act=view&view="+filename+"\" data-ajax=\"false\" >";
				
				if(filename.length() == 6)
					totalinfo += "<h3>[API] - "+filename+"</h3>";
				else
					totalinfo += "<h3>"+filename+"</h3>";
				
				totalinfo += "<p>" + d + " [" + s + "]</p></a></li>";
				
				/*
				totalinfo += "<div data-role=\"popup\" id=\"options\" data-theme=\"d\" data-overlay-theme=\"b\" class=\"ui-content\" style=\"max-width:340px;\">";
				totalinfo += "<h3>Options</h3>";
				totalinfo += "<p>Choississez une option.</p>";
				totalinfo += "<a href=\"./demonstration?consult="+filename+"\" data-role=\"button\" data-theme=\"b\" data-icon=\"check\" data-inline=\"true\" data-mini=\"true\"  ajax=\"false\">Voir</a>";
				totalinfo += "<a href=\"#\" data-role=\"button\" data-inline=\"true\" data-mini=\"true\">Supprimer</a>";	
				totalinfo += "</div>";
				*/
				
			}
		}

		totalinfo += "</ul>";
		
		
		
		/*
		
		String totalinfo = "<div class=\"ui-block-c\" style=\"width:10%\" ></div><div class=\"ui-block-d\" style=\"width:60%\" data-theme=\"c\"><input type=\"hidden\" name=\"act\" value=\"consult\"/><select data-theme=\"c\" name=\"nom\" id=\"select-choice-c\" data-native-menu=\"false\" data-mini=\"true\"><option data-theme=\"c\">Sélectionnez un texte déjà traité</option>";
		String s = "";
		for (j = 0; j < fl.length; j++) {

			String filename = fl[j].getName().toString();

			File f = new File(output + "/" + filename + "/" + filename
					+ "_treetagger.txt");
			if (f.exists()) {
				s = "TreeTagger";
			} else {
				s = "MElt";
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
			Date d = new Date(fl[j].lastModified());

			// totalinfo += filename+"............"+d+"<br/>";
			totalinfo += "<OPTION value=\"" + filename + "\">" + filename
					+ "...." + d + "...." + s + "</OPTION>";
		}

		totalinfo += "</SELECT></div><div class=\"ui-block-e\" style=\"width:25%\"><button data-mini=\"true\" type=\"submit\" name=\"submit\" value=\"submit-value\">Consulter</button></div>";
		*/
		return totalinfo;
	}
	
	
	
	public static boolean fileExist(String fileName){
		File f = new File (fileName);
		if (f.exists()){
		     return true;
		}else{
		     
			return false;
		}
	}
	
	
	
	
	public static void splitFileTxt(String inputDir, String filename)
	{
		System.out.println(" Begin splitFileTxt");
		int cpt = 0;
		String name = "";
		
		String content = "";
		try {	
		
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputDir+"/"+filename+".txt")));

			createDir(inputDir+"/"+filename+"/");
			
			
			while ((content = br.readLine()) != null) {
				
				if(!content.equals(""))
				{
					cpt++;
					if(cpt<10)
						name = filename+"_000"+cpt+".txt";
					else
						if(cpt<100)
							name = filename+"_00"+cpt+".txt";
						else
							if(cpt<1000)
								name = filename+"_0"+cpt+".txt";
							else
								name = filename+"_"+cpt+".txt";
					
					FileTools.createFile(inputDir+"/"+filename+"/"+name, content);
				}
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(" End splitFileTxt");
	}
	
	
	
}
