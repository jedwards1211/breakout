package org.breakout.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.andork.func.Bimapper;
import org.andork.func.Color2HexStringBimapper;
import org.andork.util.ArrayUtils;

public class GradientModel {
	public final float[] fractions;
	public final Color[] colors;

	public GradientModel(float[] fractions, Color[] colors) {
		super();
		this.fractions = Objects.requireNonNull(fractions);
		this.colors = Objects.requireNonNull(colors);
		if (fractions.length != colors.length) {
			throw new IllegalArgumentException("fractions and colors must have the same length");
		}
		if (fractions.length < 2) {
			throw new IllegalArgumentException("fractions.length must be >= 2");
		}
		for (int i = 0; i < fractions.length; i++) {
			Objects.requireNonNull(colors[i]);
			if (!Float.isFinite(fractions[i])) {
				throw new IllegalArgumentException("fractions must be finite");
			}
		}
		for (int i = 1; i < fractions.length; i++) {
			if (fractions[i - 1] > fractions[i]) {
				throw new IllegalArgumentException("fractions must be sorted in ascending order");
			}
		}
	}

	public GradientModel(String... hexColors) {
		this(
			evenlySpaced(hexColors.length),
			ArrayUtils.map(hexColors, new Color[hexColors.length], GradientModel::convertHex));
	}

	private static float[] evenlySpaced(int n) {
		float[] result = new float[n];
		for (int i = 0; i < n; i++) {
			result[i] = (float) i / (n - 1);
		}
		return result;
	}

	private static Color convertHex(String hex) {
		hex = hex.replaceFirst("^#", "");
		if (hex.length() == 3)
			hex = hex + hex;
		return new Color(Integer.parseInt(hex, 16));
	}

	public GradientModel reverse() {
		float[] fractions = new float[this.fractions.length];
		for (int i = 0; i < fractions.length; i++) {
			fractions[i] = 1f - this.fractions[fractions.length - 1 - i];
		}
		return new GradientModel(fractions, ArrayUtils.reverse(ArrayUtils.copyOf(colors)));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final List<Float> fractions = new ArrayList<>();
		private final List<Color> colors = new ArrayList<>();

		public Builder add(double fraction, String hexColor) {
			return add(fraction, Color2HexStringBimapper.instance.unmap(hexColor));
		}

		public Builder add(double fraction, int red, int green, int blue) {
			return add(fraction, new Color(red, green, blue));
		}

		public Builder add(double fraction, Color color) {
			fractions.add((float) fraction);
			colors.add(color);
			return this;
		}

		public GradientModel build() {
			return new GradientModel(ArrayUtils.toFloatArray(fractions), ArrayUtils.toArray(colors, Color.class));
		}
	}

	public static final Bimapper<GradientModel, Object> bimapper = new Bimapper<GradientModel, Object>() {
		@Override
		public Object map(GradientModel in) {
			List<Map<String, Object>> out = new ArrayList<>();
			for (int i = 0; i < in.fractions.length; i++) {
				Map<String, Object> stop = new HashMap<>();
				stop.put("value", in.fractions[i]);
				stop.put("color", Color2HexStringBimapper.instance.map(in.colors[i]));
				out.add(stop);
			}
			return out;
		}

		@Override
		public GradientModel unmap(Object out) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> stops = (List<Map<String, Object>>) out;
			float[] fractions = new float[stops.size()];
			Color[] colors = new Color[stops.size()];
			int i = 0;
			for (Map<String, Object> stop : stops) {
				fractions[i] = ((Number) stop.get("value")).floatValue();
				colors[i++] = Color2HexStringBimapper.instance.unmap(stop.get("color"));
			}
			return new GradientModel(fractions, colors);
		}
	};

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(colors);
		result = prime * result + Arrays.hashCode(fractions);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GradientModel other = (GradientModel) obj;
		return Arrays.equals(colors, other.colors) && Arrays.equals(fractions, other.fractions);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("GradientModel [\n");
		for (int i = 0; i < fractions.length; i++) {
			b.append("  ").append(fractions[i]).append(Color2HexStringBimapper.instance.map(colors[i]));
		}
		b.append("]");
		return b.toString();
	}
}
