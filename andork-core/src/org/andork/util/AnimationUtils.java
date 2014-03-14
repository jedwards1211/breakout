package org.andork.util;

public class AnimationUtils {

	private AnimationUtils() {
	}

	public static float animate(float current, float target, long time, float factor, float extra, int delay) {
		while (time > 0 && current != target) {
			if (target > current) {
				current = Math.min(target, current + (target - current + extra) * factor);
			} else {
				current = Math.max(target, current + (target - current - extra) * factor);
			}

			time -= delay;
		}
		return current;
	}

	public static int animate(int current, int target, long time, float factor, int extra, int delay) {
		while (time > 0 && current != target) {
			if (target > current) {
				current = Math.min(target, Math.max(current + 1,
						current + (int) ((target - current + extra) * factor)));
			} else {
				current = Math.max(target, Math.min(current - 1,
						current + (int) ((target - current - extra) * factor)));
			}

			time -= delay;
		}
		return current;
	}

	public static float getInterpFactor(float a, float b, float x) {
		if (b - a == 0) {
			return 0;
		}
		return (x - a) / (b - a);
	}

}
