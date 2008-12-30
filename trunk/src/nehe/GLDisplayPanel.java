package nehe;

import com.sun.opengl.util.FPSAnimator;

import javax.swing.*;
import javax.imageio.ImageIO;
import javax.media.opengl.*;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * @author Pepijn Van Eeckhoudt
 */
public class GLDisplayPanel {
	private static final int DONT_CARE = -1;

	private JPanel frame;
	private GLCanvas glCanvas;
	private FPSAnimator animator;
	private GraphicsDevice usedDevice;

	private MyHelpOverlayGLEventListener helpOverlayGLEventListener = new MyHelpOverlayGLEventListener();
	private MyExceptionHandler exceptionHandler = new MyExceptionHandler();

	public static GLDisplayPanel createGLDisplay( String title ) {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return new GLDisplayPanel();
	}

	private GLDisplayPanel() {
		glCanvas = new GLCanvas(new GLCapabilities());
		glCanvas.setIgnoreRepaint( true );
		glCanvas.addGLEventListener( helpOverlayGLEventListener );
		frame = new JPanel();
		frame.setLayout( new BorderLayout() );
		frame.add( glCanvas, BorderLayout.CENTER );
		addKeyListener( new MyKeyAdapter());

		animator = new FPSAnimator( glCanvas, 60 );
		animator.setRunAsFastAsPossible(false);
	}
	public JPanel getJPanel() {
		return frame;
	}
	
	public void start() {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setSize( frame.getPreferredSize() );
			frame.setLocation(
					( screenSize.width - frame.getWidth() ) / 2,
					( screenSize.height - frame.getHeight() ) / 2
			);
			frame.setVisible( true );

			glCanvas.requestFocus();

			animator.start();
		} catch ( Exception e ) {
			exceptionHandler.handleException( e );
		}
	}

//	public void stop() {
//		try {
//			animator.stop();
//			usedDevice.setFullScreenWindow( null );
//			usedDevice = null;
//		} catch ( Exception e ) {
//			exceptionHandler.handleException( e );
//		} finally {
//			System.exit( 0 );
//		}
//	}

	private DisplayMode findDisplayMode( DisplayMode[] displayModes, int requestedWidth, int requestedHeight, int requestedDepth, int requestedRefreshRate ) {
		// Try to find an exact match
		DisplayMode displayMode = findDisplayModeInternal( displayModes, requestedWidth, requestedHeight, requestedDepth, requestedRefreshRate );

		// Try again, ignoring the requested bit depth
		if ( displayMode == null )
			displayMode = findDisplayModeInternal( displayModes, requestedWidth, requestedHeight, DONT_CARE, DONT_CARE );

		// Try again, and again ignoring the requested bit depth and height
		if ( displayMode == null )
			displayMode = findDisplayModeInternal( displayModes, requestedWidth, DONT_CARE, DONT_CARE, DONT_CARE );

		// If all else fails try to get any display mode
		if ( displayMode == null )
			displayMode = findDisplayModeInternal( displayModes, DONT_CARE, DONT_CARE, DONT_CARE, DONT_CARE );

		return displayMode;
	}

	private DisplayMode findDisplayModeInternal( DisplayMode[] displayModes, int requestedWidth, int requestedHeight, int requestedDepth, int requestedRefreshRate ) {
		DisplayMode displayModeToUse = null;
		for ( int i = 0; i < displayModes.length; i++ ) {
			DisplayMode displayMode = displayModes[i];
			if ( ( requestedWidth == DONT_CARE || displayMode.getWidth() == requestedWidth ) &&
					( requestedHeight == DONT_CARE || displayMode.getHeight() == requestedHeight ) &&
					( requestedHeight == DONT_CARE || displayMode.getRefreshRate() == requestedRefreshRate ) &&
					( requestedDepth == DONT_CARE || displayMode.getBitDepth() == requestedDepth ) )
				displayModeToUse = displayMode;
		}

		return displayModeToUse;
	}

	public void addGLEventListener( GLEventListener glEventListener ) {
		this.helpOverlayGLEventListener.addGLEventListener( glEventListener );
	}

	public void removeGLEventListener( GLEventListener glEventListener ) {
		this.helpOverlayGLEventListener.removeGLEventListener( glEventListener );
	}

	public void addKeyListener( KeyListener l ) {
		glCanvas.addKeyListener( l );
	}

	public void addMouseListener( MouseListener l ) {
		glCanvas.addMouseListener( l );
	}

	public void addMouseMotionListener( MouseMotionListener l ) {
		glCanvas.addMouseMotionListener( l );
	}

	public void removeKeyListener( KeyListener l ) {
		glCanvas.removeKeyListener( l );
	}

	public void removeMouseListener( MouseListener l ) {
		glCanvas.removeMouseListener( l );
	}

	
	public void removeMouseMotionListener( MouseMotionListener l ) {
		glCanvas.removeMouseMotionListener( l );
	}
	
	public void addFocusListener( FocusListener l ) {
	glCanvas.addFocusListener(l);
	}
	
	public void removeFocusListener( FocusListener l ) {
	glCanvas.removeFocusListener(l);
	}

	private class MyKeyAdapter extends KeyAdapter {
		public MyKeyAdapter() {
		}

		public void keyReleased( KeyEvent e ) {
			switch ( e.getKeyCode() ) {
//			case KeyEvent.VK_ESCAPE:
//				stop();
//				break;
//				case KeyEvent.VK_F1:
//	break;
			}
		}
	}

//	private class MyWindowAdapter extends WindowAdapter {
//		public void windowClosing( WindowEvent e ) {
//			stop();
//		}
//	}

	private class MyExceptionHandler implements ExceptionHandler {
		public void handleException( final Exception e ) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					StringWriter stringWriter = new StringWriter();
					PrintWriter printWriter = new PrintWriter( stringWriter );
					e.printStackTrace( printWriter );
					JOptionPane.showMessageDialog( frame, stringWriter.toString(), "Exception occurred", JOptionPane.ERROR_MESSAGE );
//					stop();
				}
			} );
		}
	}

	private static class MyHelpOverlayGLEventListener implements GLEventListener {
		private java.util.List eventListeners = new ArrayList();
		private HelpOverlay helpOverlay = new HelpOverlay();


		public void registerKeyStroke( KeyStroke keyStroke, String description ) {
			helpOverlay.registerKeyStroke( keyStroke, description );
		}

		public void registerMouseEvent( int id, int modifiers, String description ) {
			helpOverlay.registerMouseEvent( id, modifiers, description );
		}

		public void addGLEventListener( GLEventListener glEventListener ) {
			eventListeners.add( glEventListener );
		}

		public void removeGLEventListener( GLEventListener glEventListener ) {
			eventListeners.remove( glEventListener );
		}

		public void display( GLAutoDrawable glDrawable ) {
			for ( int i = 0; i < eventListeners.size(); i++ ) {
				( (GLEventListener) eventListeners.get( i ) ).display( glDrawable );
			}
		}

		public void displayChanged( GLAutoDrawable glDrawable, boolean b, boolean b1 ) {
			for ( int i = 0; i < eventListeners.size(); i++ ) {
				( (GLEventListener) eventListeners.get( i ) ).displayChanged( glDrawable, b, b1 );
			}
		}

		public void init( GLAutoDrawable glDrawable ) {
			for ( int i = 0; i < eventListeners.size(); i++ ) {
				( (GLEventListener) eventListeners.get( i ) ).init( glDrawable );
			}
		}

		public void reshape( GLAutoDrawable glDrawable, int i0, int i1, int i2, int i3 ) {
			for ( int i = 0; i < eventListeners.size(); i++ ) {
				( (GLEventListener) eventListeners.get( i ) ).reshape( glDrawable, i0, i1, i2, i3 );
			}
		}
	}
}
