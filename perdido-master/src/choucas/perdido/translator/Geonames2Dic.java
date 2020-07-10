package choucas.perdido.translator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Geonames2Dic {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String chaine="";
		String srcGeonames ="/home/lmoncla/FR.txt";
		String dicGeonames ="/home/lmoncla/Geonames.dic";
		
		//lecture du fichier texte	
		try{
			InputStream ips=new FileInputStream(srcGeonames); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			
			FileWriter fw = new FileWriter (dicGeonames);
			BufferedWriter bw = new BufferedWriter (fw);
			PrintWriter fichierSortie = new PrintWriter (bw); 
			
		
			String ligne;
			while ((ligne=br.readLine())!=null){
				//System.out.println("ligne : "+ligne);
				
				
				String str[]= ligne.split("\t");
				
				//System.out.println("valeur : "+str[1]);
				
				
				fichierSortie.println (str[1]+",.N+PR+Toponyme+Geonames"); 
				
				//chaine+=ligne+"\n";
			}
			br.close(); 
			fichierSortie.close();
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		
		
		
		
			
				
			
			System.out.println("Le fichier " + dicGeonames + " a été créé!"); 
		
		
		
		
	}

}
