package UI;

import java.util.EventObject;

/**
 * this helps the sema app to communicate with the interface
 * @author d
 *
 */
public class SemaEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3038650047711638021L;
	private int semaEventCode;
	private String content;
	public static final int MSGupdate  = 1;
	public static final int EnterFullscreen = 2;
	public static final int LeaveFullscreen = 3;
	public static final int UpdateUI = 4;
	public static final int RedrawUI = 5;
	
	public SemaEvent(Object source, int _semaEventCode) {
		super(source);
		semaEventCode = _semaEventCode;
	}
	
	public int getType() {
		return semaEventCode;
	}

	public void setContent(String msg) {
		content = msg;
	}
	public String getContent(){
		return content;
	}
}