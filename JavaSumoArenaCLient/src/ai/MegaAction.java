package ai;

import helpers.Algebra;
import helpers.Collision;
import helpers.Constants;
import helpers.DefensiveHelper;

import java.awt.Color;
import java.util.ArrayList;

import sumoarena.AI;
import valueobjects.AccelerationVector;
import valueobjects.Line;
import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;
import valueobjects.Vector;
import draw.CirclePlainDrawable;
import draw.GUIHelper;
import draw.IDrawable;

public class MegaAction extends Action {

	private RoundStartInfo roundInfo;
	
	enum State{ GO_CENTER, GO_TARGET}
	
	private State state;
	
	private int enemyIndex;

	public Point target;
	
	private Point center = new Point(0,0);
	
	private int goTargetCpt =0;
	


	public MegaAction(RoundStartInfo roundInfo){
		this.roundInfo = roundInfo;
		state = State.GO_CENTER;
	}

	public AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo){
		ArrayList<Sphere> strikers = DefensiveHelper.getStrikers(playingInfo.getSpheres(), roundInfo.myIndex, roundInfo);
		if (strikers!=null && strikers.size()>0){
			Sphere striker = strikers.get(0);
			float myScore = DefensiveHelper.getScore(sumo, playingInfo);
			float stikerScore = DefensiveHelper.getScore(striker, playingInfo);
			Sphere[] nextSpheres = Collision.handleCollisionsNextRound(playingInfo.getSpheres(), roundInfo);
			float myNextScore = DefensiveHelper.getScore(nextSpheres[roundInfo.myIndex], playingInfo);
			float strikerNextScore = DefensiveHelper.getScore(nextSpheres[striker.index], playingInfo);
			if (myNextScore >= strikerNextScore || isNearTarget(striker, new Point(0,0),15) || Algebra.getEuclidDistance(striker.getVelocity())<5){
				// ATTACK
				 //myNextScore > myScore 
				AccelerationVector ac = (new ShotAction(roundInfo,striker.getPosition())).execute(sumo, playingInfo);			
				ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
				System.out.println("UNDER ATTACK sphere " + striker.index + " => REPLY BY ATTACKING !!!! myScore=" + myScore + " myNextScore=" + myNextScore + " hisNextScore=" + strikerNextScore + " ac=" + ac);
				return ac;			
			}
			// FLEE	
			AccelerationVector ac = (new SeekAction(roundInfo,new Point(0,0) )).execute(sumo, playingInfo);			
			ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
			System.out.println("UNDER ATTACK => FLEE FROM ATTACK.... :( myScore : " + myNextScore + " hisScore : " + strikerNextScore + " ac=" + ac);
			return ac;			
			//return (new FleeAction(roundInfo,striker.getNextPosition() )).execute(sumo, playingInfo);			
		}
		else{
			if (AI.nextVelocity!=null && !DefensiveHelper.compare(AI.nextVelocity, sumo.getVelocity())){
				System.out.println("HIT BY ENEMY => DO NOTHING");				
			}
			
			if (state == State.GO_CENTER){
				center = DefensiveHelper.getColdPoint(playingInfo.getSpheres(), roundInfo.myIndex, playingInfo, roundInfo);
				goTargetCpt++;
				if (isNearTarget(sumo, center,Constants.DIST_FROM_TARGET) || goTargetCpt >= Constants.MAX_GO_TO_CENTER){
					goTargetCpt = 0;
					state = State.GO_TARGET;
					return execute(sumo, playingInfo);
				}
				IDrawable targetD = new CirclePlainDrawable(Color.GREEN,new Point(GUIHelper.SHIFT +center.x, GUIHelper.SHIFT + center.y), 8, 1F);
				GUIHelper.jc.addDrawable(targetD);
				AccelerationVector ac = (new ArrivalAction(roundInfo, center)).execute(sumo, playingInfo);
				ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
				System.out.println("GO TO COLD POINT " + center + " numberAttempt=" + goTargetCpt + " ac=" + ac);								
				return ac;			
			}else{
				Sphere targetAsSphere = DefensiveHelper.getTarget(playingInfo.getSpheres(), roundInfo.myIndex, playingInfo, roundInfo);
				if (targetAsSphere==null && isNearTarget(sumo, center,15))
					targetAsSphere = DefensiveHelper.getTargetAnyway(playingInfo.getSpheres(), roundInfo.myIndex, playingInfo, roundInfo);
				if (targetAsSphere != null){{
					target = targetAsSphere.getNextPosition();
					Sphere nextPosition = AI.getNextPosition(roundInfo, targetAsSphere, new AccelerationVector(0,0), playingInfo);
					if (!nextPosition.inArena){
						target = targetAsSphere.getPosition();
					}
				}
				}else{
					target = null;
				}
				if (target!=null){
					IDrawable targetD = new CirclePlainDrawable(Color.GREEN,new Point(GUIHelper.SHIFT +target.x, GUIHelper.SHIFT + target.y), 8, 1F);
					GUIHelper.jc.addDrawable(targetD);				
				}
				
				goTargetCpt++;
				boolean isDangerousAttack =false;
				//if (target!=null)
					//isDangerousAttack = DefensiveHelper.isDangerousAttack(sumo, target, playingInfo, roundInfo);
				
				if (goTargetCpt >= Constants.MAX_GO_TO_TARGET || target==null || isDangerousAttack || isNearTarget(sumo, target,5)){
					goTargetCpt = 0;
					state = State.GO_CENTER;
					String reason="";
					if (target==null)
						reason = "targetIsNull";
					else if (isNearTarget(sumo, target,5))
						reason = "isNearTarget";
					else if (goTargetCpt >= Constants.MAX_GO_TO_TARGET)
						reason = "maxGoToTarget";
					else if (isDangerousAttack)
						reason = "isDangerousAttack";
					
					System.out.println("GO BACK TO COLD POINT : REASON = " + reason + " " );
					if (isNearTarget(sumo, center,Constants.DIST_FROM_COLD_POINT))
						return new AccelerationVector(0, 0);
					else
					return execute(sumo, playingInfo);
				}		
				AccelerationVector ac = (new ShotAction(roundInfo, target)).execute(sumo, playingInfo);	
				ac = Algebra.makeSureMaxSpeedVariation(roundInfo, ac);
				System.out.println("GO TO TARGET " + target + " numberAttempt=" + goTargetCpt  + " ac=" + ac);
				return ac;			
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

	private Point getNextTarget3(Sphere sumo, PlayingInfo playingInfo) {
		//System.out.println("CrossActionEnemy.getNextTarget");
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
		//System.out.println("isNearTarget [BEGIN] sumo " + sumo + " from target " + target);
		boolean isSlow = sumo.vx <= MAX && sumo.vy <= MAX && sumo.vx >= -MAX && sumo.vy >= -MAX;
		float dx = sumo.x - target.x;
		float dy = sumo.y - target.y;
		boolean isDx = dx >= -MAX && dx <= MAX;
		boolean isDy = dy >= -MAX && dy <= MAX;
		boolean res = isSlow && isDx && isDy;
		//System.out.println("isNearTarget [END] result=" + res);
		return res;
	}
	

}
