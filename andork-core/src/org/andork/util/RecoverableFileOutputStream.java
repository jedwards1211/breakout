package org.andork.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RecoverableFileOutputStream extends FileOutputStream {
	private final Path mainFile;
	private final Path tempFile;
	private final Path backupFile;

	public RecoverableFileOutputStream(File file) throws FileNotFoundException {
		this(file, new FileRecoveryConfig() {});
	}

	public RecoverableFileOutputStream(File file, FileRecoveryConfig config) throws FileNotFoundException {
		super(config.getTempFile(file));
		mainFile = file.toPath();
		tempFile = config.getTempFile(file).toPath();
		backupFile = config.getBackupFile(file).toPath();
	}

	@Override
	public void close() throws IOException {
		flush();
		getFD().sync();
		super.close();

		try {
			Files.delete(backupFile);
		} catch (IOException ex) {
			if (Files.exists(backupFile)) {
				throw ex;
			}
		}

		try {
			Files.move(mainFile, backupFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			if (Files.exists(mainFile)) {
				throw ex;
			}
		}

		Files.move(tempFile, mainFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
	}
}
