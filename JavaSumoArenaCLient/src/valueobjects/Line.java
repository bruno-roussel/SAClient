package valueobjects;

public class Line {

	// y = Ax + B
	
	public Point p;
	public Vector v;
	
	public Line(Point p, Vector v){
		this.p = p;
		this.v = v;
	}
	
	public float getA(){
		if (v.dx == 0)
			return Float.MAX_VALUE;
		return v.dy / v.dx;
	}

	public float getB(){
		return p.y - getA() * p.x;
	}
	
}
