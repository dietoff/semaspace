package UI;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import semaGL.FileIO;

public class GraphMLFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return f.toString().endsWith(".graphml");
	}

	@Override
	public String getDescription() {
		return "graphML network";
	}

}


