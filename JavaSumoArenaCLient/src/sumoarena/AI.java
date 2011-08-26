package sumoarena;

import ai.MaxSpeedAhead;
import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.Line;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class AI {

	private int dx;
	private int dy;
	private RoundStartInfo roundInfo;
	private MaxSpeedAhead maxSpeedAhead;
	
	public AI(RoundStartInfo roundInfo){
		dx = (int) Math.round(Math.random() * 20 - 10);
		dy = (int) Math.round(Math.random() * 20 - 10);
		this.roundInfo = roundInfo;
		maxSpeedAhead = new MaxSpeedAhead(roundInfo.maxSpeedVariation, new Vector(dx,dy));
	}
		
	public AccelerationVector getAccelerationVector(PlayingInfo playingInfo) {
		Sphere sphere = playingInfo.getSpheres()[roundInfo.myIndex];
		dx = (int) Math.round(Math.random() * 20 - 10);
		dy = (int) Math.round(Math.random() * 20 - 10);
		AccelerationVector ac = new AccelerationVector(dx, dy);
		if (isTooMuchInerty(sphere, playingInfo)){
			ac = getMaxBrake(sphere);
		}
		Sphere nextPosition = getNextPosition(sphere, ac, playingInfo);
		if (!nextPosition.inArena){
			ac = getMaxBrake(sphere);
		}
		return makeSureMaxSpeedVariation(ac);
	}

	private boolean isOverMaxSpeed(AccelerationVector ac) {
		return isOverEuclid(ac.getdVx(), ac.getdVy(), roundInfo.maxSpeedVariation);
	}

	private boolean isInArena(Sphere sphere, PlayingInfo playingInfo) {
		return !isOverEuclid(sphere.x, sphere.y, playingInfo.getArenaRadius());
	}
	
	private boolean isOverEuclid(int x, int y, int dist){
		return (x*x+y*y>dist*dist);
	}

	private Sphere getNextPosition(Sphere sphere, AccelerationVector ac, PlayingInfo playingInfo){
		Sphere next = sphere.clone();
		next.x = sphere.x + sphere.vx + ac.getdVx();
		next.y = sphere.y + sphere.vy + ac.getdVy();
		next.vx = sphere.vx + ac.getdVx();
		next.vy = sphere.vy + ac.getdVy();
		next.inArena = isInArena(next, playingInfo);
		System.out.println("next :" + next.toString());
		return next;
	}
	
	private AccelerationVector getMaxBrake(Sphere sphere) {
		float v = Algebra.getEuclidDistance(sphere.vx, sphere.vy);
		int dVx = - Math.round(sphere.vx * roundInfo.maxSpeedVariation / v);
		int dVy = - Math.round(sphere.vy * roundInfo.maxSpeedVariation / v);
		return new AccelerationVector(dVx, dVy);
	}
	
	private int getInertyRoundsToStop(Sphere sphere){
		return (int)Math.floor(Algebra.getEuclidDistance(sphere.vx, sphere.vy) / roundInfo.maxSpeedVariation);
	}

	private float getInertyDistToStop(Sphere sphere){
		int vx = sphere.vx;
		int vy = sphere.vy;
		float currentSpeed = Algebra.getEuclidDistance(vx, vy) - roundInfo.maxSpeedVariation;
		float dist = 0;
		while(currentSpeed>0){
			dist+=currentSpeed;
			currentSpeed = currentSpeed - roundInfo.maxSpeedVariation;			
		}
		return dist;
	}
	
	private AccelerationVector makeSureMaxSpeedVariation(AccelerationVector ac) {
		if (!isOverMaxSpeed(ac))
			return ac;
		float dV = Algebra.getEuclidDistance(ac.getdVx(), ac.getdVy());
		int dVx = Math.round(ac.getdVx() * roundInfo.maxSpeedVariation / dV);
		int dVy = Math.round(ac.getdVy() * roundInfo.maxSpeedVariation / dV);
		return new AccelerationVector(dVx, dVy);		
	}

	public boolean isTooMuchInerty(Sphere sphere, PlayingInfo playingInfo){
		Point p = new Point(sphere.x,sphere.y);
		Vector v = new Vector(sphere.vx,sphere.vy);
		Line line = new Line(p,v);
		Point[] intersections = Algebra.getIntersections(playingInfo.getArenaRadius(), line);
		Point nextIntersection = Algebra.getNextIntersection(p, v, intersections);
		float inertyDist = getInertyDistToStop(sphere);
		if (nextIntersection == null){
			System.out.println("[WARNING] isTooMuchInerty > intersection with circle not found !");
			return false;
		}
		float maxDist = Algebra.getEuclidDistance(nextIntersection.x - sphere.x, nextIntersection.y - sphere.y);
		System.out.println("isTooMuchInerty : inertyDist=" + inertyDist + ", maxDist=" + maxDist);
		if (inertyDist > maxDist){
			System.out.println("isTooMuchInerty > true");
			return true;
		}
		return false;
	}

}
