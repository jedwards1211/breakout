package org.andork.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileUtils {
	public static ByteBuffer slurp(Path path) throws IOException {
		if (Files.size(path) > Integer.MAX_VALUE) {
			throw new IOException("File is too large");
		}

		try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
			ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());

			while (channel.read(buffer) > 0) {
				;
			}

			buffer.position(0);

			return buffer;
		}
	}

	public static byte[] slurpAsBytes(Path path) throws IOException {
		return slurp(path).array();
	}

	public static String slurpAsString(Path path) throws IOException {
		return new String(slurp(path).array());
	}
}
