package choucas.perdido.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet; // inconnu en tomcat 5.5
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import choucas.perdido.postgresql.User;





/**
 * Servlet implementation class HelloServlet
 */
@WebServlet("/Admin")
// inconnu en tomcat 5.5
public class Admin extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public static final String URL = "/admin.jsp";
	
	public static String _codeJs = "";
	public static String _listOfMembers = "";

	/**
	 * 
	 */
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

		System.out.println("** GET ** ");
		
		
		
  
    	getListUsers();
			
			
			request.setAttribute("codeJs", _codeJs);
			request.setAttribute("listOfMembers", _listOfMembers);
	    	
    
    	
    	this.getServletContext().getRequestDispatcher(URL).forward(request, response);
			
	}
	

	
	/**
	 * 
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

		System.out.println("** POST ** ");
		
		String action =  request.getParameter("action");
		
		if(action.equals("activeUser"))
		{
			
			

			String slider = request.getParameter("slider-flip-m");
			String username = request.getParameter("username");
			
			
			System.err.println("slider : "+slider);
			System.err.println("username : "+username);
			
			//faire une requete pour activer le compte de l'utilisateur
			User user = new User();
			try {
				
				if(slider.equals("on"))
					user.activeUser(username, 1);
				else
					user.activeUser(username, 0);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if(action.equals("setAdmin"))
		{
			String slider = request.getParameter("slider-flip-group");
			String username = request.getParameter("username");
			
			System.err.println("slider : "+slider);
			System.err.println("username : "+username);
			
			User user = new User();
			try {
				
				if(slider.equals("admin"))
					user.setAdmin(username, 1);
				else
					user.setAdmin(username, 0);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		if(action.equals("setLimit"))
		{
			int limit = Integer.parseInt(request.getParameter("limit"));
			String username = request.getParameter("username");
			
			System.err.println("limit : "+limit);
			System.err.println("username : "+username);
			
			User user = new User();
			try {

				user.setLimit(username, limit);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		getListUsers();
		
		request.setAttribute("codeJs", _codeJs);
		request.setAttribute("listOfMembers", _listOfMembers);
	
		this.getServletContext().getRequestDispatcher(URL).forward(request, response);
	}
	
	
	
	private void getListUsers(){
		
		String listOfMembers = "";
		String codeJs = "";
		
	try {
    	//Processing pr = new Processing();
		//pr.init();
		
		Vector<HashMap<String, String>> users= new Vector<HashMap<String, String>>();
		User u = new User();
		users = u.getUsers("lastconnexion");
		
		Iterator<HashMap<String, String>> it = users.iterator();
		 
		while (it.hasNext()) {
			HashMap<String, String> user = it.next();
			
			
			codeJs += "$(document).on('change', '#slider_"+user.get("username")+"', function(){"; 
			codeJs += "if($(\"#slider_"+user.get("username")+" option:selected\").attr('value') == 'on') {";
			codeJs += "console.log(\"slider on\");";
			codeJs += "}";
			codeJs += "if($(\"#slider_"+user.get("username")+" option:selected\").attr('value') == 'off') {";
			codeJs += "   console.log(\"slider off\");";
			codeJs += "}";
			
			codeJs += "$(form_slider_"+user.get("username")+").submit();";
			codeJs += "});";
			
			codeJs += "$(document).on('change', '#slider_group_"+user.get("username")+"', function(){"; 
			codeJs += "if($(\"#slider_group_"+user.get("username")+" option:selected\").attr('value') == 'admin') {";
			codeJs += "console.log(\"slider admin\");";
			codeJs += "}";
			codeJs += "if($(\"#slider_group_"+user.get("username")+" option:selected\").attr('value') == 'user') {";
			codeJs += "   console.log(\"slider user\");";
			codeJs += "}";
			
			codeJs += "$(form_slider_group_"+user.get("username")+").submit();";
			codeJs += "});";
			
		 
			listOfMembers +="<tr>";
			
			listOfMembers += "<td>";
			listOfMembers += "<form id='form_slider_"+user.get("username")+"' action='admin' method='post' data-ajax='false'>";
			listOfMembers += "<input type='hidden' name='action' value='activeUser' />";
			listOfMembers += "<input type='hidden' name='username' value='"+user.get("username")+"' />";
			listOfMembers += "<select name='slider-flip-m' id='slider_"+user.get("username")+"' data-role='slider' data-mini='true' >";
			
			//listOfMembers += "onclick=\"activeUser('"+user.get("username")+"')'\"";
					
			//listOfMembers += ">";
			listOfMembers += "<option value='off' ";
			if(user.get("active").equals("off"))
				listOfMembers += "selected='selected'";
			
			listOfMembers += ">No</option>";
			
			listOfMembers += "<option value='on' ";
			if(user.get("active").equals("on"))
				listOfMembers += "selected='selected'";
			
			listOfMembers += ">Yes</option>";
			listOfMembers += "</select></form></td>";
			
			//listOfMembers += "<td>"+user.get("group")+"</td>"; //group
			
			
			listOfMembers += "<td>";
			listOfMembers += "<form id='form_slider_group_"+user.get("username")+"' action='admin' method='post' data-ajax='false'>";
			listOfMembers += "<input type='hidden' name='action' value='setAdmin' />";
			listOfMembers += "<input type='hidden' name='username' value='"+user.get("username")+"' />";
			listOfMembers += "<select name='slider-flip-group' id='slider_group_"+user.get("username")+"' data-role='slider' data-mini='true' >";
					listOfMembers += "<option value=\"user\" ";
					if(user.get("group").equals("user"))
						listOfMembers += "selected='selected'";
					listOfMembers += ">user</option>";
					listOfMembers += "<option value=\"admin\" ";
					if(user.get("group").equals("admin"))
						listOfMembers += "selected='selected'";
					listOfMembers += ">admin</option>";
			listOfMembers += "</select></form>";
			listOfMembers += "</td>";
			
			listOfMembers += "<td>"+user.get("username")+"</td>"; //
			listOfMembers += "<td>"+user.get("email")+"</td>";
			//listOfMembers += "<td>"+user.get("firstname")+"</td>";
			//listOfMembers += "<td>"+user.get("name")+"</td>";
			//listOfMembers += "<td>"+user.get("company")+"</td>";
			
			
			listOfMembers += "<td>"+user.get("api_key")+"</td>";
			
			
			listOfMembers += "<td>"+user.get("lastconnexion")+"</td>";
			
			listOfMembers += "<td>"+user.get("nbconnexion")+"</td>";
			
			listOfMembers += "<td><form action='admin' method='post' data-ajax='false'>";
			listOfMembers += "<input type='hidden' name='action' value='setLimit' />";
			listOfMembers += "<input type='hidden' name='username' value='"+user.get("username")+"' />";
			
			listOfMembers += "<div class='ui-grid-a' style=\"margin-top:0px;\">";
			listOfMembers += "<div class='ui-block-b'><input type='number' data-clear-btn='false' data-mini='true' name='limit' id='limit_"+user.get("username")+"' value='"+user.get("maxconnexion")+"'></div>";
			listOfMembers += "<div class='ui-block-b'><button type='submit' id='limit_btn_'"+user.get("username")+" data-mini='true' >Ok</button>";  
			listOfMembers += "</div>";
			
			
			//listOfMembers += "<span id='limit_"+user.get("username")+"' contenteditable='true'>"+user.get("maxconnexion")+"</span>";
			listOfMembers += "";
			
			
			listOfMembers += "</form></td>";
			
			listOfMembers += "</tr>";
		 
		}
		
		
		_codeJs = codeJs;
		_listOfMembers = listOfMembers;
		
		
		
    	
	}
	catch(Exception e){
		//request.setAttribute("resultat", "<div class='resultat'><span class='erreur'>An error has occurred, try again later.</span></div>");
		//this.getServletContext().getRequestDispatcher(URL).forward(request, response);
		return;
	}
}
	
	
	
}
