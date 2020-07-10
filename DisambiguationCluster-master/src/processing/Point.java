package processing;

/**
 * Class Point - Stores a Point in the 2 Dimensional Space
 * @version 1.0
 * @author jmishra. Adapted to DB in postgreSQL by Walter Renteria Agualimpia
 */
public class Point {
	
	/** The following are the member variables for a Point **/
	public double x;
	public double y;
	public int index;
	public ClusterDBSCAN cluster;
	public boolean isCorePoint = false;
	
	public Point() {
	}
	
	public Point(double x, double y, int index) {
		this.x = x;
		this.y = y;
		this.index = index;
	}
	
	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	/**
	 * Calculate Distance from a Point t
	 * @param t Point
	 * @return
	 * double Euclidean Distance from t
	 */
	public double calcDistanceFromPoint(Point t) {
		return Math.sqrt(Math.pow(x-t.x, 2) + Math.pow(y-t.y, 2));
	}

	public boolean equals(Point t) {
		return (x == t.x) && (y == t.y);
	}
	
	public void setCluster(ClusterDBSCAN cluster) {
		this.cluster = cluster;
	}
	
	public ClusterDBSCAN getCluster() {
		return cluster;
	}
	
	public double[] toDouble() {
		double[] xy = {x,y};
		return xy; 
	}
}