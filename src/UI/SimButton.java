package UI;


import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.plaf.ColorUIResource;
//import javax.swing.plaf.metal.MetalLookAndFeel;



public class SimButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7049044776349244140L;
	Color borderC;
	Color backgC; 
	Color rolloverC; 
	
	public SimButton(){
		super();
//		borderC = AbstractLookAndFeel.getTheme().getControlDarkShadow();
//		rolloverC = AbstractLookAndFeel.getTheme().getRolloverColor();
//		backgC = AbstractLookAndFeel.getTheme().getButtonBackgroundColor();

		borderC = new ColorUIResource(148, 148, 148);
		rolloverC = new ColorUIResource(255, 110, 90);
		backgC = new ColorUIResource(200, 200, 200);
		
//		borderC = MetalLookAndFeel.getCurrentTheme().getControlDarkShadow();
//		rolloverC = new ColorUIResource(255, 170, 90);
//		backgC = MetalLookAndFeel.getCurrentTheme().getWindowBackground();
		
		setBackground(backgC);
		
		setMargin(new Insets(2,2,2,2));
//		this.setBorder(new LineBorder(borderC, 1, false));
		this.setBorder(null);
		this.setFont(new java.awt.Font("Dialog",0,10));
//		this.setVerticalAlignment(SwingConstants.TOP);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent evt) {
				setBackground(backgC);
			}
			public void mouseEntered(MouseEvent evt) {
				setBackground(rolloverC);
			}
		});
	}
}
