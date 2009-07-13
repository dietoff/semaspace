package UI;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class SemaProjectFileFilter extends FileFilter {

	@Override
	public String getDescription() {
		return "SemaSpace Project File (*.sema)";
	}

	@Override
	public boolean accept(File f) {
		return true;
	}

}


