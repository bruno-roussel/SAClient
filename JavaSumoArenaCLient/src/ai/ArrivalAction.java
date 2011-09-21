package ai;

import helpers.Algebra;
import sumoarena.AI;
import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class ArrivalAction extends Action {

	private RoundStartInfo roundInfo;
	private Point target;
	
	public ArrivalAction(RoundStartInfo roundInfo, Point target){
		this.roundInfo = roundInfo;
		this.target = target;
	}
	
	@Override
	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		if (sumo.getPosition().equals(target) && sumo.getVelocity().dx == 0 && sumo.getVelocity().dy == 0){
			System.out.println("ArrivalAction : YEAH, target " + target + " reached !!");
			return new AccelerationVector(0,0);
		}					
		System.out.println("ArrivalAction : position " + sumo.getPosition());
		System.out.println("ArrivalAction : target " + target);
		int distanceToTarget = (int)Algebra.getEuclidDistance(target.x - sumo.x, target.y - sumo.y);
		System.out.println("ArrivalAction : distance to target " + distanceToTarget);

		AccelerationVector ac = (new SeekAction(roundInfo, target)).execute(sumo, playingInfo);
		ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
		System.out.println("ArrivalAction : seekAction ac " + ac);
		
		Point nextPos = new Point(sumo.x + sumo.vx + ac.getdVx(), sumo.y + sumo.vy + ac.getdVy());
		//Vector nextSpeed = new Vector(sumo.vx + ac.getdVx(),sumo.vy+ ac.getdVy());
		int distance = (int)Algebra.getEuclidDistance(target.x - nextPos.x, target.y - nextPos.y);
		System.out.println("ArrivalAction : distance to target next round " + distance);
		//System.out.println("ArrivalAction : speed next round  " + Algebra.getEuclidDistance(nextSpeed));
		//int maxSpeed = getMaxSpeed(distance,roundInfo);
		float minDistanceToStop = AI.getInertyDistToStop(roundInfo, sumo, ac);			
		System.out.println("ArrivalAction : minDistanceToStop " + minDistanceToStop);
		if (distance<minDistanceToStop){
			System.out.println("ArrivalAction : WARNING OVER MAX SPEED ");
			Point nextPos2 = new Point(sumo.x + sumo.vx, sumo.y + sumo.vy);
			Vector nextSpeed2 = sumo.getVelocity();

			int maxSpeed = getMaxSpeed(distance,roundInfo);
			float deceleration = maxSpeed - Algebra.getEuclidDistance(sumo.getVelocity());

			Vector normalizedAc = Algebra.normalize(new Vector(ac.getdVx(), ac.getdVy()));
			Vector decelarationAc =Algebra.multiply(normalizedAc, deceleration);			
			ac = new AccelerationVector(decelarationAc);
			ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
			
		}else{
			System.out.println("ArrivalAction : SEEK TARGET ");
		}
		return ac;
	}

	public boolean isTooFast(Sphere sumo, PlayingInfo playingInfo){
		Vector desiredVelocity = new Vector(target.x - sumo.x, target.y - sumo.y);
		if (Algebra.getEuclidDistance(desiredVelocity)>Algebra.getEuclidDistance(sumo.getVelocity()))
			return true;
		return false;
	}
	

	public int getMaxSpeed(int distance, RoundStartInfo roundInfo){
		int maxSpeed = 0;
		int computedDistance = 0;
		int i =0;
		while (computedDistance<= distance){
			i++;
			maxSpeed = roundInfo.maxSpeedVariation * i;
			computedDistance = computedDistance + maxSpeed;
		}
		return maxSpeed;
	}
	


	
	
}
