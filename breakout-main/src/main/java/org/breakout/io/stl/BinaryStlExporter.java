package org.breakout.io.stl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.andork.math3d.Vecmath;
import org.andork.task.Task;
import org.breakout.model.calc.CalcProject;
import org.breakout.model.calc.CalcShot;

public class BinaryStlExporter {
	public static void write(CalcProject project, OutputStream out, Task<?> task) throws IOException {
		if (task != null) task.setIndeterminate(true);
		int numTriangles = 0;
		for (CalcShot shot : project.shots.values()) {
			numTriangles += shot.indices.length / 3;
		}
		ByteBuffer buffer = ByteBuffer.allocate(80 + 4 + numTriangles * (3 * 4 * 4 + 2));
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(new byte[80]);
		buffer.putInt(numTriangles);
		
		float[] normal = new float[3];
		
		if (task != null) {
			task.setIndeterminate(false);
			task.setTotal(project.shots.size());
		}
		for (CalcShot shot : project.shots.values()) {
			for (int i = 0; i < shot.indices.length; i += 3) {
				int i0 = shot.indices[i] * 3;
				int i1 = shot.indices[i + 1] * 3;
				int i2 = shot.indices[i + 2] * 3;
				Vecmath.threePointNormal(shot.vertices, i0, shot.vertices, i1, shot.vertices, i2, normal, 0);
				buffer.putFloat(normal[0]);
				buffer.putFloat(normal[1]);
				buffer.putFloat(normal[2]);
				buffer.putFloat(shot.vertices[i0]);
				buffer.putFloat(shot.vertices[i0 + 1]);
				buffer.putFloat(shot.vertices[i0 + 2]);
				buffer.putFloat(shot.vertices[i1]);
				buffer.putFloat(shot.vertices[i1 + 1]);
				buffer.putFloat(shot.vertices[i1 + 2]);
				buffer.putFloat(shot.vertices[i2]);
				buffer.putFloat(shot.vertices[i2 + 1]);
				buffer.putFloat(shot.vertices[i2 + 2]);
				// attribute byte count
				buffer.putShort((short) 0);
			}
			if (task != null) task.increment();
		}
		if (task != null) {
			task.setIndeterminate(true);
		}
		out.write(buffer.array());
		out.flush();
	}
}
