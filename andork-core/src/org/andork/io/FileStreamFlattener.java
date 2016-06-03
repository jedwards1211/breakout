package org.andork.io;

import java.io.File;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileStreamFlattener implements Function<File, Stream<File>> {
	public static final FileStreamFlattener instance = new FileStreamFlattener();

	public static void main(String[] args) {
		Arrays.asList(new File(".")).stream().flatMap(instance).forEach(System.out::println);
	}

	private FileStreamFlattener() {

	}

	@Override
	public Stream<File> apply(File f) {
		if (f == null) {
			return null;
		}
		if (f.isDirectory()) {
			return Arrays.asList(f.listFiles()).stream().flatMap(this);
		}
		return Stream.of(f);
	}
}
