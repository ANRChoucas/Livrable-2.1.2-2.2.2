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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import liuppa.perdido.tools.StringTools;



/**
 * User class, provide some methods to create or modify users
 * @author Ludovic Moncla
 * @version 1.0
 */
public class User {
	

	private static Postgis _objPostgis = null;
	private String _outputDir = "";
	
	public static void main(String[] args) {
		
		try {
			
			Class<?> currentClass = new Object() { }.getClass().getEnclosingClass();
			
			InputStream is = currentClass.getResourceAsStream("/perdido/resources/config.properties");
			Properties props = new Properties();
			props.load(is);
			
			_objPostgis = new Postgis(props.getProperty("urlPstg"),props.getProperty("portPstg"),props.getProperty("userPstg"),props.getProperty("passwordPstg"),props.getProperty("db_users"),props.getProperty("db_results"));
			
			//addColumnUser("api_key");
			
			 String api_key = StringTools.generate(10);
			
			
		//	String api_key = addUser("corpusGT","corpusGT","corpusGT",1,"corpusGT","corpusGT","corpusGT",-1);
			System.out.println("User added successfuly, api_key="+api_key);
		
			
		} catch (Exception e) {
			
			e.printStackTrace();
			//System.out.println("Create user failed");
		}
	}
	
	
	
	public User()
	{
		try {
			Class<?> currentClass = new Object() { }.getClass().getEnclosingClass();
			
			InputStream is = currentClass.getResourceAsStream("/perdido/resources/config.properties");
			Properties props = new Properties();
			
			props.load(is);
			
			_outputDir = props.getProperty("outputDir");

			_objPostgis = new Postgis(props.getProperty("urlPstg"),props.getProperty("portPstg"),props.getProperty("userPstg"),props.getProperty("passwordPstg"),props.getProperty("db_users"),props.getProperty("db_results"));
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public User(Postgis objPostgis)
	{
		_objPostgis = objPostgis;
	}
	
	
	
	
	public boolean checkUserAPI_key(String api_key) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT COUNT(*) FROM users where api_key='"+ api_key + "'");

		res.next();
		int r = res.getInt(1);
				
		
		_objPostgis.close();
		if(r != 0)
			return true;
	
		else
			return false;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return true or false
	 * @throws Exception 
	 */
	public boolean checkUser(String username, String password) throws Exception
	{
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT COUNT(*) FROM users where username='"+ username + "' AND password=md5('"+password+"') AND active=1");

		res.next();
		int r = res.getInt(1);
				
		
		_objPostgis.close();
		if(r != 0)
			return true;
	
		else
			return false;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public boolean checkUser(String username) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT COUNT(*) FROM users where username='"+ username + "'");

		res.next();
		int r = res.getInt(1);
				
		
		_objPostgis.close();
		if(r != 0)
			return true;
	
		else
			return false;
		
	}
	
	public boolean newConnexion2(String username) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT maxconnexion, lastConnexion, nbConnexion FROM users where username='"+ username + "'");

		res.next();
		int limit = res.getInt(1);
		Date lastConnexion = res.getDate(2);
		int nbConnexion = res.getInt(3);
		
		java.util.Date now = new java.util.Date();
        Date today = new Date(now.getTime());
		
        if(lastConnexion.compareTo(today)==0)
        {
        	if(nbConnexion < limit)
        	{
        		nbConnexion++;
        	}
        	else
        	{
        		return false;
        	}
        }
        else
        {
        	nbConnexion = 1;
        }
		
        PreparedStatement pstmt = _objPostgis.conn.prepareStatement("UPDATE users SET nbConnexion = ?, lastConnexion = ? WHERE username = ?  ");
		pstmt.setInt(1, nbConnexion);
		pstmt.setDate(2,today);
		pstmt.setString(2,username);
		
		
		pstmt.execute();

		_objPostgis.close();
    	
    
		return true;	
	}
	
	public boolean connexion(String username) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		java.util.Date now = new java.util.Date();
        Date today = new Date(now.getTime());
		
		PreparedStatement pstmt = _objPostgis.conn.prepareStatement("UPDATE users SET lastconnexion = ? WHERE username = ? ");
		
		pstmt.setDate(1,today);
		pstmt.setString(2,username);
		
		
		pstmt.execute();
		_objPostgis.close();
    	
 
		return true;
	}
	
	public boolean newConnexion(String api_key) throws Exception
	{
	
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT maxconnexion, lastconnexion, nbconnexion FROM users WHERE api_key='"+ api_key + "'");

		res.next();
		int limit = res.getInt(1);
		Date lastConnexion = res.getDate(2);
		int nbConnexion = res.getInt(3);
		
		java.util.Date now = new java.util.Date();
        Date today = new Date(now.getTime());
		
        System.err.println("today : "+today.toString());
        System.err.println("lastConnexion : "+lastConnexion.toString());
        
        if(lastConnexion.toString().equals(today.toString()))
        {
        	System.out.println("equal");
        	System.out.println("nbconnexion : "+nbConnexion);
        	System.out.println("limit : "+limit);
        	if(nbConnexion < limit || limit == -1)
        	{
        		System.out.println("newConnexion");
        		nbConnexion++;
        	}
        	else
        	{
        		return false;
        	}
        }
        else
        {
        	System.out.println("else");
        	nbConnexion = 1;
        }
		
        PreparedStatement pstmt = _objPostgis.conn.prepareStatement("UPDATE users SET nbconnexion = ?, lastconnexion = ? WHERE api_key = ?  ");
		pstmt.setInt(1, nbConnexion);
		pstmt.setDate(2,today);
		pstmt.setString(3,api_key);
		
	
		pstmt.execute();

		_objPostgis.close();
    	
		
		return true;
	
	}
	
	
	
	public String getUserName(String api_key) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT username FROM users where api_key='"+ api_key+"'" );

		res.next();
		String r = res.getString(1);
				
		
		_objPostgis.close();
		
		return r;
	}
	
	
	public void activeUser(String username, int active) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		PreparedStatement pstmt = _objPostgis.conn.prepareStatement("UPDATE users SET active = ? WHERE username = ?  ");
		pstmt.setInt(1, active);
		pstmt.setString(2,username);
		
		
		pstmt.execute();

		_objPostgis.close();
		 	
	}
	
	public void setAdmin(String username, int type) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		
		PreparedStatement pstmt = _objPostgis.conn.prepareStatement("UPDATE users SET type = ? WHERE username = ?  ");
		pstmt.setInt(1, type);
		pstmt.setString(2,username);
		
		
		pstmt.execute();

		_objPostgis.close();
		
	}
	
	public void setLimit(String username, int limit) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		
		PreparedStatement pstmt = _objPostgis.conn.prepareStatement("UPDATE users SET maxconnexion = ? WHERE username = ?  ");
		pstmt.setInt(1, limit);
		pstmt.setString(2,username);
		
		
		pstmt.execute();

		_objPostgis.close();
			
	}
	
	
	public String getEmail(String username) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT email FROM users where username='"+ username+"'" );

		res.next();
		String r = res.getString(1);
				
		
		_objPostgis.close();
		
		return r;
	}
	
	public String getApiKey(String username) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT api_key FROM users where username='"+ username+"'" );

		res.next();
		String r = res.getString(1);
				
		
		_objPostgis.close();
		
		return r;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return true or false
	 * @throws Exception 
	 */
	public int getUserType(String username) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT type FROM users where username='"+ username+"'" );

		res.next();
		int r = res.getInt(1);
				
		
		_objPostgis.close();
		
		return r;
	}
	
	public int getUserStatus(String username) throws Exception
	{
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();

		ResultSet res = state.executeQuery("SELECT active FROM users where username='"+ username+"'" );

		res.next();
		int r = res.getInt(1);
				
		
		_objPostgis.close();
		
		return r;
	}
	
	/**
	 *  
	 * @param username
	 * @param oldPassword
	 * @param newPassword
	 * @throws Exception
	 */
	public boolean changePassword(String username,String oldPassword, String newPassword) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		Statement state = _objPostgis.conn.createStatement();
		
		int res = state.executeUpdate("UPDATE users SET password=md5('"+newPassword+"') WHERE username='"+ username + "' AND password=md5('"+oldPassword+"') ");
		
		if(res!=0)
			return true;
		else
			return false;
		
	}
	
	
	/**
	 * 
	 * @param email
	 * @param username
	 * @param password
	 * @param type
	 * @param firstname
	 * @param name
	 * @param company
	 * @param maxconnexion
	 * @throws Exception 
	 */
	public String addUser(String email, String username, String password, int type, String firstname, String name, String company, int maxconnexion, int active) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		java.util.Date now = new java.util.Date();
        Date today = new Date(now.getTime());
        
        
        String api_key = StringTools.generate(10);
		
		PreparedStatement pstmt = _objPostgis.conn.prepareStatement("INSERT INTO users (email,type,name,firstname,company,username,password,nbconnexion,lastconnexion,maxconnexion,api_key,active) values(?, ?, ?, ?, ?, ?, md5('"+password+"'), ?, ?, ?, ?, ?)");
		pstmt.setString(1, email);
		pstmt.setInt(2,type);
		
		pstmt.setString(3, name);
		pstmt.setString(4, firstname);
		pstmt.setString(5, company);
		pstmt.setString(6, username);
		pstmt.setInt(7, 0);
		pstmt.setDate(8, today);
		pstmt.setInt(9, maxconnexion);
		pstmt.setString(10, api_key);
		pstmt.setInt(11, active);
		
		pstmt.execute();

		_objPostgis.close();

		
		//création du dossier utilisateur
		new File(_outputDir + username).mkdir();
	
		return api_key;
	}
	
	
	public void addColumnUser(String columnName, String type)
	{
		
		//type = text ou integer ...
		try{
			_objPostgis.connect(_objPostgis.db_users());
			
			Statement state = _objPostgis.conn.createStatement();
			
			
			String req = "ALTER TABLE users ADD COLUMN "+columnName+" "+type+";";
			
				
			state.executeUpdate(req);
			
			state.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public Vector<HashMap<String, String>> getUsers(String orderby)
	{
		
		Vector<HashMap<String, String>> users= new Vector<HashMap<String, String>>();
		
		
		try{
			_objPostgis.connect(_objPostgis.db_users());
			
			Statement state = _objPostgis.conn.createStatement();
		
			
			ResultSet res = state.executeQuery("SELECT * FROM users ORDER BY "+orderby+" DESC");
	
			while(res.next())
			{
				HashMap<String, String> hmap = new HashMap<String, String>();
				
				int group = res.getInt("type");
				if(group == 1)
					hmap.put("group", "admin");
				else
					hmap.put("group", "user");
				
				int active = res.getInt("active");
				if(active == 1)
					hmap.put("active", "on");
				else
					hmap.put("active", "off");
				
				hmap.put("email", res.getString("email"));
				hmap.put("username", res.getString("username"));
				hmap.put("company", res.getString("company"));
				hmap.put("firstname", res.getString("firstname"));
				hmap.put("name", res.getString("name"));
				hmap.put("maxconnexion", res.getString("maxconnexion"));
				hmap.put("nbconnexion", res.getString("nbconnexion"));
				
				
				
				SimpleDateFormat sdfr = new SimpleDateFormat("dd-MM-yyyy");
				
				
				//String date = res.getDate("lastconnexion").toString();
				
				hmap.put("lastconnexion", sdfr.format( res.getDate("lastconnexion") ));
				hmap.put("api_key", res.getString("api_key"));
				
				users.add(hmap);
			
			}
				
			//state.executeUpdate(req);
			
			state.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	
	public void createTableUser()
	{
		try{
			_objPostgis.connect(_objPostgis.db_users());
			
			Statement state = _objPostgis.conn.createStatement();
			
			
			String req = "CREATE TABLE users(api_key text, email text, type integer, password text, name text, firstname text, company text, username text, maxconnexion integer, lastconnexion date, nbconnexion integer, active integer)";
			
				
			state.executeUpdate(req);
			
			state.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * @param email
	 * @throws Exception
	 */
	public void deleteUser(String username) throws Exception
	{
		
		_objPostgis.connect(_objPostgis.db_users());
		
		
		Statement state = _objPostgis.conn.createStatement();
		
		state.executeQuery("DELETE FROM users where username='"+username+"' ");
		state.close();
		
	}
	
	

	
}
