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

package choucas.perdido.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import choucas.perdido.elements.Toponyme;



/**
 * ConnexionPostgis class, provide some methods to connect to postgis database
 * @author Ludovic Moncla
 * @version 1.0
 */
public class Postgis {
	
	public static Connection conn = null;
	
	private String _url = "";
	private String _port = "";
	private String _user = "";
	private String _password = "";
	
	private String _db_users = "";
	private String _db_results = "";

	/**
	 * Constructor class
	 * @param url
	 * @param port
	 * @param user
	 * @param password
	 */
	public Postgis(String url, String port, String user, String password, String db_users, String db_results)
	{
		_url = url; //localhost
		_port = port; //5432
		_user = user; //postgres
		_password = password; //postgres
		_db_users = db_users;
		_db_results = db_results;
	}

	
	public void createDB(String db) throws SQLException
	{
		Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:"+_port+"/", _user, _password);
		Statement statement = c.createStatement();
		statement.executeUpdate("CREATE DATABASE "+db);
		
		
	}
	
	
	/**
	 * Open the connexion
	 * @param bdd			Name of the bdd
	 * @throws ClassNotFoundException 
	 * @throws Exception
	 */
	public void connect(String bdd)  throws SQLException, ClassNotFoundException {
		//System.out.println("Open connexion Postgis...");
	
			Class.forName("org.postgresql.Driver");
			//System.out.println("DRIVER OK ! ");

			String url = "jdbc:postgresql://"+_url+":"+_port+"/"+bdd;//Geonames";
			String user = _user;
			String passwd = _password;

			conn = DriverManager.getConnection(url, user, passwd);
			//System.out.println("Connection effective !");
	}
	
	/**
	 * Close the connexion
	 * @throws Exception
	 */
	public void close() throws Exception {
		//System.out.println("Close connexion Postgis...");
		
		conn.close();
			
	}
	
	
	
	/**
	 * 
	 * @param table
	 * @param vecTopo
	 */
	public void storeToponyms(String table, Vector<Toponyme> vecTopo) throws Exception{
		
		System.out.println("-- Begin storeToponyms");
	
		String req = "";
		// Création d'un objet Statement
		Statement state = conn.createStatement();
		
		try{
			ResultSet res = state.executeQuery("DROP TABLE "+table);
			res.close();
		}
		catch(Exception e)
		{
			
		}
		
		/**
		 * En version 1.5 de Postgis on ne peut pas typer la géométrie GEOMETRY(Point,4326)
		 */
		
		req = "CREATE TABLE "+table+"(gid integer, id integer, name text, findName text, resource text, country text, continent text, feature text, featureText text, verb text, polarity text, x numeric, y numeric, clusterid integer, localise numeric, coord GEOMETRY)";
		
		//création de la nouvelle table
		state.executeUpdate(req);
		
		
	
		for(int i=0;i<vecTopo.size();i++)
		{
			Toponyme toponyme = vecTopo.get(i);
			String verb = "";
			String polarity = "";
			if(toponyme.getVerb() != null)
		    {
				//verb = toponyme.getVerb().getName();
				verb = toponyme.getVerb().getValue();
				polarity = toponyme.getVerb().getPolarity();
		    }
			
			  PreparedStatement pstmt = conn.prepareStatement("INSERT INTO "+table+" values(?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_SetSRID(ST_MakePoint("
					  	+ toponyme.getLat() + "," + toponyme.getLng() + "), 4326))");
	     	pstmt.setInt(1, toponyme.getGid());
			pstmt.setInt(2, toponyme.getId());
			pstmt.setString(3, toponyme.getName());
			pstmt.setString(4, toponyme.getValue());
			pstmt.setString(5, toponyme.getSources());
			pstmt.setString(6, toponyme.getCountry());
			pstmt.setString(7, toponyme.getContinent());
			pstmt.setString(8, toponyme.getFeature());
			pstmt.setString(9, toponyme.getFeatureText());
			pstmt.setString(10, verb);
			pstmt.setString(11, polarity);
			pstmt.setDouble(12, toponyme.getLat());
			pstmt.setDouble(13, toponyme.getLng());
			pstmt.setInt(14, toponyme.getCluster());
			pstmt.setInt(15, toponyme.getLocalise());
			
			pstmt.executeUpdate();
		}
		
		
		 state.close();
		

		 System.out.println("-- End storeToponyms");
	
	}

	
	
	public String url()
	{
		return _url;
	}
	
	
	public String user()
	{
		return _user;
	}
	
	
	public String port()
	{
		return _port;
	}
	
	public String password()
	{
		return _password;
	}
	
	public String db_users()
	{
		return _db_users;
	}
	
	public String db_results()
	{
		return _db_results;
	}
	
	
}
