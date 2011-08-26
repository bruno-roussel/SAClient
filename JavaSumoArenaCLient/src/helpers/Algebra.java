package helpers;

import valueobjects.Circle;
import valueobjects.Line;
import valueobjects.Point;
import valueobjects.Vector;

public class Algebra {

	public static Point[] getIntersections(int circleRadius, Line line){
		float a = line.getA();
		float b = line.getB();
		System.out.println("Algebra.getIntersections : line y = " + a + " * x + " + b);
		Degre2 degre2 = new Degre2(1+a*a,2*a*b,b*b - circleRadius * circleRadius);
		float[] x = degre2.resolve();
		if (x == null){
			System.out.println("Algebra.getIntersections : [WARNING] delta is < 0");
			return null;
		}
		Point[] inter = new Point[2];
		inter[0] = new Point(x[0], a * x[0] + b);	
		inter[1] = new Point(x[1], a * x[1] + b);
		System.out.println("Algebra.getIntersections : arena radius=" + circleRadius);
		System.out.println("Algebra.getIntersections : sphere = ( " + line.p.x + ", " + line.p.y + " )");
		System.out.println("Algebra.getIntersections : v = ( " + line.v.dx + ", " + line.v.dy + " )");
		System.out.println("Algebra.getIntersections : inter0 = ( " + inter[0].x + ", " + inter[0].y + " )");
		System.out.println("Algebra.getIntersections : inter1 = ( " + inter[1].x + ", " + inter[1].y + " )");
		return inter;
	}
	
	public static Point getNextIntersection(Point from, Vector vector, Point[] points){
		for (int i =0; i<points.length;i++){
			if (isPointIntersected(from, vector, points[i])){
				System.out.println("Algebra.getNextIntersection : intersection = ( " + points[i].x + ", " + points[i].y + " )");
				return points[i];
			}
		}
		System.out.println("[WARNING] Algebra.getNextIntersection : next intersection not found !");
		return null;
	}

	public static boolean isPointIntersected(Point from, Vector vector, Point point){
		return ((point.x - from.x) * vector.dx >0 && (point.y - from.y) * vector.dy>0);		
	}
	
	public static float getEuclidDistance(float x, float y) {
		return (float) Math.sqrt(x*x + y*y);
	}

}
