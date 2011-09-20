package ai;

import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class SeekAction extends Action {

	private RoundStartInfo roundInfo;
	private Point target;
	
	public SeekAction(RoundStartInfo roundInfo, Point target){
		this.roundInfo = roundInfo;
		this.target = target;
	}
	
	@Override
	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		if (sumo.getPosition().equals(target) && sumo.getVelocity().dx == 0 && sumo.getVelocity().dy == 0){
			System.out.println("SeekAction : YEAH, target " + target + " reached !!");
			return new AccelerationVector(0,0);
		}		
		//System.out.println("SeekAction : position " + sumo.getPosition());
		System.out.println("SeekAction : velocity " + sumo.getVelocity());
		Vector desiredVelocity = new Vector(target.x - sumo.x, target.y - sumo.y);
		System.out.println("SeekAction : desiredVelocity " + desiredVelocity);
		Vector steering =Algebra.sub(desiredVelocity, sumo.getVelocity());
		System.out.println("SeekAction : steering " + steering);
		return new AccelerationVector(steering);
	}
}
