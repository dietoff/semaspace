package UI;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import semaGL.FileIO;

public class GmlFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return f.toString().endsWith(".gml");
	}

	@Override
	public String getDescription() {
		return "gml network";
	}

}


