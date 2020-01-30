package processing;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class Cluster - Represents a Cluster in DBSCAN 
 * @version 1.0
 * @author jmishra
 */
public class ClusterDBSCAN {
	
	/**
	 * Points Contained in the Cluster
	 */
	public ArrayList points = new ArrayList();
	
	/**
	 * Returns the Size of Cluster
	 * @return
	 * int Size of Cluster
	 */
	public int size() {
		return points.size();
	}
	
	/**
	 * Calculates if the Point P is in EPS Neighborhood of the Cluster 
	 * @param p Point p
	 * @param eps EPS Distance
	 * @return
	 * boolean <b>true</b> If the point lies in EPS Neighborhood of all points of cluster
	 * 		   <b>false</b> Otherwise
	 */
	public boolean isInEPSProximityFromAllPoints(Point p, double eps) {
		boolean result = true;
		Iterator iter = points.iterator();
		while(iter.hasNext()) {
			Point t = (Point)iter.next();
			if(t.calcDistanceFromPoint(p) > eps) {
				result = false;
				break;
			}
		}
		return result;
	}
}