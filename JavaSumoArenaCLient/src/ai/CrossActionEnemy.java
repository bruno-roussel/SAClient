package ai;

import helpers.Algebra;
import valueobjects.AccelerationVector;
import valueobjects.Line;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class CrossActionEnemy extends Action {

	private RoundStartInfo roundInfo;
	
	enum State{ GO_CENTER, GO_TARGET}
	
	private State state;
	
	private int enemyIndex;

	private Point target;
	
	private Point center = new Point(0,0);

	public CrossActionEnemy(RoundStartInfo roundInfo, int enemyIndex){
		this.roundInfo = roundInfo;
		this.enemyIndex = enemyIndex;
		state = State.GO_CENTER;
	}

	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		System.out.println("CrossActionEnemy.execute state= " + state.name());
		if (state == State.GO_CENTER){
			if (isNearTarget(sumo, center)){
				target = getNextTarget(playingInfo);
				state = State.GO_TARGET;
				return execute(sumo, playingInfo);
			}
			return (new ArrivalAction(roundInfo, center)).execute(sumo, playingInfo);
		}else{
			if (isNearTarget(sumo, target)){
				state = State.GO_CENTER;
				return execute(sumo, playingInfo);
			}				
			return (new ArrivalAction(roundInfo, target)).execute(sumo, playingInfo);			
		}
	}

	private Point getNextTarget(PlayingInfo playingInfo) {
		System.out.println("CrossActionEnemy.getNextTarget");
		Sphere enemy = playingInfo.getSpheres()[enemyIndex];
		Vector direction = new Vector(enemy.x + enemy.vx, enemy.y + enemy.vy);
		System.out.println("CrossActionEnemy.getNextTarget direction " + direction);
		Line line = new Line(center, direction);
		Point[] points = Algebra.getIntersections(playingInfo.getArenaRadius(), line);
		System.out.println("CrossActionEnemy.getNextTarget points " + points);
		Point inter = Algebra.getNextIntersection(center, direction, points);
		System.out.println("CrossActionEnemy.getNextTarget inter " + inter);
		return inter;
	}

	private boolean isNearTarget(Sphere sumo, Point target) {
		boolean isSlow = sumo.vx <= 5 && sumo.vy <= 5;
		float dx = sumo.x - target.x;
		float dy = sumo.y - target.y;
		boolean isDx = dx >= -5 && dx <= 5;
		boolean isDy = dy >= -5 && dy <= 5;
		return isSlow && isDx && isDy;
	}

}
