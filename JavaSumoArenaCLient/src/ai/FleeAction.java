package ai;

import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class FleeAction extends Action {

	private RoundStartInfo roundInfo;
	private Point target;
	
	public FleeAction(RoundStartInfo roundInfo, Point target){
		this.roundInfo = roundInfo;
		this.target = target;
	}
	
	@Override
	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		Vector desiredVelocity = new Vector(sumo.x - target.x, sumo.y - target.y);
		Vector steering =Algebra.sub(desiredVelocity, sumo.getVelocity());
		return new AccelerationVector(steering);
	}
}
