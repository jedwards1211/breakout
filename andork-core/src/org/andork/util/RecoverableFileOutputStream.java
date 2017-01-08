package org.andork.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class RecoverableFileOutputStream extends FileOutputStream {
	private final Path mainFile;
	private final Path tempFile;
	private final Path backupFile;

	private final Object closeLock = new Object();
	private volatile boolean closed;

	private static final Logger logger = Logger.getLogger(RecoverableFileOutputStream.class.getSimpleName());

	public RecoverableFileOutputStream(File file) throws FileNotFoundException {
		this(file, new FileRecoveryConfig() {});
	}

	public RecoverableFileOutputStream(File file, FileRecoveryConfig config) throws FileNotFoundException {
		super(config.getTempFile(file));
		mainFile = file.toPath();
		tempFile = config.getTempFile(file).toPath();
		backupFile = config.getBackupFile(file).toPath();
		logger.info("opened " + tempFile);
	}

	@Override
	public void close() throws IOException {
		synchronized (closeLock) {
			if (closed) {
				return;
			}
			closed = true;
		}
		try {
			flush();
		} catch (IOException ex) {
			logger.severe("failed to flush " + tempFile + "; " + ex.getLocalizedMessage());
			throw ex;
		}
		logger.info("flushed " + tempFile);

		try {
			super.close();
		} catch (IOException ex) {
			logger.severe("failed to close " + tempFile + "; " + ex.getLocalizedMessage());
			throw ex;
		}
		logger.info("closed " + tempFile);

		try {
			Files.move(mainFile, backupFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			logger.info("moved " + mainFile + " -> " + backupFile);
		} catch (IOException ex) {
			logger.severe("failed to move " + mainFile + " -> " + backupFile + "; " + ex.getLocalizedMessage());
			if (Files.exists(mainFile)) {
				throw ex;
			}
		}

		try {
			Files.move(tempFile, mainFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			logger.severe("failed to move " + tempFile + " -> " + mainFile + "; " + ex.getLocalizedMessage());
			throw ex;
		}
		logger.info("moved " + tempFile + " -> " + mainFile);
	}
}
