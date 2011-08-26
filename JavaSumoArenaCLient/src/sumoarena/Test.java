package sumoarena;

import helpers.Algebra;
import valueobjects.Circle;
import valueobjects.Line;
import valueobjects.Point;
import valueobjects.Vector;



/**
 * Test parsing an FCL file
 * 
 * @author pcingola@users.sourceforge.net
 */
public class Test {
	public static void main(String[] args) throws Exception {
		Point p = new Point(-2,0);
		Vector v = new Vector(1,-1);
		Line line = new Line(p,v);
		Point[] intersections = Algebra.getIntersections(10, line);
		Point nextIntersection = Algebra.getNextIntersection(p, v, intersections);
	}
}