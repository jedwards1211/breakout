package org.andork.torquescape;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.andork.torquescape.model.StandardSlice;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.gen.DefaultTrackGenerator;
import org.andork.torquescape.model.normal.NormalGenerator;
import org.andork.torquescape.model.track.Track;
import org.andork.torquescape.model.track.Track1;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class TorquescapeRenderer implements Renderer {
	float[] mVMatrix = new float[16];
	float[] mProjMatrix = new float[16];
	float[] mPanMatrix = new float[16];
	float[] mTiltMatrix = new float[16];
	float[] mMVMatrix = new float[16];
	float[] mMVPMatrix = new float[16];

	TorquescapeScene scene;

	float mPan = 0;
	float mTilt = 0;

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0f, 0f, 0f, 1.0f);
		scene = initScene();
	}

	public TorquescapeScene getScene() {
		return scene;
	}

	public void setScene(TorquescapeScene scene) {
		this.scene = scene;
	}

	public void onDrawFrame(GL10 unused) {
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);

		// Set the camera position (View matrix)
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, 5, 0f, 0f, 1f, 0f, 1.0f, 0.0f);

		// Create a rotation for the triangle
		// long time = SystemClock.uptimeMillis() % 4000L;
		// float angle = 0.090f * ((int) time);
		Matrix.setIdentityM(mPanMatrix, 0);
		Matrix.setRotateM(mPanMatrix, 0, mPan, 0, 1.0f, 0f);

		// Create a rotation for the triangle
		// long time = SystemClock.uptimeMillis() % 4000L;
		// float angle = 0.090f * ((int) time);
		Matrix.setIdentityM(mTiltMatrix, 0);
		Matrix.setRotateM(mTiltMatrix, 0, mTilt, 1, 0, 0f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVMatrix, 0, mTiltMatrix, 0, mPanMatrix, 0);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVMatrix, 0, mVMatrix, 0, mMVMatrix, 0);

		// Calculate the projection and view transformation
		// Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

		// Combine the rotation matrix with the projection and camera view
		// Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);

		if (scene != null) {
			scene.draw(mMVMatrix, mProjMatrix);
		}
	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.perspectiveM(mProjMatrix, 0, 90, ratio, 0.001f, 100);
		// Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.001f, 100f);
	}

	private TorquescapeScene initScene() {
		System.out.println(System.getProperty("java.class.path"));
		Track track = new Track1();

		DefaultTrackGenerator generator = new DefaultTrackGenerator();
		generator.add(track.getXformFunction(), track.getSectionFunction(), track.getMeshingFunction(), 0, (float) Math.PI * 4, (float) Math.PI / 180);

		float[] verts = generator.getVertices();
		char[] indices = generator.getIndices();

		System.out.println("verts.length: " + verts.length);
		System.out.println("indices.length: " + indices.length);

		NormalGenerator.generateNormals(verts, 3, 6, indices, 0, indices.length);

		Zone zone1 = new Zone();
		zone1.init(verts, indices);

		StandardSlice slice1 = new StandardSlice();
		slice1.setIndices(indices);
		set(slice1.ambientColor, 0.2f, 0, 0, 1);
		set(slice1.diffuseColor, 1, 0, 0, 1);
		zone1.slices.add(slice1);

		ZoneRenderer zoneRend1 = new ZoneRenderer(zone1);
		zoneRend1.init();

		TorquescapeScene scene = new TorquescapeScene();
		scene.zoneRenderers.add(zoneRend1);

		return scene;
	}

	private void set(float[] array, float a, float b, float c, float d) {
		array[0] = a;
		array[1] = b;
		array[2] = c;
		array[3] = d;
	}
}