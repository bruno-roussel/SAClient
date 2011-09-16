package valueobjects;

public class Point {

	public float x;
	public float y;
	
	public Point(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public String toString(){
		return "[" + x + ", " + y + "]";
	}
	
	public boolean equals(Point p){
		return x==p.x && y==p.y;
	}

	
}
