package UI;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import semaGL.FileIO;

public class SemaInlineFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return true;
	}

	@Override
	public String getDescription() {
		return "SemaSpace Inline (*.txt, *.txt.n)";
	}

}


