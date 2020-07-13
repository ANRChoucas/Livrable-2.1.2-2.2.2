package choucas.perdido.toponymDiscovery;

/** 
 * A factory to generate the appropriate objects according to the gazetteer that must be invoked
 */
public abstract class ToponymArtifactFactory {
	
	public abstract WFSClient makeWFSClient(int maxResults);
	
	public abstract ToponymFinder makeToponymFinder(int maxResults) ;
	

}
