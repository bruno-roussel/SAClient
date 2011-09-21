package ai;

import java.util.ArrayList;

import helpers.Algebra;
import helpers.Collision;
import helpers.DefensiveHelper;
import sumoarena.AI;
import valueobjects.AccelerationVector;
import valueobjects.Line;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;

public class MegaAction extends Action {

	private RoundStartInfo roundInfo;
	
	enum State{ GO_CENTER, GO_TARGET}
	
	private State state;
	
	private int enemyIndex;

	private Point target;
	
	private Point center = new Point(0,0);
	
	private int goTargetCpt =0;
	
	private int MAX_GO_TO_TARGET = 100;

	public MegaAction(RoundStartInfo roundInfo, int enemyIndex){
		this.roundInfo = roundInfo;
		this.enemyIndex = enemyIndex;
		state = State.GO_CENTER;
	}

	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		System.out.println("MegaActionEnemy.execute state= " + state.name());
		ArrayList<Sphere> strikers = DefensiveHelper.getStrikers(playingInfo.getSpheres(), roundInfo.myIndex, roundInfo);
		if (strikers!=null && strikers.size()>0){
			System.out.println("DEFENSE");
			Sphere striker = strikers.get(0);
			float myScore = DefensiveHelper.getScore(sumo, playingInfo);
			float stikerScore = DefensiveHelper.getScore(striker, playingInfo);
			Sphere[] nextSpheres = Collision.handleCollisionsNextRound(playingInfo.getSpheres(), roundInfo);
			float myNextScore = DefensiveHelper.getScore(nextSpheres[roundInfo.myIndex], playingInfo);
			float strikerNextScore = DefensiveHelper.getScore(nextSpheres[striker.index], playingInfo);
			if (myNextScore > strikerNextScore){
				// ATTACK
				System.out.println("REPLY ATTACK !!!! myScore : " + myNextScore + " hisScore : " + strikerNextScore);
				return (new SeekAction(roundInfo,striker.getNextPosition() )).execute(sumo, playingInfo);			
			}
			// FLEE	
			System.out.println("FLEE FROM ATTACK.... :(");
			return (new FleeAction(roundInfo,striker.getNextPosition() )).execute(sumo, playingInfo);			
		}
		else{
			System.out.println("ATTACK");
			if (AI.nextVelocity!=null && !DefensiveHelper.compare(AI.nextVelocity, sumo.getVelocity())){
				System.out.println("HIT BY ENEMY => DO NOTHING");				
			}
			
			if (state == State.GO_CENTER){
				if (isNearTarget(sumo, center,15)){
					state = State.GO_TARGET;
					return execute(sumo, playingInfo);
				}
				return (new ArrivalAction(roundInfo, center)).execute(sumo, playingInfo);
			}else{
				target = getNextTarget(sumo, playingInfo);
				goTargetCpt++;
				boolean isDangerousAttack =false;
				if (target!=null)
					isDangerousAttack = DefensiveHelper.isDangerousAttack(sumo, target, playingInfo, roundInfo);
				
				if (isNearTarget(sumo, target,5) || goTargetCpt >= MAX_GO_TO_TARGET || target==null || isDangerousAttack){
					goTargetCpt = 0;
					state = State.GO_CENTER;
					String reason="";
					if (isNearTarget(sumo, target,5))
						reason = "isNearTarget";
					if (goTargetCpt >= MAX_GO_TO_TARGET)
						reason = "maxGoToTarget";
					if (target==null)
						reason = "targetIsNull";
					if (isDangerousAttack)
						reason = "isDangerousAttack";
					
					System.out.println("GO BACK TO CENTER : REASON = " + reason + " " );
					return execute(sumo, playingInfo);
				}		
				return (new ArrivalAction(roundInfo, target)).execute(sumo, playingInfo);			
			}
		}
		
	}

	private Point getNextTarget2(Sphere sumo, PlayingInfo playingInfo) {
		System.out.println("CrossActionEnemy.getNextTarget");
		Sphere enemy = playingInfo.getSpheres()[enemyIndex];
		Point p = new Point(sumo.x,sumo.y);
		Vector direction = new Vector(enemy.x + enemy.vx - sumo.x, enemy.y + enemy.vy - sumo.y);
		System.out.println("CrossActionEnemy.getNextTarget direction " + direction);
		Line line = new Line(sumo.getPosition(), direction);
		Point[] points = Algebra.getIntersections(playingInfo.getArenaRadius(), line);
		System.out.println("CrossActionEnemy.getNextTarget points " + points);
		Point inter = Algebra.getNextIntersection(p, direction, points);
		inter.x = Algebra.round(inter.x);
		inter.y = Algebra.round(inter.y);
		System.out.println("CrossActionEnemy.getNextTarget inter " + inter);
		boolean insideArena = inter.x * inter.x + inter.y * inter.y < playingInfo.getArenaRadius() * playingInfo.getArenaRadius();
		System.out.println("CrossActionEnemy.getNextTarget insideArena " + insideArena);
		if (!insideArena){
			inter.y = (float) Math.sqrt(playingInfo.getArenaRadius() * playingInfo.getArenaRadius() - inter.x * inter.x);
			if (inter.x<0)
				inter.x = inter.x + roundInfo.sphereRadius;
			if (inter.x>0)
				inter.x = inter.x - roundInfo.sphereRadius;
			if (inter.y<0)
				inter.y = inter.y + roundInfo.sphereRadius;
			if (inter.y>0)
				inter.y = inter.y - roundInfo.sphereRadius;
		}
		return inter;
	}

	private Point getNextTarget(Sphere sumo, PlayingInfo playingInfo) {
		System.out.println("CrossActionEnemy.getNextTarget");
		Sphere enemy = playingInfo.getSpheres()[enemyIndex];
		Point target = enemy.getNextPosition();
		boolean insideArena = target.x * target.x + target.y * target.y < playingInfo.getArenaRadius() * playingInfo.getArenaRadius();
		if (!insideArena){
			target = enemy.getPosition();
		}
		insideArena = target.x * target.x + target.y * target.y < playingInfo.getArenaRadius() * playingInfo.getArenaRadius();
		if (!insideArena){
			target = null;
		}
		return target;
	}

	private boolean isNearTarget(Sphere sumo, Point target, int MAX) {
		System.out.println("isNearTarget [BEGIN] sumo " + sumo + " from target " + target);
		boolean isSlow = sumo.vx <= MAX && sumo.vy <= MAX && sumo.vx >= -MAX && sumo.vy >= -MAX;
		float dx = sumo.x - target.x;
		float dy = sumo.y - target.y;
		boolean isDx = dx >= -MAX && dx <= MAX;
		boolean isDy = dy >= -MAX && dy <= MAX;
		boolean res = isSlow && isDx && isDy;
		System.out.println("isNearTarget [END] result=" + res);
		return res;
	}
	

}
