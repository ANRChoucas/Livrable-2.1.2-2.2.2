package choucas.perdido.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import choucas.perdido.postgresql.User;
import choucas.perdido.tools.XmlTools;



/**
 * User_ws class
 * @author Ludovic Moncla
 * @version 1.0
 */
@Path("/user")
public class User_ws {


	@POST
    @Path("/add/")
	@Produces("application/xml") 
    @Consumes(MediaType.APPLICATION_JSON)
    public String addUserPOST(InputStream incomingData) {
       
        
		StringBuilder builder = new StringBuilder();
        String username = "",password = "",name = "", firstname = "", email = "", company = "";
        int maxconnexion =0,type = 0;
    
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            
            JSONObject obj = new JSONObject(builder.toString());
            
            
            JSONObject request = obj.getJSONObject("request");
           
            
            username = request.getString("username");
            password = request.getString("password");
            name = request.getString("name");
            company = request.getString("company");
            firstname = request.getString("firstname");
            email = request.getString("email");
            maxconnexion = request.getInt("maxconnexion");
            type = request.getInt("type");
            
            
            if(!username.isEmpty() && !password.isEmpty() && !name.isEmpty() && !firstname.isEmpty() && !email.isEmpty() && !company.isEmpty())
    		{
    			return addUser(username, password,name, firstname, email, company, maxconnexion, type);
    		
    		}
    		else
    		{
    			return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("Missing parameters"));
    		}

            
        }
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("An error occurs, please try again."));
			
		}
	}
	
	@GET  
    @Path("/add/") 
	@Produces("application/xml")  
    public String addUserGET(@DefaultValue("") @QueryParam("username") String username, @DefaultValue("") @QueryParam("password") String password,@DefaultValue("") @QueryParam("name") String name,@DefaultValue("") @QueryParam("firstname") String firstname,@DefaultValue("20") @QueryParam("maxconnexion") String maxconnexion,@DefaultValue("") @QueryParam("email") String email,@DefaultValue("") @QueryParam("company") String company, @DefaultValue("0") @QueryParam("type") String type) {  
		
		
		if(!username.isEmpty() && !password.isEmpty() && !name.isEmpty() && !firstname.isEmpty() && !email.isEmpty() && !company.isEmpty())
		{
			return addUser(username, password,name, firstname, email, company, Integer.parseInt(maxconnexion), Integer.parseInt(type));
		
		}
		else
		{
			return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("Missing parameters"));
		}

    }  
	
	
	
	
	private String addUser(String username, String password, String name, String firstname, String email, String company, int maxconnexion, int type)
	{
		String status = "";
		User user = new User();
		try {
		
			String api_key;
			
			api_key = user.addUser(email, username, password, type, firstname, name, company, maxconnexion, 1);
			
			status = XmlTools.getMsgXML("success",XmlTools.eltStatusXML("User '"+username+"' : api_key="+api_key+" added."));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("An error occurs, please try again."));
	
		}
		
		return status;
	}
	
	@GET  
    @Path("/addColumn/") 
	@Produces("application/xml")  
    public String addColumn(@DefaultValue("") @QueryParam("columnName") String columnName, @DefaultValue("") @QueryParam("type") String type) throws Exception{  
		String status = "";
		
		User user = new User();
		//if(!username.isEmpty() && !password.isEmpty() && !name.isEmpty() && !firstname.isEmpty() && !email.isEmpty() && !company.isEmpty())
		{
			user.addColumnUser(columnName, type);
			//String api_key = user.addUser(email, username, password, Integer.parseInt(type), firstname, name, company, Integer.parseInt(maxconnexion));
			//status = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><success>Done</success>";
			status = XmlTools.getMsgXML("success","Done");
		}
		
		
		return status;
    }  
	
	@GET  
    @Path("/del/") 
	@Produces("application/xml")  
    public String delUser(@DefaultValue("") @QueryParam("username") String username, @DefaultValue("") @QueryParam("password") String password) throws Exception{  
		String status = "";
		if(!username.isEmpty() && !password.isEmpty())
		{
			User user = new User();
			
			user.deleteUser(username);
			status = XmlTools.getMsgXML("success",XmlTools.eltStatusXML("User '"+username+"' deleted."));
		}
		else
		{
			status = XmlTools.getMsgXML("errors",XmlTools.eltStatusXML("Missing parameters"));
		}
		
		return status;
    }  
	

}
