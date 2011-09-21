package draw;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;


public class JCanvas extends JPanel {

	private List drawables = new LinkedList();
	public boolean draw = true;
	
	public void paint(Graphics g) {
		if (!draw)
			return;
		for (Iterator iter = drawables.iterator(); iter.hasNext();) {
			IDrawable d = (IDrawable) iter.next();
			d.draw(g);	
		}
	}

	public void addDrawable(IDrawable d) {
		if (!draw)
			return;
		drawables.add(d);
		repaint();
	}

	public void removeDrawable(IDrawable d) {
		if (!draw)
			return;
		drawables.remove(d);
		repaint();
	}

	public void clear() {
		if (!draw)
			return;
		drawables.clear();
		repaint();
	}
	
}