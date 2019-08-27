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
