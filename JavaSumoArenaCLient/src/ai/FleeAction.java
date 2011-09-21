package ai;

import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.Line;
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
		int minDist = 50;
		Vector dir = new Vector(target.x-sumo.x, target.y-sumo.y);
		Line line = new Line(target, dir);
		Point[] inters = Algebra.getIntersections(playingInfo.getArenaRadius(), line);
		Point inter = Algebra.getNextIntersection(sumo.getPosition(), dir, inters);
		Vector dist = new Vector(inter.x - target.x, inter.y - target.y);
		float distance = Algebra.getEuclidDistance(dist);
		if (distance <minDist){
			System.out.println("Flee from outside and from striker");
		}
		else{
			System.out.println("Flee from striker");
				Vector desiredVelocity = new Vector(sumo.x - target.x, sumo.y - target.y);
				Vector steering =Algebra.sub(desiredVelocity, sumo.getVelocity());
				//return new AccelerationVector(steering);
		}
		Vector steering = new Vector(-dist.dy,dist.dx);			
		return new AccelerationVector(steering);

	}
}
