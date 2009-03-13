package UI;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import semaGL.FileIO;

public class SemaTableFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return true;
	}

	@Override
	public String getDescription() {
		return "SemaSpace Table (*.tab, *.tab.n)";
	}

}


