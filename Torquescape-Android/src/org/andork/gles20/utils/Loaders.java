package org.andork.gles20.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.opengl.GLES20;

public class Loaders {
	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	public static String load(URL url) {
		BufferedReader reader = null;
		try {
			StringBuffer sb = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(url.openStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				if (sb.length() > 0) {
					sb.append('\n');
				}
				sb.append(line);
			}

			return sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static String load(String path) {
		BufferedReader reader = null;
		try {
			File file = new File(path);
			StringBuffer sb = new StringBuffer();
			reader = new BufferedReader(new FileReader(file));

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
