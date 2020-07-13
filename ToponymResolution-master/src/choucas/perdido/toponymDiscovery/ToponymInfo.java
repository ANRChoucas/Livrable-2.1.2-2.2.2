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

package choucas.perdido.toponymDiscovery;

public class ToponymInfo {
	private String name;
	private String latitude;
	private String longitude;
	private String country;
	private String continent;
	
	/** INSPIRE type of toponym */
	private String type; 
	
	/** some gazetteers add an internal local type, apart from INSPIRE type */
	private String localType; 

	public ToponymInfo() {
	}
	
	public ToponymInfo(String name, String latitude, String longitude,
			String country, String continent, String type, String localType) {
		setName(name);
		setLatitude(latitude);
		setLongitude(longitude);
		setCountry(country);
		setContinent(continent);
		setType(type);
		setLocalType(localType);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getContinent() {
		return continent;
	}
	public void setContinent(String continent) {
		this.continent = continent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getLocalType() {
		return localType;
	}

	public void setLocalType(String localType) {
		this.localType = localType;
	}

	@Override
	public String toString() {
		return name + "\t" + latitude
				+ "\t" + longitude
				+ "\t" + country
				+ "\t" + continent
				+ "\t" + type
				+ "\t" + localType;
	}
	
	
}
