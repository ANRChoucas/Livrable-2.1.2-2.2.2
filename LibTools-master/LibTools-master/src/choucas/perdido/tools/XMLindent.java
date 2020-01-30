package choucas.perdido.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


public class XMLindent {


		 public static void main(String args[]) throws Exception {
			 
			 String folder = "/Users/lmoncla/Desktop/testXML/";
			 
			 File di = new File(folder);
			 String fl[] = di.list();

			 Arrays.sort(fl);

			 
			  // File fl[] = FileTools.listFiles(folder);
			  

			 for (int j = 0; j < fl.length; j++) {
					
				 indentXMLFile(fl[j]);
			 }
			 
			 
			 
		 

		 }
		 
		 
		 
		 public static int indentXMLFile(String file)
		 {
			 String fileName = "";
			 String extension = "";
			 File fl = new File(file);
				
			 try {
					fileName = StringTools.getNameWithoutExtention(fl.getName().toString());
					extension = StringTools.getExtension(fl.getName().toString());
					
				}
				catch(Exception e)
				{
					return 1;
				}
				
				//System.out.println("## fileName : "+fileName+" ext : "+extension);
				
				//String filePath = folder+fileName+extension;
				//String content = "";
				
				if(extension.equals(".xml"))
				{
					try 
					{
						
						String content = "";
						
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

						String ligne = "";
						while ((ligne = br.readLine()) != null) 
						{
							 
							content += ligne;
						//	System.out.println("ligne : "+ligne);
						}
						
						
						content = content.replaceAll("<TEI xmlns='http://www.tei-c.org/ns/1.0'>", "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">" +
								"<teiHeader><fileDesc><titleStmt><title><!-- title of the resource --></title></titleStmt><publicationStmt><p><!-- Information about distribution of the resource --></p></publicationStmt><sourceDesc><p><!-- Information about source from which the resource derives --></p></sourceDesc></fileDesc></teiHeader>" +
								"<text><body><p>");
						
						content = content.replaceAll("</TEI>", "</p></body></text></TEI>");
						content = content.replaceAll("xml:id=\"", "xml:id=\"ene.");
						
						
						content = content.replaceAll("[\r\n]+", "");
						
						
						content = content.replaceAll("( ){2,}", " ");
					
						// System.err.println("avant : "+content);
						 
						 content = content.replaceAll("> <", "><");
						
						
						//System.err.println("après : "+content);
						
						
						br.close();
						
						
						 ByteArrayOutputStream s = new ByteArrayOutputStream();

						 //(1)
						  TransformerFactory tf = TransformerFactory.newInstance();
						  tf.setAttribute("indent-number", new Integer(2));

						  
						  //(2)
						  Transformer t = tf.newTransformer();
						  t.setOutputProperty(OutputKeys.INDENT, "yes");

						  
						  //(3)
						  t.transform(new StreamSource(new StringReader(content)), new StreamResult(new OutputStreamWriter(s, "utf-8")));
						  
						
						  //System.out.println(new String(s.toByteArray()));
						
						
						  PrintWriter out = new PrintWriter(new FileWriter(file));

							out.println(new String(s.toByteArray()));
							out.close();
						  

					}
					catch(Exception e)
					{
						return 2;
					}
				}
				return 0;
		 }
		 
		 


}
