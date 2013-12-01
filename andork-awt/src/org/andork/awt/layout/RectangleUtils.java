package org.andork.awt.layout;

import java.awt.Insets;
import java.awt.Rectangle;

public class RectangleUtils {
	public static Rectangle interpolate(Rectangle r1, Rectangle r2, float f) {
		Rectangle out = new Rectangle();
		interpolate(r1, r2, f, out);
		return out;
	}

	public static void interpolate(Rectangle r1, Rectangle r2, float f, Rectangle out) {
		if (r1.x + r1.width == r2.x + r2.width) {
			int right = r1.x + r1.width;
			out.x = Math.round(r1.x * (1f - f) + r2.x * f);
			out.width = right - out.x;
		} else {
			out.x = Math.round(r1.x * (1f - f) + r2.x * f);
			out.width = Math.round(r1.width * (1f - f) + r2.width * f);
		}
		if (r1.y + r1.height == r2.y + r2.height) {
			int bottom = r1.y + r1.height;
			out.y = Math.round(r1.y * (1f - f) + r2.y * f);
			out.height = bottom - out.y;
		} else {
			out.y = Math.round(r1.y * (1f - f) + r2.y * f);
			out.height = Math.round(r1.height * (1f - f) + r2.height * f);
		}
	}

	public static Rectangle animate(Rectangle current, Rectangle target, long time,
			float factor, int extra, int delay) {
		Rectangle out = new Rectangle();
		animate(current, target, time, factor, extra, delay, out);
		return out;
	}

	public static void animate(Rectangle current, Rectangle target, long time,
			float factor, int extra, int delay, Rectangle out) {

		Side maxSide = null;
		int maxOffset = 0;
		float maxInterpFactor = 0f;

		for (Side side : Side.values()) {
			int curLoc = side.location(current);
			int targetLoc = side.location(target);
			int newPos = AnimationUtils.animate(curLoc, targetLoc,
					time, factor, extra, delay);
			int offset = Math.abs(newPos - curLoc);

			if (maxSide == null && offset > maxOffset) {
				maxSide = side;
				maxOffset = offset;
				maxInterpFactor = AnimationUtils.getInterpFactor(curLoc, targetLoc, newPos);
			}
		}
		maxInterpFactor = Math.min(1, Math.max(0, maxInterpFactor));

		interpolate(current, target, maxInterpFactor, out);
	}

	public static void inset( Rectangle r , Insets insets , Rectangle out )
	{
		out.x = r.x + insets.left;
		out.y = r.y + insets.top;
		out.width = r.width - insets.left - insets.right;
		out.height = r.height - insets.top - insets.bottom;
	}

	public static Rectangle insetCopy( Rectangle r , Insets insets )
	{
		Rectangle out = new Rectangle( );
		inset( r , insets , out );
		return out;
	}
}
