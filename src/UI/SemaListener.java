package UI;

import java.util.EventListener;

/**
 * this helps the sema app to communicate with the interface
 * @author d
 *
 */
public interface SemaListener extends EventListener  {
	
public void eventReceived(SemaEvent sevt);

}