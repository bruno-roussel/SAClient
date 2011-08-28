package ai;

import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;

public class RandomAction extends Action {

	private int maxRandom;
	private RoundStartInfo roundInfo;
	
	public RandomAction(RoundStartInfo roundInfo, int maxRandom){
		this.roundInfo = roundInfo;
		this.maxRandom = maxRandom;
	}
	
	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		int dVx = (int) Math.round(Math.random() * maxRandom - maxRandom/2);
		int dVy = (int) Math.round(Math.random() * maxRandom - maxRandom/2);
		return Algebra.getNormalizedAccelerationVector(dVx, dVy, roundInfo.maxSpeedVariation);
	}
}
