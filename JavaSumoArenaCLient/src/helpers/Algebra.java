package helpers;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import draw.CirclePlainDrawable;
import draw.GUIHelper;
import draw.IDrawable;
import draw.LineDrawable;

import valueobjects.AccelerationVector;
import valueobjects.Line;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class Algebra {

	private static Map<Float, Map<Float, AccelerationVector>> meomization = new HashMap<Float, Map<Float, AccelerationVector>>();

	public static Point[] getIntersections(int circleRadius, Line line) {
		float a = line.getA();
		float b = line.getB();
		if (a == Float.MAX_VALUE)
			return getIntersectionsWithVerticalLine(circleRadius, line);
		// System.out.println("Algebra.getIntersections : line y = " + a +
		// " * x + " + b);
		Degre2 degre2 = new Degre2(1 + a * a, 2 * a * b, b * b - circleRadius
				* circleRadius);
		float[] x = degre2.resolve();
		if (x == null) {
			System.out
					.println("Algebra.getIntersections : [WARNING] delta is < 0");
			return null;
		}
		Point[] inter = new Point[2];
		inter[0] = new Point(x[0], a * x[0] + b);
		inter[1] = new Point(x[1], a * x[1] + b);
		// System.out.println("Algebra.getIntersections : arena radius=" +
		// circleRadius);
		// System.out.println("Algebra.getIntersections : sphere = ( " +
		// line.p.x + ", " + line.p.y + " )");
		// System.out.println("Algebra.getIntersections : v = ( " + line.v.dx +
		// ", " + line.v.dy + " )");
		// System.out.println("Algebra.getIntersections : inter0 = ( " +
		// inter[0].x + ", " + inter[0].y + " )");
		// System.out.println("Algebra.getIntersections : inter1 = ( " +
		// inter[1].x + ", " + inter[1].y + " )");
		IDrawable interLineD = new LineDrawable(Color.GREEN,new Point(GUIHelper.SHIFT + inter[0].x, GUIHelper.SHIFT + inter[0].y),new Point(GUIHelper.SHIFT + inter[1].x, GUIHelper.SHIFT + inter[1].y), 1F);
		GUIHelper.jc.addDrawable(interLineD);
	
		return inter;
	}

	private static Point[] getIntersectionsWithVerticalLine(int circleRadius,
			Line line) {
		//System.out.println("Algebra.getIntersectionsWithVerticalLine line=" + line);
		float b = line.getB();
		Point[] inter = new Point[2];
		inter[0] = new Point(b, (float) Math.sqrt(circleRadius * circleRadius
				- b * b));
		//System.out.println("Algebra.getIntersectionsWithVerticalLine inter[0]=" + inter[0]);
		inter[1] = new Point(b, -(float) Math.sqrt(circleRadius * circleRadius
				- b * b));
		//System.out.println("Algebra.getIntersectionsWithVerticalLine inter[1]=" + inter[1]);
		return inter;
	}

	public static Point getNextIntersection(Point from, Vector vector,
			Point[] points) {
		for (int i = 0; i < points.length; i++) {
			if (isPointIntersected(from, vector, points[i])) {
				//System.out.println("Algebra.getNextIntersection : intersection = ( "
					//			+ points[i].x + ", " + points[i].y + " )");
				IDrawable interD = new CirclePlainDrawable(Color.GREEN,new Point(GUIHelper.SHIFT + points[i].x, GUIHelper.SHIFT + points[i].y), 5, 1F);
				GUIHelper.jc.addDrawable(interD);				
				return points[i];
			}
		}
		System.out
				.println("[WARNING] Algebra.getNextIntersection : next intersection not found !");
		return null;
	}

	public static boolean isPointIntersected(Point from, Vector vector,
			Point point) {
		return ((point.x - from.x) * vector.dx >= 0 && (point.y - from.y)
				* vector.dy >= 0);
	}

	public static float getEuclidDistance(float x, float y) {
		return (float) Math.sqrt(x * x + y * y);
	}

	public static AccelerationVector getNormalizedAccelerationVector(float dx,
			float dy, int maxSpeedVariation) {
		Map<Float, AccelerationVector> map = meomization.get(dx);
		if (map == null) {
			AccelerationVector ac = computeNormalizedAccelerationVector(dx, dy,
					maxSpeedVariation);
			map = new HashMap<Float, AccelerationVector>();
			map.put(dy, ac);
			meomization.put(dx, map);
			return ac;
		}
		AccelerationVector value = map.get(dy);
		if (value == null) {
			AccelerationVector ac = computeNormalizedAccelerationVector(dx, dy,
					maxSpeedVariation);
			map.put(dy, ac);
			meomization.put(dx, map);
			return ac;
		}
		return value;
	}

	public static AccelerationVector computeNormalizedAccelerationVector(
			float dx, float dy, int maxSpeedVariation) {
		float v = Algebra.getEuclidDistance(dx, dy);
		int dVx = round(dx * maxSpeedVariation / v);
		int dVy = round(dy * maxSpeedVariation / v);
		return new AccelerationVector(dVx, dVy);
	}
	
	public static Vector normalize(Vector a){
		float length = getEuclidDistance(a.dx, a.dy);
		if (length==0)
			return new Vector(0, 0);
		return new Vector(a.dx/length, a.dy/length);
	} 
	
	public static Vector multiply(Vector a, float length){
		return new Vector(a.dx * length, a.dy * length);
	}

	public static Vector sub(Vector a, Vector b) {
		return new Vector(a.dx - b.dx, a.dy - b.dy);
	} 

	public static Vector add(Vector a, Vector b) {
		return new Vector(a.dx + b.dx, a.dy + b.dy);
	} 
	
	public static int round(float a){
		if (a>0)
			return (int)Math.floor(a);
		else
			return (int)Math.ceil(a);		
	}

	public static float getEuclidDistance(Vector v) {
		return getEuclidDistance(v.dx, v.dy);
	} 
	
	public static AccelerationVector makeSureMaxSpeedVariation(RoundStartInfo roundInfo, AccelerationVector ac) {
		if (!isOverMaxSpeed(roundInfo, ac))
			return ac;
		float dV = Algebra.getEuclidDistance(ac.getdVx(), ac.getdVy());
		int dVx = Algebra.round(ac.getdVx() * roundInfo.maxSpeedVariation / dV);
		int dVy = Algebra.round(ac.getdVy() * roundInfo.maxSpeedVariation / dV);
		return new AccelerationVector(dVx, dVy);		
	}
	public static  boolean isOverMaxSpeed(RoundStartInfo roundInfo, AccelerationVector ac) {
		return isOverEuclid(ac.getdVx(), ac.getdVy(), roundInfo.maxSpeedVariation);
	}
	
	public static int SECURITY = 5;
	
	public static  boolean isInArena(Sphere sphere, PlayingInfo playingInfo) {
		return !isOverEuclid(sphere.x, sphere.y, playingInfo.getArenaRadius() - 5);
	}
	
	public static  boolean isOverEuclid(int x, int y, int dist){
		return (x*x+y*y>=dist*dist);
	}

	public static float dot(Vector a , Vector b){
		return a.dx*b.dx + a.dy*b.dy;
	}
	
	





}
