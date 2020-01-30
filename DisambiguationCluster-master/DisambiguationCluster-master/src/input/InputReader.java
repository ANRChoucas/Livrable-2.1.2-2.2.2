package input;

import java.sql.DriverManager;
import java.sql.Statement;
import input.Dataset;
import input.FeatureVector;
import java.sql.ResultSet;

/**
 * Class to read and write Table to store all results of the clustering algorithm 
 * Author:  Markus and Adapted to posgresSQL+postGIS by 
 * 			Walter Renteria Agualimpia IA3-Unizar
 */

public class InputReader {        	
	/*
	static String dbname = "outputDemo";
    static String dbuser = "postgres";
    static String dbpass = "postgres";
    static String dbhost = "10.0.13.153";
    static String dbport = "5432";
	*/
    public static Dataset readFromDB2(String table,java.sql.Connection conn){
		try{        

				boolean isResult=false;
		        Dataset dataset = new Dataset();
		        FeatureVector featureVector;
		        String[] splitLine=new String[3];
				/*
		        java.sql.Connection conn; 
	    		Class.forName("org.postgresql.Driver").newInstance();
		        String url = "jdbc:postgresql://" + dbhost + ":" + dbport + "/" + dbname;
		        conn = DriverManager.getConnection(url, dbuser, dbpass);
				 */
		        ((org.postgresql.PGConnection)conn).addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
				((org.postgresql.PGConnection)conn).addDataType("box3d",Class.forName("org.postgis.PGbox3d"));	        
				
				Statement s = conn.createStatement(); 
				String query="SELECT gid, x, y FROM ";
				query=query.concat(table);
				//query=query.concat(" ORDER BY gid ASC");
				
				ResultSet r = s.executeQuery(query);
				int id;
				double x, y;
				
				while( r.next() ) { 
					id = r.getInt(1); 
	    			x= r.getDouble(2);
	    			y= r.getDouble(3);
					splitLine[0]=String.valueOf(id);
					splitLine[1]="1:"+String.valueOf(x);
					splitLine[2]="2:"+String.valueOf(y);
					featureVector = new FeatureVector(splitLine,isResult);
					dataset.add(featureVector);	
				}
				return dataset;				
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("error");
		}
		return null;
    }
    //--------------------------------------                
    public static void writeDatasetToDB(Dataset dataset, String table,java.sql.Connection conn){
       // java.sql.Connection conn;
    	try { 
    		/*Class.forName("org.postgresql.Driver").newInstance();
	        String url = "jdbc:postgresql://" + dbhost + ":" + dbport + "/" + dbname;
	        conn = DriverManager.getConnection(url, dbuser, dbpass);
	        */
	        ((org.postgresql.PGConnection)conn).addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
			((org.postgresql.PGConnection)conn).addDataType("box3d",Class.forName("org.postgis.PGbox3d"));	        
    					
			Statement s = conn.createStatement();
			String query;
			
			query=" ALTER TABLE ";
			query=query.concat(table);
			query=query.concat(" DROP COLUMN clusterID;");
			
			query=query.concat(" ALTER TABLE ");
			query=query.concat(table);
			query=query.concat(" ADD COLUMN clusterID integer");
				
			s.executeUpdate(query);
			//s.execute("END");				
			//s.close(); 
			//conn.close(); 
			
			//--- Etiquetar cada record "servicio" con el n�mero del cluster al que pertenece
            int clusterID;
            int elmentID;
            for (FeatureVector featureVector : dataset) {
                    elmentID=featureVector.getLabel();
                    clusterID=featureVector.getCalculatedClusternumber();
                    
    				query="UPDATE "; 
    				query=query.concat(table);
    				query=query.concat(" SET clusterID=");
    				query=query.concat(String.valueOf(clusterID));
    				query=query.concat(" WHERE (gid='");
    				query=query.concat(String.valueOf(elmentID));
    				query=query.concat("' )");

    				s.executeUpdate(query);
			}	
			s.close(); 
			conn.close(); 
    	} 
		catch( Exception e ) { 
			System.out.println(e.getMessage());
			e.printStackTrace(); 
		}             
    }
    //--------------------------------------                        
}