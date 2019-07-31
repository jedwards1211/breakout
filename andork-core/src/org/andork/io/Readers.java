package org.andork.io;

import java.io.BufferedReader;
import java.io.Reader;

public class Readers {
	private Readers() {
	}

	public static BufferedReader buffered(Reader reader) {
		if (reader instanceof BufferedReader)
			return (BufferedReader) reader;
		return new BufferedReader(reader);
	}

	public static BufferedReader buffered(BufferedReader reader) {
		return reader;
	}
}
