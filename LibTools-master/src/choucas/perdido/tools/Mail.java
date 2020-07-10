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

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * 
 * @author lmoncla
 *
 */
public class Mail
{
	private static String _from = "ludovic.moncla@univ-pau.fr";
	private static String _to = "moncla.ludovic@gmail.com";
	   
	private static String _host = "smtp.univ-pau.fr";
	private static String _port = "587";
    private static final String _login = "lmoncla";
    private static final String _password = "euhtruc";
    
    
    public static void main(String [] args)
    {    
 	  // Mail mail = new Mail();
 	   
 	   
 	  
 	   
 	   String username = "lmoncla";
 	   String api_key = "b21g058SgE"; 	   
 	   String dest = "ludovic.moncla@univ-pau.fr";
 	 
 	 	MsgAPI_fr(dest,username,api_key);
 		MsgAPI_en(dest,username,api_key);
 	 	
 		/*
 	 	username = "mgaio";
  	    api_key = "VH8TBROCae"; 	   
  	    dest = "mauro.gaio@univ-pau.fr";
  	    
  	    MsgAPI_fr(dest,username,api_key);
  	    
  	    username = "velaga";
	    api_key = "L4oykIyFr8"; 	   
	    dest = "vlazgar@unizar.es";
	    
	    MsgAPI_en(dest,username,api_key);
	    
	    username = "verolg";
	    api_key = "LIgvjxbBA8"; 	   
	    dest = "62711@unizar.es";
	    
	    MsgAPI_en(dest,username,api_key);
	    
	    username = "jnog";
	    api_key = "Wfoq7RVVBk"; 	   
	    dest = "jnog@unizar.es";
	    
	    MsgAPI_en(dest,username,api_key);
	    
	    username = "smustiere";
	    api_key = "ATADwSLqfX"; 	   
	    dest = "sebastien.mustiere@ign.fr";
	    
	    MsgAPI_fr(dest,username,api_key);
	    
	    username = "maurel";
	    api_key = "bqP84Xzb0y"; 	   
	    dest = "denis.maurel@univ-tours.fr";
	    
	    MsgAPI_fr(dest,username,api_key);
	    
	    username = "sabrine";
	    api_key = "XLsHSrFEek"; 	   
	    dest = "sabrinee.ayachi@gmail.com";
	    
	    MsgAPI_fr(dest,username,api_key);
	    
	    username = "ofavre";
	    api_key = "TdaOt0KeKB"; 	   
	    dest = "olivierfavre@gmail.com";
	    
	    MsgAPI_fr(dest,username,api_key);
 	 	*/
 	   
    }
    
    
    public static void MsgAPI_en(String dest, String username, String api_key)
    {
    	
    	 Mail mail = new Mail();
    	 String subject = "PERDIDO: New web API";
  	   
  	   String msg = "Dear "+username+",\n\n" +
  	   		"The PERDIDO website has been updated (http://erig.univ-pau.fr/PERDIDO/).\n\n" +
  			"You can find the specifications of the new PERDIDO web API at this address: http://erig.univ-pau.fr/PERDIDO/PERDIDO_API_specifications.pdf \n\n" +
  	   		"You need an API KEY to use the PERDIDO API.\n" +
  			"You can find the information related to your account in 'Options>My Account' after login.\n\n" +
  			"Your API KEY is: "+api_key+"\n\n" +
  	   		"Best Regards,\n\n" +
  	   		"Ludovic Moncla \n\n"+
  	   		"-------------------------------------\n" +
	   		"This is an automatically generated message.\n You are receiving this email because you are registered on the website: <http://erig.univ-pau.fr/PERDIDO/>\n" +
	   		"-------------------------------------\n";
  	   
  	   
  	   
  	   mail.sendMail(subject, msg, dest);
    }
    
    public static void MsgAPI_fr(String dest, String username, String api_key)
    {
    	
    	 Mail mail = new Mail();
    	 String subject = "PERDIDO : Nouveaux services web";
  	   
  	   String msg = "Bonjour "+username+",\n\n" +
  			 "Le site internet PERDIDO a été mis à jour (http://erig.univ-pau.fr/PERDIDO/).\n\n" +
  	   		//"The PERDIDO website has been updated (http://erig.univ-pau.fr/PERDIDO/).\n\n" +
  	   		"Vous pouvez trouver les spécifications des nouveaux services web à cette adresse : http://erig.univ-pau.fr/PERDIDO/PERDIDO_API_specifications.pdf \n\n" +
  	   		//"You can find the specifications of the new PERDIDO web API at this address: http://erig.univ-pau.fr/PERDIDO/PERDIDO_API_specifications.pdf \n\n" +
  	   		"Vous avez maintenant besoin d'une clé d'API (API KEY) pour utiliser les services web.\n" +
  	   		//"You need an API KEY to use the PERDIDO API.\n" +
			"Vous pouvez trouver les informations relatives à votre compte dans 'Options>My account' après s'être connecté.\n\n" + 
  	   		//"You can find the information related to your account in 'Options>My Account' after login.\n\n" +
  	   		"Votre clé d'API (API KEY) est : "+api_key+"\n\n" +
  	   		//"Your API KEY is: "+api_key+"\n\n" +
  	   		"Cordialement,\n\n" +
  	   		//"Best Regards,\n\n" +
  	   		"Ludovic Moncla \n\n" +
  	   		"-------------------------------------\n" +
  	   		"Ce message est généré automatiquement.\n Vous le recevez car vous êtes inscrit sur le site : <http://erig.univ-pau.fr/PERDIDO/>\n" +
  	   		"-------------------------------------\n";
  	   
  	   mail.sendMail(subject, msg, dest);
    }
    
    /**
     * 
     * @param subject
     * @param msg
     */
    public static void sendMail(String subject, String msg, String to)
    {
    	
       
        // Get system properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", _host);
        properties.put("mail.smtp.port", _port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Get the default Session object.
        //Session session = Session.getDefaultInstance(properties);
        Session session = Session.getDefaultInstance(properties, 
                new Authenticator(){
                   protected PasswordAuthentication getPasswordAuthentication() {
                      return new PasswordAuthentication(_login, _password);
                   }});

        try{
           // Create a default MimeMessage object.
           MimeMessage message = new MimeMessage(session);

           // Set From: header field of the header.
           message.setFrom(new InternetAddress(_from));

           // Set To: header field of the header.
           message.addRecipient(Message.RecipientType.TO,
                                    new InternetAddress(to));

           // Set Subject: header field
           message.setSubject(subject);

           // Now set the actual message
           message.setText(msg);

           // Send message
           Transport.send(message);
           System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
           mex.printStackTrace();
        }
     }
	
	
	
   
}
