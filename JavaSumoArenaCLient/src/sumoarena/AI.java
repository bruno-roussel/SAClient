package sumoarena;

import helpers.Algebra;

import java.util.Date;

import valueobjects.AccelerationVector;
import valueobjects.Line;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;
import ai.Action;
import ai.ArrivalAction;
import ai.CrossAction;
import ai.CrossActionEnemy;
import ai.SeekAction;

public class AI {

	private int dx;
	private int dy;
	private Action action;
	private RoundStartInfo roundInfo;
	
	public AI(RoundStartInfo roundInfo){
		int maxRandom = 200;
		int x = (int) Math.round(Math.random() * maxRandom - maxRandom/2);
		int y = (int) Math.round(Math.random() * maxRandom - maxRandom/2);
		action = new SeekAction(roundInfo, new Point(50,50));
		action = new CrossAction(roundInfo);
		int enemy = roundInfo.playerCount - roundInfo.myIndex - 1;
		action = new CrossActionEnemy(roundInfo, enemy);
		//action = new ArrivalAction(roundInfo, new Point(x,y));
		this.roundInfo = roundInfo;
	}
		
	public AccelerationVector getAccelerationVector(PlayingInfo playingInfo) {
		Date now = new Date();
		long startTime = now.getTime();
		Sphere sphere = playingInfo.getSpheres()[roundInfo.myIndex];
		AccelerationVector ac = action.execute(sphere, playingInfo);
		if (isTooMuchInerty(roundInfo, sphere, ac, playingInfo)){
			System.out.println("WARNING TOO MUCH INERTY, OUT OF ARENA  => MAX BRAKE!");
			ac = getMaxBrake(roundInfo, sphere);	
		}		
		Sphere nextPosition = getNextPosition(roundInfo, sphere, ac, playingInfo);
		if (!nextPosition.inArena){
			System.out.println("WARNING NEXT POSITION IS OUT OF ARENA  => MAX BRAKE!");
			ac = getMaxBrake(roundInfo, sphere);
		}
		ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
		now = new Date();
		long endTime = now.getTime();
		long duration = endTime - startTime;
		System.out.println("Thinking duration =" + duration + "ms, AccelerationVector = " + ac);
		return ac;
	}


	public static  Sphere getNextPosition(RoundStartInfo roundInfo, Sphere sphere, AccelerationVector ac, PlayingInfo playingInfo){
		Sphere next = sphere.clone();
		next.x = sphere.x + sphere.vx + ac.getdVx();
		next.y = sphere.y + sphere.vy + ac.getdVy();
		next.vx = sphere.vx + ac.getdVx();
		next.vy = sphere.vy + ac.getdVy();
		next.inArena = Algebra.isInArena(next, playingInfo);
		System.out.println("next :" + next.toString());
		return next;
	}
	
	public static  AccelerationVector getMaxBrake(RoundStartInfo roundInfo, Sphere sphere) {
		float v = Algebra.getEuclidDistance(sphere.vx, sphere.vy);
		int dVx = - Algebra.round(sphere.vx * roundInfo.maxSpeedVariation / v);
		int dVy = - Algebra.round(sphere.vy * roundInfo.maxSpeedVariation / v);
		return new AccelerationVector(dVx, dVy);
	}
	
	public static int getInertyRoundsToStop(RoundStartInfo roundInfo, Sphere sphere){
		return (int)Algebra.round(Algebra.getEuclidDistance(sphere.vx, sphere.vy) / roundInfo.maxSpeedVariation);
	}

	public static float getInertyDistToStop(RoundStartInfo roundInfo, Sphere sphere, AccelerationVector ac){
		int vx = sphere.vx + ac.getdVx();
		int vy = sphere.vy + ac.getdVy();
		float currentSpeed = Algebra.getEuclidDistance(vx, vy);
		float dist = 0;
		while(currentSpeed>0){
			dist+=currentSpeed;
			currentSpeed = currentSpeed - roundInfo.maxSpeedVariation;			
		}
		return dist;
	}
	
	public static boolean isTooMuchInerty(RoundStartInfo roundInfo, Sphere sphere, AccelerationVector ac, PlayingInfo playingInfo){
		Point p = new Point(sphere.x,sphere.y);
		Vector v = new Vector(sphere.vx+ ac.getdVx(),sphere.vy + ac.getdVy());
		Line line = new Line(p,v);
		Point[] intersections = Algebra.getIntersections(playingInfo.getArenaRadius(), line);
		Point nextIntersection = Algebra.getNextIntersection(p, v, intersections);
		float inertyDist = getInertyDistToStop(roundInfo, sphere, ac);
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
