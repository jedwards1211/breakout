package org.andork.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

public class RecoverableFileOutputStreamTest {
	FileRecoveryConfig config = new FileRecoveryConfig() {};

	@Test
	public void testSuccessfulSave() throws IOException {
		Path dir = Files.createTempDirectory(getClass().getName() + ".testSuccessfulSave");
		File file = new File(dir.toFile(), "test.txt");
		String text = "Hello world!";

		try (Writer out = new OutputStreamWriter(new RecoverableFileOutputStream(file))) {
			out.write(text);
		}

		Assert.assertTrue(file.exists());
		Assert.assertFalse(config.getTempFile(file).exists());
		Assert.assertFalse(config.getBackupFile(file).exists());

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			Assert.assertEquals(text, reader.readLine());
		}
	}

	@Test
	public void testOverwrite() throws IOException {
		Path dir = Files.createTempDirectory(getClass().getName() + ".testSuccessfulSave");
		File file = new File(dir.toFile(), "test.txt");
		String text1 = "Hello world!";
		String text2 = "blargh!";

		try (Writer out = new FileWriter(file)) {
			out.write(text1);
		}

		try (Writer out = new OutputStreamWriter(new RecoverableFileOutputStream(file))) {
			out.write(text2);
		}

		Assert.assertTrue(file.exists());
		Assert.assertFalse(config.getTempFile(file).exists());

		try (BufferedReader reader = new BufferedReader(new FileReader(config.getBackupFile(file)))) {
			Assert.assertEquals(text1, reader.readLine());
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			Assert.assertEquals(text2, reader.readLine());
		}
	}
}
