package draw;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import valueobjects.Point;

public class LineDrawable implements IDrawable{

	Color color;
	Point a,b;
	int radius;
	float stroke;
	
	
	public LineDrawable(Color color, Point a, Point b, float stroke) {
		this.a = a;
		this.b =b;
		this.color = color;		
		this.stroke = stroke;
	}

	public void draw(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		Stroke oldStroke = g2D.getStroke();
	    g2D.setStroke(new BasicStroke(stroke));  // set stroke width of 10
		Color c = g.getColor();
		g.setColor(color);
	    g2D.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
	    g2D.setColor(c);
	    g2D.setStroke(oldStroke);
	}

}