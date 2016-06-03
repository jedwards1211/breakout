/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.util;

import static org.andork.collect.EnumerationIterator.iterable;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.andork.collect.Visitor;

public class ClassFinder {
	private static String createClassName(File root, File file) {
		StringBuffer sb = new StringBuffer();
		String fileName = file.getName();
		sb.append(fileName.substring(0, fileName.lastIndexOf(".class")));
		file = file.getParentFile();
		while (file != null && !file.equals(root)) {
			sb.insert(0, '.').insert(0, file.getName());
			file = file.getParentFile();
		}
		return sb.toString();
	}

	private static boolean findClasses(File root, File file, boolean includeJars, Visitor<String> visitor) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				if (!findClasses(root, child, includeJars, visitor)) {
					return false;
				}
			}
		} else {
			if (file.getName().toLowerCase().endsWith(".jar") && includeJars) {
				JarFile jar = null;
				try {
					jar = new JarFile(file);
				} catch (Exception ex) {

				}
				if (jar != null) {
					for (ZipEntry entry : iterable(jar.entries())) {
						String name = entry.getName();
						int extIndex = name.lastIndexOf(".class");
						if (extIndex > 0) {
							if (!visitor.visit(name.substring(0, extIndex).replace("/", "."))) {
								return false;
							}
						}
					}

					String jarClassPath = null;
					try {
						jarClassPath = jar.getManifest().getMainAttributes().getValue("Class-Path");
					} catch (IOException e) {
					}

					if (jarClassPath != null) {
						for (String item : jarClassPath.split("\\s+")) {
							File itemFile = new File(item);
							if (!itemFile.equals(file)) {
								findClasses(root, itemFile, includeJars, visitor);
							}
						}
					}
				}
			} else if (file.getName().toLowerCase().endsWith(".class")) {
				if (!visitor.visit(createClassName(root, file))) {
					return false;
				}
			}
		}

		return true;
	}

	public static void findClasses(Visitor<String> visitor) {
		String classpath = System.getProperty("java.class.path");
		System.out.println(classpath);
		String[] paths = classpath.split(";");

		String javaHome = System.getProperty("java.home");
		File file = new File(javaHome + File.separator + "lib");
		if (file.exists()) {
			findClasses(file, file, true, visitor);
		}

		for (String path : paths) {
			file = new File(path);
			if (file.exists()) {
				findClasses(file, file, true, visitor);
			}
		}
	}
}
