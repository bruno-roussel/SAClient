package ai;

import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.Vector;

public class MaxSpeedAhead {

	Vector direction;
	int maxSpeedVariation;

	public MaxSpeedAhead(int maxSpeedVariation, Vector direction){
		this.direction = direction;
		this.maxSpeedVariation = maxSpeedVariation;
	}

	public void setDirection(Vector direction){
		this.direction = direction;
	}
	
	public AccelerationVector execute(){
		float v = Algebra.getEuclidDistance(direction.dx, direction.dy);
		int dVx = Math.round(direction.dx * this.maxSpeedVariation / v);
		int dVy = Math.round(direction.dy * this.maxSpeedVariation / v);
		return new AccelerationVector(dVx, dVy);
	}
}
