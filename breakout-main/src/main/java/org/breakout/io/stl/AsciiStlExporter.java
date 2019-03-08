package org.breakout.io.stl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.andork.math3d.Vecmath;
import org.andork.task.Task;
import org.breakout.model.calc.CalcProject;
import org.breakout.model.calc.CalcShot;

public class AsciiStlExporter {
	public static void write(CalcProject project, OutputStream out, Task<?> task) throws IOException {
		if (task != null) {
			task.setIndeterminate(false);
			task.setTotal(project.shots.size());
		}

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(out, "ASCII"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Are you serious? " + e.getLocalizedMessage());
		}
		writer.write("solid caves\n");
		
		float[] normal = new float[3];
		
		for (CalcShot shot : project.shots.values()) {
			for (int i = 0; i < shot.indices.length; i += 3) {
				int i0 = shot.indices[i] * 3;
				int i1 = shot.indices[i + 1] * 3;
				int i2 = shot.indices[i + 2] * 3;
				Vecmath.threePointNormal(shot.vertices, i0, shot.vertices, i1, shot.vertices, i2, normal, 0);
				writer.write(String.format("facet normal %e %e %e\n", normal[0], normal[1], normal[2]));
				writer.write("    outer loop\n");
				writer.write(String.format("        vertex %e %e %e\n", shot.vertices[i0], shot.vertices[i0 + 1], shot.vertices[i0 + 2]));
				writer.write(String.format("        vertex %e %e %e\n", shot.vertices[i1], shot.vertices[i1 + 1], shot.vertices[i1 + 2]));
				writer.write(String.format("        vertex %e %e %e\n", shot.vertices[i2], shot.vertices[i2 + 1], shot.vertices[i2 + 2]));
				writer.write("    endloop\n");
				writer.write("endfacet\n");
			}
			if (task != null) task.increment();
		}
		writer.write("endsolid caves\n");
		writer.flush();
	}
}
