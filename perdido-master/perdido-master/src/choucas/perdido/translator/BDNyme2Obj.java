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

public class BDNyme2Obj {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String chaineBdnyme="";
		String chaineGeoroute="";
		String chaineBdtopo="";
		
		
		for(int i=0;i<5;i++)
		{
			String srcBDNyme = "";
			int origin=0, nom=0;
			switch(i)
			{
			case 0 :
				srcBDNyme ="/home/lmoncla/lieu_dit_habite.csv";
				origin = 1;
				nom = 2;
				System.out.println("###### /home/lmoncla/lieu_dit_habite.csv"); 
				break;
			case 1:
				srcBDNyme ="/home/lmoncla/chef_lieu.csv";
				origin = 2;
				nom = 4;
				System.out.println("###### /home/lmoncla/chef_lieu.csv"); 
				break;
			case 2:
				srcBDNyme ="/home/lmoncla/toponyme_divers.csv";
				origin = 1;
				nom = 2;
				System.out.println("###### /home/lmoncla/toponyme_divers.csv"); 
				break;
			case 3:
				srcBDNyme ="/home/lmoncla/hydronyme.csv";
				origin = 1;
				nom = 2;
				System.out.println("###### /home/lmoncla/hydronyme.csv"); 
				break;
			case 4:
				srcBDNyme ="/home/lmoncla/lieu_dit_non_habite.csv";
				origin = 1;
				nom = 2;
				System.out.println("###### /home/lmoncla/lieu_dit_non_habite.cs"); 
				break;
			case 5:
				srcBDNyme ="/home/lmoncla/oronyme.csv";
				origin = 1;
				nom = 2;
				System.out.println("###### /home/lmoncla/oronyme.csv"); 
				break;
			case 6:
				srcBDNyme ="/home/lmoncla/toponyme_communication.csv";
				origin = 1;
				nom = 2;
				System.out.println("###### /home/lmoncla/toponyme_communication.csv"); 
				break;
			case 7:
				srcBDNyme ="/home/lmoncla/toponyme_ferre.csv";
				origin = 1;
				nom = 2;
				System.out.println("###### /home/lmoncla/toponyme_ferre.csv"); 
				break;
			default :
				break;
			}
			
			
			
			//lecture du fichier texte	
			try{
				InputStream ips=new FileInputStream(srcBDNyme); 
				InputStreamReader ipsr=new InputStreamReader(ips);
				BufferedReader br=new BufferedReader(ipsr);
				
				//Set tab=new HashSet(); // on crée notre Set
				
				String ligne;
				while ((ligne=br.readLine())!=null){
					//System.out.println("ligne : "+ligne);
					
					
					String str[]= ligne.split(",");
					
					System.out.println("valeur : "+str[nom]);
					//tester si on est en bdnyme ou en georoute
					
					if(str[origin].equals("BDNyme"))
						chaineBdnyme += "["+str[nom]+"] ";
					else if(str[origin].equals("Géoroute"))
						chaineGeoroute += "["+str[nom]+"] ";
					else if(str[origin].equals("BDTopo"))
						chaineBdtopo += "["+str[nom]+"] ";
					
	
				}
				br.close(); 
				
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}
		}
		
		try {
			
			FileOutputStream fichier = new FileOutputStream("/home/lmoncla/bdnyme.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(chaineBdnyme);
			oos.flush();
			oos.close();
			System.out.println("Le fichier bdnyme.ser a été créé!"); 
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		try {
			
			FileOutputStream f = new FileOutputStream("/home/lmoncla/georoute.ser");
			ObjectOutputStream oos2 = new ObjectOutputStream(f);
			oos2.writeObject(chaineGeoroute);
			oos2.flush();
			oos2.close();
			System.out.println("Le fichier geroute.ser a été créé!");  
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		try {
			FileOutputStream f2 = new FileOutputStream("/home/lmoncla/bdtopo.ser");
			ObjectOutputStream oos3 = new ObjectOutputStream(f2);
			oos3.writeObject(chaineGeoroute);
			oos3.flush();
			oos3.close();
			System.out.println("Le fichier bdtopo.ser a été créé!"); 
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
