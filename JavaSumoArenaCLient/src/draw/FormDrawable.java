package draw;
import java.awt.*;

public abstract class FormDrawable implements IDrawable {

	
	protected Rectangle rect ;
	protected Color color;
	
	public FormDrawable(Color color, valueobjects.Point p, Dimension dim){
		this.color=color;
		this.rect = new Rectangle(new java.awt.Point((int)p.x,(int) p.y),dim);
	}
	
	public abstract void draw(Graphics g) ;
	
	
}