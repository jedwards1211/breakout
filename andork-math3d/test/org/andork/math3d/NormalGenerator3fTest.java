package org.andork.math3d;

import org.andork.math3d.NormalGenerator3f.MeshBuilder;
import org.andork.math3d.NormalGenerator3f.Triangle;
import org.junit.Assert;
import org.junit.Test;

public class NormalGenerator3fTest {
	@Test
	public void test001() {
		MeshBuilder mesh = new MeshBuilder();

		Triangle side = mesh.add(1, 0, 0, 0, 1, 0, -1, 0, 0);

		float SQRT_2_2 = (float) Math.sqrt(2) / 2;
		Triangle t1 = mesh.add(-1, 0, 0, 0, 1, 0, -SQRT_2_2, 0, SQRT_2_2);
		Triangle t2 = mesh.add(-SQRT_2_2, 0, SQRT_2_2, 0, 1, 0, 0, 0, 1);
		Triangle t3 = mesh.add(0, 0, 1, 0, 1, 0, SQRT_2_2, 0, SQRT_2_2);
		Triangle t4 = mesh.add(SQRT_2_2, 0, SQRT_2_2, 0, 1, 0, 1, 0, 0);

		Triangle[] triangles = mesh.getTriangles();

		NormalGenerator3f generator = new NormalGenerator3f(triangles);
		generator.foldAngle(Math.PI / 2);
		generator.foldSharpness(1);
		int vertexCount = generator.generateNormals();
		Assert.assertEquals(9, vertexCount);
	}
}
