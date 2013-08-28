package org.andork.torquescape;


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
	private float mPreviousX;
	private float mPreviousY;

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:

			float dx = x - mPreviousX;
			float dy = y - mPreviousY;

			renderer.mPan -= dx * 180f / getWidth(); // = 180.0f / 320
			renderer.mTilt -= dy * 180f / getHeight(); // = 180.0f / 320
			requestRender();
		}

		mPreviousX = x;
		mPreviousY = y;
		return true;
	}
}
