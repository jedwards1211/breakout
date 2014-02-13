package org.andork.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.andork.io.Downloader;

public class DownloaderTest {
	public static void main(String[] args) throws MalformedURLException, IOException {
		final Downloader downloader = new Downloader().url(new URL("http://andork.com/index.html")).destFile(new File("trace.txt")).blockSize(10);
		downloader.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Transferred " + downloader.getNumBytesDownloaded() + "/" + downloader.getTotalSize());
			}
		});
		downloader.download();
	}
}
