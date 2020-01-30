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

package choucas.perdido.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Geonames2Postgis {

	public static void main(String[] args) {

		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("DRIVER POSTGRESQL : OK ");

			String url = "jdbc:postgresql://localhost:5432/Geonames";
			String user = "postgres";
			String passwd = "postgres";

			Connection conn = DriverManager.getConnection(url, user, passwd);
			System.out.println("CONNECTION POSTGRESQL : OK ");

			// Création d'un objet Statement
			Statement state = conn.createStatement();

			String srcGeonames = "/Users/lmoncla/Programme/Unitex3.1beta/Ressources/FR.txt";

			// lecture du fichier texte
			try {
				InputStream ips = new FileInputStream(srcGeonames);
				InputStreamReader ipsr = new InputStreamReader(ips);
				BufferedReader br = new BufferedReader(ipsr);

				System.out.println("CHARGEMENT FICHIER GEONAMES : OK ");
				String ligne;
				int v_id = 0;
				while ((ligne = br.readLine()) != null) {

					String str[] = ligne.split("\t");

					String v_id_geonames = str[0];
					String v_name = str[1];
					String v_alternate_name = str[2] + "," + str[3];
					String v_dept = str[11];
					String v_country = str[8];
					String v_lat = str[5];
					String v_lng = str[4];

					PreparedStatement pstmt = conn
							.prepareStatement("INSERT INTO toponyms values(?, ?, ?, ?, ST_SetSRID(ST_MakePoint("
									+ v_lat + "," + v_lng + "), 4326), ?, ?)");
					pstmt.setInt(1, v_id);
					pstmt.setString(2, v_name);
					pstmt.setString(3, v_country);
					pstmt.setString(4, v_dept);
					pstmt.setString(5, v_alternate_name);
					pstmt.setString(6, v_id_geonames);
					pstmt.executeUpdate();

					/*
					 * int res = state
					 * .executeUpdate("INSERT INTO toponyms VALUES (" + v_id +
					 * ",'" + v_name + "','" + v_country + "','" + v_dept + "',"
					 * + "ST_SetSRID(ST_MakePoint(" + v_lat + "," + v_lng +
					 * "), 4326)" + ",'" + v_alternate_name + "'," +
					 * v_id_geonames + ")" );
					 */
					System.out.println("INSERT : " + v_name);

					v_id++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("MISE A JOUR BD : TERMINE ");

			// si column de type geometry
			// ResultSet res =
			// state.executeUpdate("INSERT INTO toponyms VALUES (1,'test','test 1','france','123456789','64','32',ST_GeomFromText('POINT(42 23)', 4326))");
			// ST_SetSRID(ST_MakePoint(lng, lat), 4326)
			// si column de type POINT&

			/*
			 * int res = state .executeUpdate(
			 * "INSERT INTO toponyms VALUES (1,'test','test 1','france','123456789','64','32',ST_SetSRID(ST_MakePoint(42, 23), 4326))"
			 * );
			 * 
			 * 
			 * 
			 * //L'objet ResultSet contient le résultat de la requête SQL
			 * ResultSet result = state.executeQuery(
			 * "SELECT id, name, country, dept, reg, ST_X(coord), ST_Y(coord) FROM toponyms"
			 * );
			 * 
			 * 
			 * //On récupère les MetaData ResultSetMetaData resultMeta =
			 * result.getMetaData();
			 * 
			 * System.out.println("\n**********************************"); //On
			 * affiche le nom des colonnes for(int i = 1; i <=
			 * resultMeta.getColumnCount(); i++) System.out.print("\t" +
			 * resultMeta.getColumnName(i).toUpperCase() + "\t *");
			 * 
			 * System.out.println("\n**********************************");
			 * 
			 * while(result.next()){ for(int i = 1; i <=
			 * resultMeta.getColumnCount(); i++) { System.out.print("\t" +
			 * result.getObject(i).toString() + "\t |");
			 * 
			 * } System.out.println("\n---------------------------------");
			 * 
			 * }
			 * 
			 * 
			 * result.close();
			 */
			state.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
