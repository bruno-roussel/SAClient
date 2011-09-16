package ai;

import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;

public class CrossAction extends Action {

	private RoundStartInfo roundInfo;
	
	enum State{ GO_CENTER, GO_TARGET}
	
	private State state;
	
	private Point[] targets;
	
	private int currentTarget;
	
	private Point target;
	private Point center = new Point(0,0);

	public CrossAction(RoundStartInfo roundInfo){
		this.roundInfo = roundInfo;
		state = State.GO_CENTER;
		currentTarget = 0;
	}

	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		targets = new Point[4];
		targets[0] = new Point(playingInfo.getArenaRadius(), 0);
		targets[1] = new Point(-playingInfo.getArenaRadius(), 0);
		targets[2] = new Point(0, playingInfo.getArenaRadius());
		targets[3] = new Point(0, - playingInfo.getArenaRadius());
		target = targets[currentTarget];
		if (state == State.GO_CENTER){
			if (isNearTarget(sumo, center)){
				currentTarget = getNextTarget();
				state = State.GO_TARGET;
				return execute(sumo, playingInfo);
			}
			return (new SeekAction(roundInfo, center)).execute(sumo, playingInfo);
		}else{
			if (isNearTarget(sumo, target)){
				state = State.GO_CENTER;
				return execute(sumo, playingInfo);
			}				
			return (new SeekAction(roundInfo, target)).execute(sumo, playingInfo);			
		}
	}

	private int getNextTarget() {
		int current = currentTarget++;
		if (current>targets.length)
			return 0;
		return current;
	}

	private boolean isNearTarget(Sphere sumo, Point target) {
		boolean isSlow = sumo.vx <= 3 && sumo.vy <= 3;
		float dx = sumo.x - target.x;
		float dy = sumo.y - target.y;
		boolean isDx = dx >= -3 && dx <= 3;
		boolean isDy = dy >= -3 && dy <= 3;
		return isSlow && isDx && isDy;
	}

}
