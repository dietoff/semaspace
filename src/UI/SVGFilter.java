package UI;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import semaGL.FileIO;

public class SVGFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return f.toString().endsWith(".svg");
	}

	@Override
	public String getDescription() {
		return "SVG Image";
	}

}


