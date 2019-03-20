package org.andork.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamUtils {
	private InputStreamUtils() {
	}
	
	public static byte[] readAllBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int numRead;
		while ((numRead = in.read(buf, 0, buf.length)) >= 0) {
			out.write(buf, 0, numRead);
		}
		return out.toByteArray();
	}
}
