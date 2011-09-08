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
		System.out.println("SeekAction position " + sumo.getPosition());
		System.out.println("SeekAction target " + target);
		Vector desiredVelocity = new Vector(target.x - sumo.x, target.y - sumo.y);
		System.out.println("SeekAction desiredVelocity " + desiredVelocity);
		desiredVelocity = Algebra.normalize(desiredVelocity);
		Vector velocity = Algebra.normalize(sumo.getVelocity());
		Vector steering = Algebra.sub(desiredVelocity, velocity);
		System.out.println("SeekAction desiredVelocity " + desiredVelocity);
		System.out.println("SeekAction velocity " + velocity);
		System.out.println("SeekAction steering " + steering);
		return new AccelerationVector(Algebra.multiply(steering, roundInfo.maxSpeedVariation));
	}
}
