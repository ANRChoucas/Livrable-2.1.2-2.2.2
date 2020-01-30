package choucas.perdido.translator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class Geonames2Obj {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String chaine=""; // format-> [nom],x,y;[nom],x,y;
		String srcGeonames ="/Users/lmoncla/Programme/Unitex3.1beta/Ressources/FR.txt";
		
		//lecture du fichier texte	
		try{
			InputStream ips=new FileInputStream(srcGeonames); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			
			
			
			String ligne;
			while ((ligne=br.readLine())!=null){
				//System.out.println("ligne : "+ligne);
				
				java.util.Vector v = new java.util.Vector();
				
				String str[]= ligne.split("\t");
				
				//System.out.println("valeur : "+str[1]);
				
				
				//chaine += "["+str[1]+"],"+str[4]+","+str[5]+";"; 
				
				v.add("["+str[1]+"],"+str[1]+","+str[10]+","+str[4]+","+str[5]+";");
				
				v.add("["+str[2]+"],"+str[1]+","+str[10]+","+str[4]+","+str[5]+";");
				
			//	if(!str[1].equals(str[2]))
			//		chaine += "["+str[2]+"],"+str[4]+","+str[5]+";";
				
				String c[] = str[3].split(",");
				
				if(c.length>1)
				{
					for(int i=0;i<c.length;i++)
					{
						//chaine += "["+c[i]+"],"+str[4]+","+str[5]+";";
						v.add("["+c[i]+"],"+str[1]+","+str[10]+","+str[4]+","+str[5]+";");
						//System.out.println("c : "+c[i]);
					}
				}
				
				//v sans doublons
				v = new java.util.Vector(new java.util.HashSet(v));
				
				for(int i=0; i<v.size(); i++)
		         {
		         	chaine += v.get(i);
		         }
				
				//chaine += ","+str[4]+","+str[5]+";";
				System.out.println(str[1]+" : "+str[4]+";"+str[5]);

			}
			br.close(); 
			
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		
		try {
			
			FileOutputStream fichier = new FileOutputStream("/Users/lmoncla/Programme/Unitex3.1beta/Ressources/geonames2_fr.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(chaine);
			oos.flush();
			oos.close();
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		
		
			
				
			
			System.out.println("Le fichier geonames2_fr.ser a été créé!"); 
		
		
		
		
	}

}
