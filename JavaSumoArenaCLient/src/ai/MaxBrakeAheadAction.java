package ai;

import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class MaxBrakeAheadAction extends Action {

	public Vector direction;
	private RoundStartInfo roundInfo;

	public MaxBrakeAheadAction(RoundStartInfo roundInfo, Vector direction){
		this.direction = direction;
		this.roundInfo = roundInfo;
	}

	public void setDirection(Vector direction){
		this.direction = direction;
	}
	
	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		return Algebra.getNormalizedAccelerationVector(- direction.dx, - direction.dy, roundInfo.maxSpeedVariation);
	}
}
