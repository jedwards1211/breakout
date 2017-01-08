package org.andork.util;

import java.io.File;

public interface FileRecoveryConfig {
	public default File getBackupFile(File f) {
		return new File(f.getParentFile(), "~" + f.getName());
	}

	public default File getTempFile(File f) {
		return new File(f.getPath() + ".tmp");
	}
}
