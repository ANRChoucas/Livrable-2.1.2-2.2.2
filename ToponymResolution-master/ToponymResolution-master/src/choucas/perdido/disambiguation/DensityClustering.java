/*
 * Copyright (C) 2016 Ludovic Moncla <ludovic.moncla@univ-pau.fr>
 * 
 * This file is part of ToponymResolution - Perdido project <http://erig.univ-pau.fr/PERDIDO/>
 *
 * ToponymResolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ToponymResolution is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ToponymResolution. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package choucas.perdido.disambiguation;

import java.sql.ResultSet;
import java.sql.Statement;

import choucas.perdido.postgresql.Postgis;
import runner.DBSCAN;



/**
 * DensityClustering class : provides methods for clustering points using the spatial density
 * @author Ludovic Moncla
 */
public class DensityClustering {
	
	
	private static String _tablename = "";
	private static Postgis _objPostgis = null;
	
	
	/**
	 * 
	 * @param objPostgis
	 * @param tablename 		name of the table of the postgis database
	 */
	public DensityClustering(Postgis objPostgis, String tablename) 
	{
		_tablename = tablename;
		_objPostgis = objPostgis;
	}
	
	
	
	
	
	/**
	 * runClustering : create clusters of toponyms using a density based clustering method (dbscan) applied on a postgis database
	 * @throws Exception
	 */
	public void runClustering() throws Exception{
		DBSCAN dbscan = new DBSCAN(0.1f, 2);
		dbscan.setParamDB(_objPostgis.db_results(), _objPostgis.user(), _objPostgis.password(), _objPostgis.url(), _objPostgis.port());
		dbscan.execute(_tablename);
	}
	
	
	
	
	/**
	 * selectBestCluster : choose the best cluster
	 * @param objPostgis
	 * @param tablename		name of the table of the postgis database
	 * @return int : number of the best cluster
	 * @throws Exception
	 */
	public static int selectBestCluster(Postgis objPostgis, String tablename) throws Exception{
		int max = 0;
		int bestCluster = -1;
		//faire une requete sur le clusterid et sur un distinct sur id pour savoir combien de toponym unique sont dans le cluster
		
		//_objPostgis.connect("outputDemo");
		objPostgis.connect(objPostgis.db_results());
		//select count(distinct id), clusterid from routine_1e_jour_de_champagny_le_haut_au_refuge_d where clusterid>-1 group by clusterid
		Statement state = objPostgis.conn.createStatement();
		ResultSet res = state.executeQuery("SELECT COUNT(DISTINCT id), clusterid FROM "+tablename+" WHERE clusterid>-1 GROUP BY clusterid");
		while(res.next())
		{ 		
			if(max < res.getInt(1))
			{
				max = res.getInt(1);
				bestCluster = res.getInt("clusterid");
			}
		}
		res.close();
		
		objPostgis.close();
		return bestCluster;
     }
	
	
	
	
	
	
	
	/*
	public int testClustering(ToponymsResolution tr, String name, Vector<Toponyme> toponyms ) throws Exception
	{
		System.out.println("**********************************");
		System.out.println("**     Begin test clustering    **");
	
		String tableNameTmp = user()+"_"+name+"_tmp";
		
		Vector<Toponyme> toponymsTmp = new Vector<Toponyme>(toponyms);
		
		Vector<Toponyme> toponymsInBestCluster= new Vector<Toponyme>();
		
		for(int i=0;i<toponyms.size();i++){
			if(toponyms.get(i).isBest()){
				toponymsInBestCluster.add(toponyms.get(i));
			}
		}
		
		int initialSize = toponymsInBestCluster.size();
		
		
		System.out.println("**     toponymsInBestCluster.size() : "+initialSize);
		
		String listTopoTmp = "";
		String listTopo = "";
		
		int bestClusterTmp = -1;
		int finalSize = 0;
		if(initialSize > 2){
			do{
				
				System.out.println("** do ");
	
				int lower = 0;
				int higher = toponymsInBestCluster.size();
				
				//generate randomly the id of the toponym to delete
				int idRandom = (int)(Math.random() * (higher-lower)) + lower;
			
				
	
				System.out.println("**     idRandom : "+idRandom);
	
				int gidRandom = toponymsInBestCluster.get(idRandom).getGid();
				
				System.out.println("**     gidRandom : "+gidRandom);
				
				//Vector<Toponyme> toponymsTmp = new Vector<Toponyme>(toponymsInBestCluster);
				//toponymsTmp = toponymsInBestCluster;
	
			
				
				for(int i=0;i<toponymsInBestCluster.size();i++)
				{
					if(toponymsInBestCluster.get(i).getGid() == gidRandom)
						toponymsInBestCluster.remove(i);
				}
				
				for(int i=0;i<toponymsTmp.size();i++)
				{
					if(toponymsTmp.get(i).getGid() == gidRandom)
						toponymsTmp.remove(i);
				}		
	
				
				
				tr.vector2Bdd(tableNameTmp,toponymsTmp);
				
				tr.clustering(tableNameTmp);
			
				bestClusterTmp = tr.bestCluster(tableNameTmp);
				
				tr.updateVecTopo(tableNameTmp,bestClusterTmp, toponymsTmp);
				
				listTopo = "";
				finalSize = 0;
				for(int i=0;i<toponymsInBestCluster.size();i++)
				{
					listTopo += toponymsInBestCluster.get(i).getGid()+" ";
					finalSize++;
				}
			
				System.out.println("**  toponyms in bestCluster: "+listTopo);
	
	
				listTopoTmp = "";
				
				for(int i=0;i<toponymsTmp.size();i++)
				{
					if(toponymsTmp.get(i).isBest())
					{
						listTopoTmp += toponymsTmp.get(i).getGid()+" ";
					}
				}
				
				System.out.println("**  toponyms in bestCluster temp: "+listTopoTmp);	
	
				System.out.println("** final size: "+finalSize);
	
				
				 * delete the temp table
				 						
				try {
					
					perdido.postgresql.Postgis pstgis = new perdido.postgresql.Postgis(urlPstg(),portPstg(),userPstg(),passwordPstg(),db_users(),db_results());
					//pstgis.connect("outputDemo");
					pstgis.connect(pstgis._db_results);
					
					
					Statement state = pstgis.conn.createStatement();
					
					ResultSet res = state.executeQuery("DROP TABLE "+tableNameTmp);
					res.close();
					
					pstgis.close();
				} 
				catch (Exception e) { }	
				
				
			}while(listTopoTmp.equals(listTopo) && finalSize > 3 && bestClusterTmp != -1);
		}
		
	
	
	
		
		System.out.println("** final size++: "+finalSize);
			
		System.out.println("**     End test clustering    **");
		System.out.println("**********************************");
		
		return finalSize;
	}*/
			
	
}
