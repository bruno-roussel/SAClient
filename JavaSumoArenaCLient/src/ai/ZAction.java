package ai;

import helpers.Algebra;
import sumoarena.AI;
import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class ZAction extends Action {

	public Vector direction;
	private MaxSpeedAheadAction maxSpeedAheadAction;
	private RandomAction randomAction;
	private RoundStartInfo roundInfo;

	public ZAction(RoundStartInfo roundInfo){
		this.roundInfo = roundInfo;
		randomAction = new RandomAction(roundInfo, 30);
		maxSpeedAheadAction = new MaxSpeedAheadAction(roundInfo);
	}

	public void setDirection(Vector direction){
		this.direction = direction;
	}
	
	public AccelerationVector execute(Sphere sphere, PlayingInfo playingInfo){
		AccelerationVector ac;
		float v = Algebra.getEuclidDistance(sphere.vx, sphere.vy);
		System.out.println("current speed=" + v);
		System.out.println("Inerty Rounds To Stop : " + AI.getInertyRoundsToStop(roundInfo, sphere));
		if (v == 0){
			ac = randomAction.execute(sphere, playingInfo);
			maxSpeedAheadAction.setDirection(new Vector(ac.getdVx(), ac.getdVy()));
			return ac;
		}
		ac = maxSpeedAheadAction.execute(sphere, playingInfo);
		if (AI.isTooMuchInerty(roundInfo, sphere, ac, playingInfo)){
			System.out.println("WARNING TOO MUCH INERTIE WITH THIS ACCELERATION  => MAX BRAKE!");
			ac = AI.getMaxBrake(roundInfo, sphere);
		}
		return ac;
	}
}
