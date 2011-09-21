package sumoarena;

import helpers.Algebra;

import java.awt.Color;
import java.util.Date;

import valueobjects.AccelerationVector;
import valueobjects.Line;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;
import ai.MegaAction;
import ai.SeekAction;
import draw.GUIHelper;
import draw.IDrawable;
import draw.LineDrawable;

public class AI {

	private int dx;
	private int dy;
	private MegaAction action;
	private RoundStartInfo roundInfo;
	public static Vector nextVelocity;
	
	public AI(RoundStartInfo roundInfo){
		int maxRandom = 200;
		int x = (int) Math.round(Math.random() * maxRandom - maxRandom/2);
		int y = (int) Math.round(Math.random() * maxRandom - maxRandom/2);
		//action = new SeekAction(roundInfo, new Point(50,50));
		//action = new CrossAction(roundInfo);
		//int enemy = roundInfo.playerCount - roundInfo.myIndex - 1;
		action = new MegaAction(roundInfo);
//		action = new CrossAction(roundInfo);
//		action = new ArrivalAction(roundInfo, new Point(x,y));
		this.roundInfo = roundInfo;
	}
		
	public AccelerationVector getAccelerationVector(PlayingInfo playingInfo) {
		GUIHelper.drawArena(playingInfo, roundInfo);
		Date now = new Date();
		long startTime = now.getTime();
		Sphere sphere = playingInfo.getSpheres()[roundInfo.myIndex];
		System.out.println("current position= " + sphere.getPosition() + ", current velocitty= " + sphere.getVelocity());
		AccelerationVector ac =action.execute(sphere, playingInfo);
		ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
		
		boolean dontBreak = false;
		Sphere nextPosition = getNextPosition(roundInfo, sphere, ac, playingInfo);
		if (isTooMuchInerty(roundInfo, sphere, ac, playingInfo) || !nextPosition.inArena){
			if (action.target!=null){
				Vector toTarget = new Vector(action.target.x - sphere.x, action.target.y - sphere.y);
				Vector normToTarget = Algebra.normalize(toTarget);
				Vector v = Algebra.normalize(sphere.getVelocity());
				float dot = Algebra.dot(normToTarget, v);
				float dist = Algebra.getEuclidDistance(toTarget);
				if (dot > 0.7 && dist < 30)
					dontBreak = true;
			}
			
			if (dontBreak){
				System.out.println("NEVER BRAKE !!! ATTACK TO DEATH !!!!");			
			}
			else{
				System.out.println("WARNING TOO MUCH INERTY, OUT OF ARENA  => MAX BRAKE!");
				ac = (new SeekAction(roundInfo,new Point(0,0) )).execute(sphere, playingInfo);
			}
		}	
		ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
		IDrawable acD = new LineDrawable(Color.BLUE,new Point(GUIHelper.SHIFT + sphere.x + sphere.vx, GUIHelper.SHIFT + sphere.y + sphere.vy),new Point(GUIHelper.SHIFT + sphere.x + sphere.vx + ac.getdVx(), GUIHelper.SHIFT + sphere.y + sphere.vy + ac.getdVy()), 5F);
		GUIHelper.jc.addDrawable(acD);
		IDrawable vD = new LineDrawable(Color.BLACK,new Point(GUIHelper.SHIFT + sphere.x , GUIHelper.SHIFT + sphere.y ),new Point(GUIHelper.SHIFT + sphere.x + sphere.vx , GUIHelper.SHIFT + sphere.y + sphere.vy), 1F);
		GUIHelper.jc.addDrawable(vD);
		now = new Date();
		long endTime = now.getTime();
		long duration = endTime - startTime;
		Vector nextPos = new Vector(sphere.x + sphere.vx + ac.getdVx(), sphere.y + sphere.vy + ac.getdVy());		
		nextVelocity = new Vector(sphere.vx + ac.getdVx(), sphere.vy + ac.getdVy());		
		System.out.println("Thinking duration =" + duration + "ms, AccelerationVector = " + ac + ", Next position should be = " + nextPos + ", Next Velocitty should be = " + nextVelocity);
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
