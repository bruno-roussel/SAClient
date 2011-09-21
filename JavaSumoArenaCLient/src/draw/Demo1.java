package draw;

import java.awt.Color;
import java.awt.Dimension;

public class Demo1 {
	
	public static void main(String[] args) {
		JCanvas jc = new JCanvas();
		jc.setBackground(Color.WHITE);
		jc.setPreferredSize(new Dimension(1000,1000));
		GUIHelper.showOnFrame(jc);
	}

}