package ai;

import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;

public class TargetAction extends Action {

	public Sphere target;
	private RoundStartInfo roundInfo;

	public TargetAction(RoundStartInfo roundInfo){
		this.roundInfo = roundInfo;
	}
	
	public void setTarget(Sphere target) {
		this.target = target;
	}

	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		int targetX = target.x + target.vx;
		int targetY = target.y + target.vy;
		int sumoX = sumo.x + sumo.vx;
		int sumoY = sumo.y + sumo.vy;
		return Algebra.getNormalizedAccelerationVector(targetX - sumoX, targetY - sumoY, this.roundInfo.maxSpeedVariation);
	}
}
