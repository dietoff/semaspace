package UI;


import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

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
		borderC = new Color(.5f, 0.5f, 0.5f, 1f);
		backgC = new Color(.8f, .8f, .8f, 1f);
		rolloverC =new Color(.7f, .7f, 1f, 1f);
		setBackground(backgC);
		setMargin(new Insets(2,2,2,2));
		this.setBorder(new LineBorder(borderC, 1, false));
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
