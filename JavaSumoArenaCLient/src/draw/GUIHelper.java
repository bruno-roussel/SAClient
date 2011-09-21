package draw;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import valueobjects.PlayingInfo;
import valueobjects.Point;
import valueobjects.RoundStartInfo;
import valueobjects.Sphere;


public class GUIHelper {
	
	public static JCanvas jc = new JCanvas();
	static JFrame frame = new JFrame("test");
	public static int SHIFT = 300;
	
	public static void showOnFrame(JComponent component) {
		WindowAdapter wa = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(wa);
		frame.getContentPane().add(component);
		frame.pack();
		frame.setVisible(true);
		frame.setBounds(0, 0, 700, 700);
	}
	
	public static void show() {
		jc.setBackground(Color.WHITE);
		jc.setPreferredSize(new Dimension(400,200));
		Dimension dim  =new Dimension(20,20);
		GUIHelper.showOnFrame(jc);
	}

	public static void drawArena(PlayingInfo info, RoundStartInfo roundInfo){
		jc.clear();
		IDrawable rectD = new RectangleDrawable(Color.GRAY,new Point(0,0), new Dimension(700,700));
		IDrawable circle2 = new CirclePlainDrawable(Color.WHITE,new Point(SHIFT, SHIFT), info.getArenaRadius(), 1F);
		IDrawable circle = new CircleDrawable(Color.RED,new Point(SHIFT, SHIFT), info.getArenaRadius(), 10F);
		jc.addDrawable(rectD);
		jc.addDrawable(circle);
		jc.addDrawable(circle2);
		for (int i=0;i<info.getSpheres().length;i++){
			Sphere sphere =  info.getSpheres()[i];
			Color color = Color.LIGHT_GRAY;
			if (roundInfo.myIndex == i)
				color = Color.RED;
			IDrawable sphereD = new CirclePlainDrawable(color,new Point(SHIFT + sphere.x, SHIFT + sphere.y), roundInfo.sphereRadius, 1F);
			IDrawable velD = new LineDrawable(Color.BLACK,new Point(SHIFT + sphere.x, SHIFT + sphere.y),new Point(SHIFT + sphere.x + sphere.vx, SHIFT + sphere.y + sphere.vy), 3F);
			jc.addDrawable(sphereD);
			jc.addDrawable(velD);
			
		}
	}
}