package draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import valueobjects.Point;

public class CirclePlainDrawable implements IDrawable {

	Color color;
	Point a;
	int radius;
	float stroke;
	
	public CirclePlainDrawable(Color color, Point center, int radius, float stroke) {
		a = new Point(center.x - radius, center.y - radius);
		this.radius = radius;
		this.color = color;
		this.stroke = stroke;
	}

	public void draw(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		Stroke oldStroke = g2D.getStroke();
	    g2D.setStroke(new BasicStroke(stroke));  // set stroke width of 10
		Color c = g.getColor();
		g.setColor(color);
		g.fillOval((int)a.x,(int)a.y,radius*2,radius*2);
		g.setColor(c);
		g2D.setStroke(oldStroke);
	}

}
