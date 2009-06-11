package UI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;


public class SemaTheme extends DefaultMetalTheme {
    private final ColorUIResource primary1 = new ColorUIResource(255, 150, 110);
    private final ColorUIResource primary2 = new ColorUIResource(255, 150, 110);
    private final ColorUIResource primary3 = new ColorUIResource(255, 110, 90);
    
    private final ColorUIResource secondary1 = new ColorUIResource(30,30, 30 );
    private final ColorUIResource secondary2 = new ColorUIResource(220,220,220);
    private final ColorUIResource secondary3 = new ColorUIResource(240, 240, 240);

    private FontUIResource boldFont =
        new FontUIResource("SansSerif", Font.BOLD, 12);
    
    private FontUIResource plainFont =
        new FontUIResource("SansSerif", Font.PLAIN, 10);;
    

    @Override
	public String getName() {
        return "sema";
    }

    public SemaTheme() {
    }

    ColorUIResource black = secondary1;//new ColorUIResource(new Color(100, 100, 100));
    ColorUIResource white = new ColorUIResource(new Color(255, 255, 255));

    @Override
	protected ColorUIResource getBlack() {
        return black;
    };
    @Override
	protected ColorUIResource getWhite() {
        return white;
    };

    // these are blue in Metal Default Theme
    @Override
	protected ColorUIResource getPrimary1() {
        return primary1;
    }
    @Override
	protected ColorUIResource getPrimary2() {
        return primary2;
    }
    @Override
	protected ColorUIResource getPrimary3() {
        return primary3;
    }

    // these are gray in Metal Default Theme
    @Override
	protected ColorUIResource getSecondary1() {
        return secondary1;
    }
    @Override
	protected ColorUIResource getSecondary2() {
        return secondary2;
    }
    @Override
	protected ColorUIResource getSecondary3() {
        return secondary3;
    }

 
    /**
     * Gets the Font of Labels in many cases.
     *
     * @return The Font of Labels in many cases.
     */
    @Override
	public FontUIResource getControlTextFont()
    {
      return plainFont;
    }

    /**
     * Gets the Font of Menus and MenuItems.
     *
     * @return The Font of Menus and MenuItems.
     */
    @Override
	public FontUIResource getMenuTextFont()
    {
      return plainFont;
    }

    /**
     * Gets the Font of Nodes in JTrees.
     *
     * @return The Font of Nodes in JTrees.
     */
    @Override
	public FontUIResource getSystemTextFont()
    {
      return plainFont;
    }

    /**
     * Gets the Font in TextFields, EditorPanes, etc.
     *
     * @return The Font in TextFields, EditorPanes, etc.
     */
    @Override
	public FontUIResource getUserTextFont()
    {
      return plainFont;
    }    
    
    @Override
	public FontUIResource getWindowTitleFont()
    {
      return boldFont;
    }
    
}