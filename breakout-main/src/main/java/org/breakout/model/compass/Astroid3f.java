package org.breakout.model.compass;

/*
 * Octant for numSubdivisions = 3
 * 
 * 0 strip = 0 1 2 strip = 1 3 4 5 strip = 2 6 7 8 9 strip = 3
 */

public class Astroid3f {
	public final float[][] points;
	public final float[][] normals;
	public final int[] triangles;

	public Astroid3f(float radius, int numSubdivisions) {
		int numVerticesPerOctant = (numSubdivisions + 1) * (numSubdivisions + 2) / 2;
		int numTrianglesPerOctant = numSubdivisions * (numSubdivisions + 1) / 2 +
				numSubdivisions * (numSubdivisions - 1) / 2;

		points = new float[numVerticesPerOctant * 8][];
		normals = new float[numVerticesPerOctant * 8][];
		triangles = new int[numTrianglesPerOctant * 8 * 3];

		int pointsIndex = 0;
		int trianglesIndex = 0;
		
		float root2_2 = (float) Math.sqrt(2) / 2;

		for (int strip = 0; strip <= numSubdivisions; strip++) {
			double phi = Math.PI / 2 * strip / numSubdivisions;
			float z = (float) (3 * Math.cos(phi) + Math.cos(3 * phi)) / 4;
			float horizSize = (float) (3 * Math.sin(phi) - Math.sin(3 * phi)) / 4;
			for (int i = 0; i <= strip; i++) {
				double theta = strip == 0 ? 0 : Math.PI / 2 * i / strip;
				float x = horizSize * (float) (3 * Math.cos(theta) + Math.cos(3 * theta)) / 4;
				float y = horizSize * (float) (3 * Math.sin(theta) - Math.sin(3 * theta)) / 4;
				points[pointsIndex] = new float[] {
						radius * x,
						radius * y,
						radius * z,
				};
				// TODO: calc better normals
				normals[pointsIndex++] = new float[] { root2_2, root2_2, root2_2 };
			}
		}

		for (int strip = 0; strip < numSubdivisions; strip++) {
			for (int i = 0; i <= strip; i++) {
				int startIndex = strip * (strip + 1) / 2;
				triangles[trianglesIndex++] = startIndex + i;
				triangles[trianglesIndex++] = startIndex + i + strip + 1;
				triangles[trianglesIndex++] = startIndex + i + strip + 2;

				if (i > 0) {
					triangles[trianglesIndex++] = startIndex + i;
					triangles[trianglesIndex++] = startIndex + i - 1;
					triangles[trianglesIndex++] = startIndex + i + strip + 1;
				}
			}
		}

		for (int octant = 6; octant >= 0; octant--) {
			int xq = (octant & 0x4) > 0 ? 1 : -1;
			int yq = (octant & 0x2) > 0 ? 1 : -1;
			int zq = (octant & 0x1) > 0 ? 1 : -1;
			
			int pointsOffset = pointsIndex;
			
			for (int i = 0; i < numVerticesPerOctant; i++) {
				float[] sourcePoint = points[i];
				float[] sourceNormal = normals[i];
				points[pointsIndex] = new float[] {
						xq * sourcePoint[0],
						yq * sourcePoint[1],
						zq * sourcePoint[2],
				};
				normals[pointsIndex++] = new float[] {
						xq * sourceNormal[0],
						yq * sourceNormal[1],
						zq * sourceNormal[2],
				};
			}

			for (int i = 0; i < numTrianglesPerOctant * 3; i += 3) {
				if (xq * yq * zq > 0) {
					triangles[trianglesIndex++] = triangles[i] + pointsOffset;
					triangles[trianglesIndex++] = triangles[i + 1] + pointsOffset;
					triangles[trianglesIndex++] = triangles[i + 2] + pointsOffset;
				} else {
					triangles[trianglesIndex++] = triangles[i + 2] + pointsOffset;
					triangles[trianglesIndex++] = triangles[i + 1] + pointsOffset;
					triangles[trianglesIndex++] = triangles[i] + pointsOffset;
				}
			}
		}
	}
}
