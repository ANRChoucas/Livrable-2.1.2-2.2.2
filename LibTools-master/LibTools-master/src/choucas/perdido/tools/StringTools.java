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


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

/**
 * StringTools class : provide some methods for string
 * @author Ludovic Moncla
 * @version 1.0
 */
public class StringTools  {

	/**
	 * Generate a random string of the given lenght
	 * @param length
	 * @return
	 */
	public static String generate(int length) {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder pass = new StringBuilder(length);
		for (int x = 0; x < length; x++) {
			int i = (int) (Math.random() * chars.length());
			pass.append(chars.charAt(i));
		}
		return pass.toString();
	}

	
	
	

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	
	public static byte[] encodeUTF8(String string) {
	    return string.getBytes(UTF8_CHARSET);
	}
	
	public static String decodeUTF8(byte[] bytes) {
	    return new String(bytes, UTF8_CHARSET);
	}

	
	
	/**
	 * Get name witout extension
	 * @param name
	 * @return string
	 */
	public static String getNameWithoutExtention(String name) throws Exception{
		
		return name.substring(0, name.lastIndexOf("."));
	}
	
	
	/**
	 * Get username from email
	 * @param email
	 * @return string
	 */
	public static String getUsernameFromEmail(String email) throws Exception{
		
		return email.substring(0, email.lastIndexOf("@"));
	}
	
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static String getExtension(String name) throws Exception{
		return name.substring(name.lastIndexOf("."));
	}
	
	/**
	 * 
	 * @param chaine
	 * @return
	 */
	public static int nombreDeMots(String chaine) {
		int n = 0;
		String[] mots = chaine.trim().split(" ");// pour ne pas compter les
													// espaces de début ni de
													// fin

		for (int i = 0; i < mots.length; i++) {
			// Si y'a bien un mots qui sépare deux espaces
			if (!mots[i].equals(""))
				n++;
		}
		return (n + 1); // Car nombre de mots = nombre d'espaces plus 1
	}
	
	
	
	
/**
 * 
 * @param value
 * @return
 */
	public static String capitalizeFirstLetter(String value) {
		if (value == null) {
			return null;
		}
		if (value.length() == 0) {
			return value;
		}
		
		// on supprime si ca commence par l' ex: l'île
		Pattern pattern = Pattern.compile("^.'|^.’");
		Matcher matcher = pattern.matcher(value);
		value = matcher.replaceAll("");
		
		if (value.equals("")) {
			return "";
		}
		
		StringBuilder result = new StringBuilder(value);
		result.replace(0, 1, result.substring(0, 1).toUpperCase());
		
		return result.toString();
	}

	
	
	
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	public static String filtreString(String content) {

		
		content = content.replaceAll("œ", "oe");
		content = content.replaceAll("@", "");
		content = content.replaceAll("°", "");
		content = content.replaceAll("«", "");
		content = content.replaceAll("»", "");
		
		
		content = content.replaceAll("’", "'");
		content = content.replaceAll("’", "'");
		
		content = content.replaceAll("’", "'");
		content = content.replaceAll("`", "'");
		content = content.replaceAll("“", "\"");
		content = content.replaceAll("…", ".");
		content = content.replaceAll("–", "-");
		content = content.replaceAll("―", "-");
		content = content.replaceAll("—", "-");
		
		content = content.replaceAll("”", "\"");
		
		content = content.replaceAll("_", "");
		
		content = content.replaceAll("°", "");
		
		content = content.replaceAll("<[^>]*>", "");
		
		content = content.replaceAll(">", "'");
		content = content.replaceAll("<", "'");
		
		
		//content = content.replaceAll("(http|https|ftp|mailto):(\\S*)", " ");
		
		content = content.replaceAll(" & ", " and ");
		content = content.replaceAll("&", " and ");
		content = content.replaceAll("/", " ");
		content = content.replaceAll("€", "euros");


		content = content.replaceAll("\' ", "\'"); // supprime l'espace rajouté
													// apres un apostrophe

		return content;
	}

		
	/**
	 * 
	 * @param string
	 * @return
	 */
	public static String stringToHTMLString(String string) {

		StringBuffer sb = new StringBuffer(string.length());
		// true if last char was blank
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);
			if (c == ' ') {
				// blank gets extra work,
				// this solves the problem you get if you replace all
				// blanks with &nbsp;, if you do that you loss
				// word breaking
				if (lastWasBlankChar) {
					lastWasBlankChar = false;
					sb.append("&nbsp;");
				} else {
					lastWasBlankChar = true;
					sb.append(' ');
				}
			} else {
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if (c == '"')
					sb.append("&quot;");
				else if (c == '&')
					sb.append("&amp;");
				else if (c == '<')
					sb.append("&lt;");
				else if (c == '>')
					sb.append("&gt;<br />");
				else if (c == '\n')
					// Handle Newline
					sb.append("&lt;br/&gt;");
				else {
					int ci = 0xffff & c;
					if (ci < 160)
						// nothing special only 7 Bit
						sb.append(c);
					else {
						// Not 7 Bit use the unicode system
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param in
	 * @return
	 */
	public static String stripNonValidXMLCharacters(String in) { 
        StringBuffer out = new StringBuffer(); // Used to hold the output. 
        char current; // Used to reference the current character. 
 
        if (in == null || ("".equals(in))) return ""; // vacancy test. 
        for (int i = 0; i < in.length(); i++) { 
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen. 
            if ((current == 0x9) || 
                (current == 0xA) || 
                (current == 0xD) || 
                ((current >= 0x20) && (current <= 0xD7FF)) || 
                ((current >= 0xE000) && (current <= 0xFFFD)) || 
                ((current >= 0x10000) && (current <= 0x10FFFF))) 
                out.append(current); 
        } 
        return out.toString(); 
    } 

}
