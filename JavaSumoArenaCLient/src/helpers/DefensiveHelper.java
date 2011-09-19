package helpers;

import java.util.ArrayList;

import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;

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
	
	public static ArrayList<Sphere> getStrikers(Sphere[] spheres, int myIndex, Point position, RoundStartInfo roundInfo){
		ArrayList<Sphere> strikers = new ArrayList<Sphere>();
		for (int i=0; i<spheres.length;i++){
			if (i==myIndex)
				continue;
			if (isReachable(position, spheres[i], roundInfo))
				strikers.add(spheres[i]);
		}
		return strikers;
	}
	
	
	
}
