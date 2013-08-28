package org.andork.torquescape;

import org.andork.util.ArrayUtils;
import org.andork.vecmath.FloatArrayVecmath;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class TorquescapeGLView extends GLSurfaceView {
	TorquescapeRenderer renderer;

	public TorquescapeGLView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		setRenderer(renderer = new TorquescapeRenderer());

		// Render the view only when there is a change in the drawing data
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private int mPreviousPointerCount;
	private int[] mPreiousPointerIds = new int[10];
	private float[] mPreviousX = new float[2];
	private float[] mPreviousY = new float[2];

	float[] tempMatrix = new float[16];
	float[] tempAxis = new float[3];

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (e.getPointerCount() == 1) {
				int index = 0;
				for (int i = 0; i < mPreiousPointerIds.length; i++) {
					if (mPreiousPointerIds[i] == e.getPointerId(0)) {
						index = i;
						break;
					}
				}
				float dx = x - mPreviousX[index];
				float dy = y - mPreviousY[index];

				float pan = (float) (dx * Math.PI / getWidth());
				float tilt = (float) (dy * Math.PI / getHeight());

				FloatArrayVecmath.rotY(tempMatrix, pan);
				FloatArrayVecmath.mmulRotational(tempMatrix, renderer.cameraMatrix, renderer.cameraMatrix);

				FloatArrayVecmath.mvmulAffine(renderer.cameraMatrix, 1, 0, 0, tempAxis);
				FloatArrayVecmath.setRotation(tempMatrix, tempAxis, tilt);
				FloatArrayVecmath.mmulRotational(tempMatrix, renderer.cameraMatrix, renderer.cameraMatrix);
			} else if (e.getPointerCount() == 2) {
				if (mPreviousPointerCount == 2) {
					float lastcx = (mPreviousX[0] + mPreviousX[1]) * 0.5f;
					float lastcy = (mPreviousY[0] + mPreviousY[1]) * 0.5f;
					float cx = (e.getX(0) + e.getX(1)) * 0.5f;
					float cy = (e.getY(0) + e.getY(1)) * 0.5f;

					float dcx = cx - lastcx;
					float dcy = cy - lastcy;

					float lastdx = mPreviousX[0] - mPreviousX[1];
					float lastdy = mPreviousY[0] - mPreviousY[1];
					float dx = e.getX(0) - e.getX(1);
					float dy = e.getY(0) - e.getY(1);

					float lastpinch = (float) Math.sqrt(lastdx * lastdx + lastdy * lastdy);
					float pinch = (float) Math.sqrt(dx * dx + dy * dy);
					float dpinch = pinch - lastpinch;

					float lastroll = (float) Math.atan2(lastdy, lastdx);
					float roll = (float) Math.atan2(dy, dx);
					float droll = roll - lastroll;

					renderer.cameraMatrix[3] += renderer.cameraMatrix[0] * -dcx + renderer.cameraMatrix[1] * dcy + renderer.cameraMatrix[2] * -dpinch;
					renderer.cameraMatrix[7] += renderer.cameraMatrix[4] * -dcx + renderer.cameraMatrix[5] * dcy + renderer.cameraMatrix[6] * -dpinch;
					renderer.cameraMatrix[11] += renderer.cameraMatrix[8] * -dcx + renderer.cameraMatrix[9] * dcy + renderer.cameraMatrix[10] * -dpinch;

					// FloatArrayVecmath.mvmulAffine(renderer.cameraMatrix, 0,
					// 0, 1,
					// tempAxis);
					// FloatArrayVecmath.setRotation(tempMatrix, tempAxis,
					// droll);
					// FloatArrayVecmath.mmulRotational(tempMatrix,
					// renderer.cameraMatrix, renderer.cameraMatrix);
				}
			}

			requestRender();
		}

		mPreviousPointerCount = e.getPointerCount();

		for (int i = 0; i < e.getPointerCount(); i++) {
			mPreiousPointerIds[i] = e.getPointerId(i);
			mPreviousX[i] = e.getX(i);
			mPreviousY[i] = e.getY(i);
		}

		return true;
	}
}
