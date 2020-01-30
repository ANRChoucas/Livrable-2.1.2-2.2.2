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
public class DBSCAN {

	private String _dbname = "outputDemo";
	private String _dbuser = "postgres";
	private String _dbpass = "postgres";
	private String _dbhost = "10.0.13.153";
	private String _dbport = "5432";
    
	private float _epsilon;
	private int _minNumOfPointsInCluster;
	
		
		public DBSCAN(float epsilon, int minNumOfPointsInCluster)
		{
			//float epsilon=0.1f;				//Epsilon;
			//int minNumOfPointsInCluster=2;	//MinPts
			
			_epsilon = epsilon;
			_minNumOfPointsInCluster = minNumOfPointsInCluster;
           
		}
		
		public void setParamDB(String dbname, String dbuser, String dbpass, String dbhost, String dbport)
		{
			_dbname = dbname;
			_dbuser = dbuser;
			_dbpass = dbpass;
			_dbhost = dbhost;
			_dbport = dbport;
		}
		
		public void execute(String table)
		{
			//float epsilon=0.1f;				//Epsilon;
			//int minNumOfPointsInCluster=2;	//MinPts
            long beginTime = System.currentTimeMillis();
            
            
            try {
            	System.out.println("Connecting Postgis... ");
                java.sql.Connection conn; 
				Class.forName("org.postgresql.Driver").newInstance();
		        String url = "jdbc:postgresql://" + _dbhost + ":" + _dbport + "/" + _dbname;
		        conn = DriverManager.getConnection(url, _dbuser, _dbpass);
	            
	            System.out.println("Clutering... ");
				Dataset dataset = InputReader.readFromDB2(table,conn);
	            System.out.println("--Dataset Readed, size: "+dataset.size()+" elements");
	            
	            FastDBSCAN dbscan = new FastDBSCAN();
	            dbscan.setEpsilon(_epsilon);
	            dbscan.setMinPoints(_minNumOfPointsInCluster);
	            dbscan.doClustering(dataset);
	           
	            System.out.println("--Dataset clusterized ");
	            InputReader.writeDatasetToDB(dataset, table, conn);
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