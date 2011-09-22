package helpers;

import java.awt.Color;
import java.util.ArrayList;

import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;
import draw.GUIHelper;
import draw.IDrawable;
import draw.LineDrawable;

public class DefensiveHelper {

	public static boolean isReachable(Point position, Sphere sphere, RoundStartInfo roundInfo){
		Point centerAttack = new Point(sphere.x + sphere.vx,sphere.y + sphere.vy);
		float rPosition = (position.x - centerAttack.x) * (position.x - centerAttack.x) + (position.y - centerAttack.y) * (position.y - centerAttack.y);
		float rMax = roundInfo.maxSpeedVariation * roundInfo.maxSpeedVariation;
		if (rPosition <= rMax)
			return true;
		else
			return false;
	}
	
	public static ArrayList<Sphere> getStrikers(Sphere[] spheres, int myIndex, RoundStartInfo roundInfo){
		//System.out.println("getStrikers [BEGIN]");
		Sphere me = spheres[myIndex];
		ArrayList<Sphere> strikers = new ArrayList<Sphere>();
		for (int i=0; i<spheres.length;i++){
			if (i==myIndex)
				continue;
			//System.out.println("getStrikers [" + i + "]");
			if (isInRect(me, spheres[i], roundInfo))
				strikers.add(spheres[i]);
		}
		//System.out.println("getStrikers [END]");
		return strikers;
	}
	

	public static boolean isInRect(float ax, float ay, float bx, float by, float dx, float dy, float x, float y){
		//System.out.println("isInRect (" + x + "," + y + ") is in (" + ax + ", " + ay + ") (" + bx + ", " + by + ") (" + dx + ", " + dy + ").");
		float bax=bx-ax;
		float bay=by-ay;
		float dax=dx-ax;
		float day=dy-ay;
	
		if ((x-ax)*bax+(y-ay)*bay<0.0) return false;
		if ((x-bx)*bax+(y-by)*bay>0.0) return false;
		if ((x-ax)*dax+(y-ay)*day<0.0) return false;
		if ((x-dx)*dax+(y-dy)*day>0.0) return false;
	
		return true;
	}
	
	public static boolean isInRect(Sphere me, Sphere enemy, RoundStartInfo roundInfo){
		return isInRect(me.getPosition(), enemy, roundInfo);
	}

	public static boolean isInRect(Point me, Sphere enemy, RoundStartInfo roundInfo){
		int securityDist = roundInfo.maxSpeedVariation + roundInfo.sphereRadius;
		//System.out.println("isInRect securityDist=" + securityDist);
		float velocity = Algebra.getEuclidDistance(enemy.getVelocity());
		if (velocity == 0)
			return isInCircle(me, enemy, roundInfo);
		
		//System.out.println("isInRect enemy position=" + enemy.getPosition());		
		//System.out.println("isInRect velocity=" + velocity);
		float securityX = enemy.vy / velocity * securityDist;  
		float securityY = enemy.vx / velocity * securityDist;  
		//System.out.println("isInRect security=(" + securityX + ", " + securityY + ")");
		float ax = enemy.x + securityX;
		float ay = enemy.y - securityY;
		//System.out.println("isInRect a=(" + ax + ", " + ay + ")");

		float bx = enemy.x - securityX;
		float by = enemy.y + securityY;
		//System.out.println("isInRect b=(" + bx + ", " + by + ")");
		
		float mx = enemy.x + enemy.vx + securityY; 
		float my = enemy.y + enemy.vy + securityX;
		//System.out.println("isInRect m=(" + mx + ", " + my + ")");
		
		float cx = mx - securityX;
		float cy = my + securityY;
		
		float dx = mx + securityX;
		float dy = my - securityY;
		//System.out.println("isInRect d=(" + dx + ", " + dy + ")");
		
		IDrawable line1 = new LineDrawable(Color.BLACK,new Point(GUIHelper.SHIFT + ax, GUIHelper.SHIFT + ay),new Point(GUIHelper.SHIFT + bx, GUIHelper.SHIFT + by), 1F);
		IDrawable line2 = new LineDrawable(Color.BLACK,new Point(GUIHelper.SHIFT + bx, GUIHelper.SHIFT + by),new Point(GUIHelper.SHIFT + cx, GUIHelper.SHIFT + cy), 1F);
		IDrawable line3 = new LineDrawable(Color.BLACK,new Point(GUIHelper.SHIFT + cx, GUIHelper.SHIFT + cy),new Point(GUIHelper.SHIFT + dx, GUIHelper.SHIFT + dy), 1F);
		IDrawable line4 = new LineDrawable(Color.BLACK,new Point(GUIHelper.SHIFT + dx, GUIHelper.SHIFT + dy),new Point(GUIHelper.SHIFT + ax, GUIHelper.SHIFT + ay), 1F);
		GUIHelper.jc.addDrawable(line1);
		GUIHelper.jc.addDrawable(line2);
		GUIHelper.jc.addDrawable(line3);
		GUIHelper.jc.addDrawable(line4);

		
		boolean isInRect = isInRect(ax,ay,bx,by,dx,dy,me.x,me.y);
		//System.out.println("isInRect " + isInRect);
		return isInRect;
	}

	private static boolean isInCircle(Point me, Sphere enemy,
			RoundStartInfo roundInfo) {
		float radius = roundInfo.maxSpeedVariation + roundInfo.sphereRadius;
		float x = me.x - enemy.x;
		float y = me.y - enemy.y;
		boolean isInCircle = x * x + y * y < radius * radius;
		System.out.println("isInCircle " + isInCircle);
		return isInCircle;
	}
	
	public static boolean compare(Vector a, Vector b){
		int MAX = 2;
		float dx = a.dx - b.dx;
		float dy = a.dy - b.dy;
		boolean isDx = dx >= -MAX && dx <= MAX;
		boolean isDy = dy >= -MAX && dy <= MAX;
		boolean res = isDx && isDy;
		return res;	
	}
	
	public static float getScore(Sphere sphere, PlayingInfo playingInfo){
		return playingInfo.getArenaRadius() - Algebra.getEuclidDistance(sphere.x, sphere.y);	
	}
	
	public static boolean isDangerousAttack(Sphere me, Point enemy, PlayingInfo playingInfo, RoundStartInfo roundInfo){
		float myScore = getScore(me, playingInfo);
		boolean isEnemyReachable = isInRect(enemy, me, roundInfo);
		if (myScore<Constants.MIN_DANGEROUS_SCORE && !isEnemyReachable)
			return true;
		return false;
	}
	

	
	public static Sphere getTarget(Sphere[] spheres, int myIndex, PlayingInfo playingInfo, RoundStartInfo roundInfo){
		Sphere me = spheres[myIndex];
		Sphere target = null;
		float bestScore =0;
		for (int i=0; i<spheres.length;i++){
			if (i==myIndex)
				continue;
			if (!spheres[i].inArena)
				continue;
			float currentScore = getAttackScore(me, spheres[i],playingInfo, roundInfo);
			if (currentScore >= bestScore){
				bestScore = currentScore;
				target = spheres[i];
			}
		}
		if (bestScore>=Constants.MIN_ATTACK_SCORE)
				return target;
		return null;

	}
	
	
	public static float getAttackScore(Sphere me, Sphere sphere, PlayingInfo playingInfo, RoundStartInfo roundInfo){
		float a = 0;
		if (playingInfo.getArenaRadius()!=0)
		a = Constants.WEIGHT_ARENA * Algebra.getEuclidDistance(sphere.x, sphere.y) / playingInfo.getArenaRadius();
		float v = Algebra.getEuclidDistance(sphere.vx, sphere.vy);
		float b = 0;
		if (v!=0)
			b= Constants.WEIGHT_SPEED / v;
		float c = Constants.WEIGHT_ANGLE * Algebra.dot(Algebra.normalize(sphere.getVelocity()), Algebra.normalize(new Vector(sphere.x, sphere.y)));
		float d = Constants.WEIGHT_DISTANCE / Algebra.getEuclidDistance(sphere.x - me.x, sphere.y - me.y);
		float score = a + b + c + d;		
		System.out.println("Attack Score spheres[" + sphere.index + "]=" + + score);
		return score;		
	}

	public static Sphere getTargetAnyway(Sphere[] spheres, int myIndex,
			PlayingInfo playingInfo, RoundStartInfo roundInfo) {
		Sphere me = spheres[myIndex];
		Sphere target = null;
		float bestScore =0;
		for (int i=0; i<spheres.length;i++){
			if (i==myIndex)
				continue;
			if (!spheres[i].inArena)
				continue;
			float currentScore = getAttackScore(me, spheres[i],playingInfo, roundInfo);
			if (currentScore > bestScore){
				bestScore = currentScore;
				target = spheres[i];
			}
		}
		return target;

	}
	
	
	public static Point getColdPoint(Sphere[] spheres, int myIndex,
			PlayingInfo playingInfo, RoundStartInfo roundInfo) {
		Sphere me = spheres[myIndex];
		
		int radius = playingInfo.getArenaRadius();
		int risk=20;
		if (spheres.length<Constants.EXTREMIST_RISK.length)
			risk = Constants.EXTREMIST_RISK[spheres.length-1];
		radius = radius * risk / 100;
		int step = 5;
		int bestX=0;
		int bestY=0;
		int maxDist=0;
		for (int x=-radius; x<radius; x +=step){
			int y1= -(int)Math.sqrt(radius*radius - x*x);
			int y2= (int)Math.sqrt(radius*radius - x*x);
			int currentDist1 = 0;
			int currentDist2 = 0;
			for (int i=0; i<spheres.length;i++){
				Sphere sphere = spheres[i];		
				Vector v1 = new Vector(x - sphere.x, y1 - sphere.y);
				Vector v2 = new Vector(x - sphere.x, y2 - sphere.y);
				if (i==myIndex){
				currentDist1 -= Algebra.getEuclidDistance(v1) * Constants.WEIGHT_MY_DISTANCE_COLD_POINT / 100;
				currentDist2 -= Algebra.getEuclidDistance(v2) * Constants.WEIGHT_MY_DISTANCE_COLD_POINT / 100;
				}else{
				currentDist1 += Algebra.getEuclidDistance(v1);
				currentDist2 += Algebra.getEuclidDistance(v2);
				}
			}
			if (currentDist1>maxDist){
				maxDist = currentDist1;
				bestX = x;
				bestY = y1;
			}
			if (currentDist2>maxDist){
				maxDist = currentDist2;
				bestX = x;
				bestY = y2;
			}
		}
		Point best = new Point(bestX, bestY);
		System.out.println("best=" + best + " risk=" + risk + " radius=" + radius);
		return best;
	
	}
	
}
