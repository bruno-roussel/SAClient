package valueobjects;

import java.util.Map;

/**
 * A Value Object describing a player status
 * @author peal6230
 */
public class Sphere 
{

	public Sphere()
	{}
	
	public Sphere(Map<String, Object> sphereAsJson)
	{
		index = ((Number) sphereAsJson.get("index")).intValue();
		x = ((Number) sphereAsJson.get("x")).intValue();
		y = ((Number) sphereAsJson.get("y")).intValue();
		vx = ((Number) sphereAsJson.get("vx")).intValue();
		vy = ((Number) sphereAsJson.get("vy")).intValue();
		inArena = (Boolean) sphereAsJson.get("inArena");
	}
	
	/**
	 * The index of this player, during the current game
	 */
	public int index;
	
	/**
	 * The horizontal coordinate of the sphere center
	 */
	public int x;
	
	/**
	 * The vertical coordinate of the sphere center
	 */
	public int y;
	
	/**
	 * The horizontal component of the velocity vector of this sphere
	 */
	public int vx;
	
	/**
	 * The vertical component of the velocity vector of this sphere
	 */
	public int vy;
	
	/**
	 * True when the sphere is inside the arena, false otherwise
	 */
	public Boolean inArena;

	@Override
	public String toString() {
		return "Sphere [index=" + index + ", x=" + x + ", y=" + y + ", vx=" + vx + ", vy=" + vy + ", inArena="
				+ inArena + "]";
	}
	
	@Override
	public Sphere clone(){
		Sphere clone = new Sphere();
		clone.index = index;
		clone.x = x;
		clone.x = y;
		clone.vx = vx;
		clone.vy = vy;
		clone.inArena = inArena;	
		return clone;
	}
	
	public Point getPosition(){
		return new Point(x,y);		
	}
	
	public Vector getVelocity(){
		return new Vector(vx, vy);
	}
	
}
