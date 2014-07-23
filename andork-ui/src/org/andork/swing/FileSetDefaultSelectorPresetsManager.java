/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.SwingUtilities;

import org.andork.persist.PersistenceScheduler;


/**
 * A {@link DefaultSelectorPresetsManager} that saves presets to individual
 * files in a given directory. The files are saved/loaded on a given
 * {@link ScheduledExecutorService}, and the implementation doesn't have to
 * handle threading, only I/O.
 * 
 * @author andy.edwards
 * 
 * @param <T>
 *            the preset type.
 */
public abstract class FileSetDefaultSelectorPresetsManager<T> extends DefaultSelectorPresetsManager<T> {
	private File						presetDir;
	private PersistenceScheduler		persistenceScheduler;
	private ScheduledExecutorService	executor;

	protected FileSetDefaultSelectorPresetsManager(File presetDir, PersistenceScheduler persistenceScheduler, ScheduledExecutorService executor) {
		super();
		this.presetDir = presetDir;
		this.persistenceScheduler = persistenceScheduler;
		this.executor = executor;
		deserializeAll();
	}

	@Override
	protected void serialize(final T preset) {
		if (preset == getDefaultPreset() || preset == getUntitledPreset()) {
			return;
		}

		persistenceScheduler.save(executor, preset, new Runnable() {
			@Override
			public void run() {
				if (!presetDir.exists()) {
					presetDir.mkdirs();
				}

				File file = new File(presetDir, getName(preset));
				try {
					serialize(preset, file);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	protected void deserializeAll() {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				if (!presetDir.exists() || !presetDir.isDirectory()) {
					return;
				}

				final List<T> presets = new ArrayList<T>();

				for (File file : presetDir.listFiles()) {
					try {
						presets.add(deserialize(file));
					} catch (Exception e) {
						file.delete();
						e.printStackTrace();
					}
				}

				doSwing(new Runnable() {
					@Override
					public void run() {
						for (T preset : presets) {
							int index = search(getName(preset));
							if (index < 0) {
								getSelector().addAvailableValue(-(index + 1), preset);
							} else {
								getSelector().setAvailableValue(index, preset);
							}
						}
					}
				});
			}
		});
	}

	protected abstract void serialize(T preset, File file) throws Exception;

	protected abstract T deserialize(File file) throws Exception;

	private void doSwing(Runnable r) {
		try {
			SwingUtilities.invokeAndWait(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
