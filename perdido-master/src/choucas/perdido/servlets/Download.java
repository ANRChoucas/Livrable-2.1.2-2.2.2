package liuppa.perdido.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet; // inconnu en tomcat 5.5
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/**
 * Servlet implementation class HelloServlet
 */
public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10 ko
   
  public static String output = "";
    
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
        execution(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		execution(request, response);
        
	}
	
	protected void execution(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("** GET ** ");

		System.out.println("** DEBUT DOWNLOAD **");
		
		HttpSession session = request.getSession(true);
		
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream is = cl
				.getResourceAsStream("/perdido/resources/config.properties");
		Properties props = new Properties();
		try {
			props.load(is);
			output = props.getProperty("outputDir");
			
			output += "/"+session.getAttribute("username");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String fichierRequis = request.getPathInfo();
		
		/* Vérifie qu'un fichier a bien été fourni */
		if ( fichierRequis == null || "/".equals( fichierRequis ) ) {
		    /* Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas */
		    response.sendError(HttpServletResponse.SC_NOT_FOUND);
		    return;
		}
		
		
		
		
		
		/* Décode le nom de fichier récupéré, susceptible de contenir des espaces et autres caractères spéciaux, et prépare l'objet File */
		fichierRequis = URLDecoder.decode( fichierRequis, "UTF-8");
		
		// on supprime l'extension 
		
		int i = fichierRequis.lastIndexOf(".");
		String filename = fichierRequis.substring(0, i);
		
		System.out.println("++ fichierRequis : "+fichierRequis);
		System.out.println("++ filename : "+filename);
		
		File fichier = new File( output, filename+"/"+fichierRequis );
		        
		/* Vérifie que le fichier existe bien */
		if ( !fichier.exists() ) {
			/* Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas */
		    response.sendError(HttpServletResponse.SC_NOT_FOUND);
		    return;
		}
		
		/* Récupère le type du fichier */
		String type = getServletContext().getMimeType( fichier.getName() );
		
		/* Si le type de fichier est inconnu, alors on initialise un type par défaut */
		if ( type == null ) {
		    type = "application/octet-stream";
		}
		
		
		
		
		
		// type = "application/x-download";
		
		
		System.out.println("++ type : "+type);
		
		/* Initialise la réponse HTTP */
		response.reset();
		response.setBufferSize( DEFAULT_BUFFER_SIZE );
		response.setContentType( type );
		
		response.setHeader( "Content-Length", String.valueOf( fichier.length() ) );
		response.setHeader( "Content-Disposition", "attachment; filename=" + fichier.getName());
		
				
		/* Prépare les flux */
        BufferedInputStream entree = null;
        BufferedOutputStream sortie = null;
        try {
            /* Ouvre les flux */
            entree = new BufferedInputStream( new FileInputStream( fichier ), DEFAULT_BUFFER_SIZE );
            sortie = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );

            /* Lit le fichier et écrit son contenu dans la réponse HTTP */
            byte[] tampon = new byte[DEFAULT_BUFFER_SIZE];
            int longueur;
            while ( ( longueur = entree.read( tampon ) ) > 0 ) {
                sortie.write( tampon, 0, longueur );
            }
        } finally {
            sortie.close();
            entree.close();
        }
        
        
        System.out.println("** FIN DOWNLOAD **");
	}

		
	
}
