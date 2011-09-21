package helpers;

import java.util.ArrayList;

import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

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
		System.out.println("isInRect securityDist=" + securityDist);
		float velocity = Algebra.getEuclidDistance(enemy.getVelocity());
		if (velocity == 0)
			return isInCircle(me, enemy, roundInfo);
		
		System.out.println("isInRect enemy position=" + enemy.getPosition());		
		System.out.println("isInRect velocity=" + velocity);
		float securityX = enemy.vy / velocity * securityDist;  
		float securityY = enemy.vx / velocity * securityDist;  
		System.out.println("isInRect security=(" + securityX + ", " + securityY + ")");
		float ax = enemy.x + securityX;
		float ay = enemy.y - securityY;
		System.out.println("isInRect a=(" + ax + ", " + ay + ")");

		float bx = enemy.x - securityX;
		float by = enemy.y + securityY;
		System.out.println("isInRect b=(" + bx + ", " + by + ")");
		
		float mx = enemy.x + enemy.vx + securityY; 
		float my = enemy.y + enemy.vy + securityX;
		System.out.println("isInRect m=(" + mx + ", " + my + ")");
		
		float dx = mx + securityX;
		float dy = my - securityY;
		System.out.println("isInRect d=(" + dx + ", " + dy + ")");
		
		boolean isInRect = isInRect(ax,ay,bx,by,dx,dy,me.x,me.y);
		System.out.println("isInRect " + isInRect);
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
	
	public static float MIN_DANGEROUS_SCORE = 20;
	
	public static boolean isDangerousAttack(Sphere me, Point enemy, PlayingInfo playingInfo, RoundStartInfo roundInfo){
		float myScore = getScore(me, playingInfo);
		boolean isEnemyReachable = isInRect(enemy, me, roundInfo);
		if (myScore<MIN_DANGEROUS_SCORE && !isEnemyReachable)
			return true;
		return false;
	}
	
}
