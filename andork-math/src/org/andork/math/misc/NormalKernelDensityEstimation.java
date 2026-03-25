package org.andork.math.misc;

import java.util.Arrays;
import java.util.function.DoubleFunction;

public class NormalKernelDensityEstimation implements DoubleFunction<Double> {
	private final double[] samples;

	private double multiplier;
	private double expMultiplier;
	private double mean;
	private double stddev;

	public NormalKernelDensityEstimation(double[] samples) {
		this(samples, Double.NaN);
	}

	public NormalKernelDensityEstimation(double[] samples, double smoothing) {
		this.samples = samples;

		double x = 0;
		for (double sample : samples) {
			x += sample;
		}
		mean = x / samples.length;

		x = 0;
		for (double sample : samples) {
			double diff = sample - mean;
			x += diff * diff;
		}
		stddev = Math.sqrt(x / samples.length);

		if (Double.isNaN(smoothing)) {
			if (!isSorted(samples))
				Arrays.sort(samples);
			int n = samples.length;
			double interquartileRange = samples[(int) (n * 3 / 4f)] - samples[(int) (n / 4f)];
			smoothing = 0.9 * Math.min(stddev, interquartileRange / 1.34) * Math.pow(n, -1 / 5.0);
		}

		multiplier = 1 / (samples.length * smoothing * stddev * Math.sqrt(2 * Math.PI));
		expMultiplier = -1 / (2 * smoothing * smoothing * stddev * stddev);
	}

	public double get(double x) {
		double sum = 0;
		for (double sample : samples) {
			double diff = x - sample;
			sum += Math.exp(expMultiplier * diff * diff);
		}
		return (double) sum * multiplier;
	}

	public Double apply(double x) {
		return get(x);
	}

	public double getMean() {
		return mean;
	}

	public double getStandardDeviation() {
		return stddev;
	}

	private static boolean isSorted(double[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			if (array[i] > array[i + 1])
				return false;
		}
		return true;
	}
}
