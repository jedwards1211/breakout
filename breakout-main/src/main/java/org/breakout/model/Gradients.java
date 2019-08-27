package org.breakout.model;

import java.awt.Color;

public class Gradients {
	private Gradients() {

	}

	public static final GradientModel DEFAULT =
		new GradientModel(new float[]
		{ 0f, 0.24f, 0.64f, 1f },
			new Color[]
			{ new Color(255, 249, 204), new Color(255, 195, 0), new Color(214, 6, 127), new Color(34, 19, 150) });

	public static final GradientModel[] CHOICES =
		{
			DEFAULT,
			// Black to White
			new GradientModel(new float[]
			{ 0f, 1f }, new Color[] { new Color(0, 0, 0), new Color(255, 255, 255) }).reverse(),
			// Blue Bright
			new GradientModel(new float[]
			{ 0f, 1f }, new Color[] { new Color(0, 0, 255), new Color(197, 200, 255) }).reverse(),
			// Blue Light to Dark
			new GradientModel(new float[]
			{ 0f, 1f }, new Color[] { new Color(34, 94, 140), new Color(214, 233, 242) }).reverse(),
			// Blue-Green Bright
			new GradientModel(new float[]
			{ 0f, 1f }, new Color[] { new Color(17, 207, 147), new Color(206, 248, 244) }).reverse(),
			// Blue-Green Light to Dark
			new GradientModel(new float[]
			{ 0f, 1f }, new Color[] { new Color(7, 72, 69), new Color(208, 240, 241) }).reverse(),
			// Brown Light to Dark
			new GradientModel(new float[]
			{ 0f, 1f }, new Color[] { new Color(95, 65, 42), new Color(241, 240, 183) }).reverse(),
			// Brown to Blue Green Diverging, Bright
			new GradientModel(new float[]
			{ 0f, 0.5f, 1f },
				new Color[]
				{ new Color(18, 127, 145), new Color(251, 255, 197), new Color(154, 83, 29) }),
			// Brown to Blue Green Diverging, Dark
			new GradientModel(new float[]
			{ 0f, 0.5f, 1f }, new Color[] { new Color(39, 95, 99), new Color(202, 205, 105), new Color(107, 67, 42) }),
			// Coefficient Bias
			new GradientModel(new float[]
			{ 0f, 1f }, new Color[] { new Color(0, 50, 148), new Color(207, 212, 255) }).reverse(),
			// Cold to Hot Diverging
			new GradientModel(new float[]
			{ 0f, .5f, 1f },
				new Color[]
				{ new Color(215, 41, 32), new Color(251, 255, 197), new Color(197, 200, 255) }),
			// Condition Number
			new GradientModel(new float[]
			{ 0f, .5f, 1f }, new Color[] { new Color(0, 96, 2), new Color(251, 255, 20), new Color(253, 33, 5) })
				.reverse(),
			// Cyan to Purple
			new GradientModel(new float[]
			{ 0f, .5f, 1f }, new Color[] { new Color(255, 0, 255), new Color(0, 0, 255), new Color(0, 255, 255) })
				.reverse(),
			// Distance
			new GradientModel(new float[]
			{ 0f, 116f / 260, 190f / 260, 1f },
				new Color[]
				{ new Color(252, 194, 15), new Color(253, 4, 63), new Color(253, 3, 147), new Color(0, 0, 255) }),
			// Elevation #1
			new GradientModel(new float[]
			{ 0f, 20f / 260, 42f / 260, 73f / 260, 80f / 260, 120f / 260, 157f / 260, 197f / 250, 1f },
				new Color[]
				{
					new Color(168, 241, 235),
					new Color(170, 246, 187),
					new Color(249, 253, 182),
					new Color(0, 151, 51),
					new Color(0, 128, 66),
					new Color(248, 177, 13),
					new Color(128, 0, 2),
					new Color(106, 50, 16),
					new Color(242, 243, 253) }).reverse(), };
}
