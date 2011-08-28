package ai;

import valueobjects.AccelerationVector;
import valueobjects.PlayingInfo;
import valueobjects.Sphere;

public abstract class Action {

	public abstract AccelerationVector execute(Sphere sumo, PlayingInfo playingInfo);
	
}
