package org.andork.swing.filechooser;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class DirectoryFileFilter extends FileFilter {

	private DirectoryFileFilter() {
	}
	
	public static DirectoryFileFilter INSTANCE = new DirectoryFileFilter();

	@Override
	public boolean accept(File f) {
		return f.isDirectory();
	}

	@Override
	public String getDescription() {
		return "Directories";
	}
	
	public static JFileChooser install(JFileChooser fileChooser) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(INSTANCE);
		return fileChooser;
	}
	
	public static File getSelectedDirectory(JFileChooser fileChooser) {
		File selected = fileChooser.getSelectedFile();
		return selected != null ? selected : fileChooser.getCurrentDirectory();
	}

}
