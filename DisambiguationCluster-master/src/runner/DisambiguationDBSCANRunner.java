package runner;

import java.sql.DriverManager;

import input.Dataset;
import input.InputReader;
import algorithms.FastDBSCAN;

/**
 * Author:  Markus and Adapted to posgresSQL+postGIS by 
 * 			Walter Renteria Agualimpia IA3-Unizar
 *
 */
public class DisambiguationDBSCANRunner {

	static String dbname = "outputDemo";
    static String dbuser = "postgres";
    static String dbpass = "postgres";
    static String dbhost = "10.0.13.153";
    static String dbport = "5432";    
    
		public static void main(String[] args) {
			
			
			try {
				java.sql.Connection conn; 
				Class.forName("org.postgresql.Driver").newInstance();
		        String url = "jdbc:postgresql://" + dbhost + ":" + dbport + "/" + dbname;
		        conn = DriverManager.getConnection(url, dbuser, dbpass);
			
		    	//String table="routine_1e_jour_de_champagny_le_haut_au_refuge_d";
		    	//String table="routine_2e_jour_du_refuge_de_la_leisse_au_refuge";
		    	String table="routine_1e_jour_de_pralognan_au_refuge_de_la_lei";
				float epsilon=0.1f;				//Epsilon;
				int minNumOfPointsInCluster=2;	//MinPts
                long beginTime = System.currentTimeMillis();
                System.out.println("Clutering... ");
				Dataset dataset = InputReader.readFromDB2(table,conn);
                System.out.println("--Dataset Readed, size: "+dataset.size()+" elements");
                
                FastDBSCAN dbscan = new FastDBSCAN();
                dbscan.setEpsilon(epsilon);
                dbscan.setMinPoints(minNumOfPointsInCluster);
                dbscan.doClustering(dataset);
               
                System.out.println("--Dataset clusterized ");
                InputReader.writeDatasetToDB(dataset, table,conn);
                System.out.println("--Dataset updated, clusters saved, cluster_id taggs");
                System.out.println("END");
                
                long time = System.currentTimeMillis() - beginTime;
                System.out.println("\nThe Total Algorithm took " + time/1000 + " seconds to complete.");
                
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
				
}