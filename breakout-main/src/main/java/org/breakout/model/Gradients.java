package org.breakout.model;

import java.awt.Color;

public class Gradients {
	private Gradients() {

	}

	public static final GradientModel DEFAULT =
		new GradientModel(new float[]
		{ 0, 0.24f, 0.64f, 1 },
			new Color[]
			{ new Color(255, 249, 204), new Color(255, 195, 0), new Color(214, 6, 127), new Color(34, 19, 150) });

	public static final GradientModel[] CHOICES =
		{
			DEFAULT,
			// pink tan teal slate-blue
			GradientModel
				.builder()
				.add(0, 255, 175, 153)
				.add(0.333, 151, 143, 82)
				.add(0.666, 2, 151, 134)
				.add(1, 70, 91, 118)
				.build(),
			// mint gold pumpkin mauve
			GradientModel
				.builder()
				.add(0, 210, 255, 169)
				.add(0.3, 240, 193, 14)
				.add(0.55, 219, 128, 44)
				.add(1, 126, 59, 106)
				.build(),
			// mint gold pumpkin mauve turquoise
			GradientModel
				.builder()
				.add(0, 210, 255, 169)
				.add(0.2, 240, 193, 14)
				.add(0.4, 219, 128, 44)
				.add(0.7, 126, 59, 106)
				.add(1, 6, 70, 79)
				.build(),
			// watermelon tan turquoise
			GradientModel.builder().add(0, 250, 75, 65).add(0.5, 130, 105, 69).add(1, 6, 73, 79).build(),
			// sunset variants
			GradientModel.builder().add(0, 255, 105, 79).add(1, 1, 75, 107).build(),
			GradientModel.builder().add(0, 255, 221, 0).add(0.5, 255, 105, 79).add(1, 1, 75, 107).build(),
			GradientModel.builder().add(0, 255, 221, 169).add(0.5, 255, 105, 79).add(1, 1, 75, 107).build(),
			GradientModel
				.builder()
				.add(0, 255, 221, 169)
				.add(0.25, 255, 165, 46)
				.add(0.5, 255, 105, 79)
				.add(1, 1, 75, 107)
				.build(),
			// black white
			GradientModel.builder().add(0, 0, 0, 0).add(1.000, 255, 255, 255).build(),
			// green yellow red
			GradientModel.builder().add(0, 179, 236, 240).add(1.000, 10, 13, 148).build(),
			// toucan
			GradientModel.builder().add(0, 255, 194, 0).add(0.417, 255, 0, 64).add(1.000, 31, 0, 252).build(),
			// burnt-red light-yellow navy
			GradientModel
				.builder()
				.add(0, 136, 36, 35)
				.add(0.251, 240, 151, 17)
				.add(0.502, 251, 250, 192)
				.add(1.000, 41, 34, 125)
				.build(),
			// burnt-rainbow
			GradientModel
				.builder()
				.add(0, 193, 79, 57)
				.add(0.395, 255, 254, 1)
				.add(0.590, 4, 219, 0)
				.add(1.000, 12, 49, 122)
				.build(),
			// red steel-blue
			GradientModel.builder().add(0, 42, 147, 200).add(1.000, 232, 24, 21).build(),
			GradientModel.builder().add(0, 255, 203, 255).add(1.000, 199, 8, 199).build(),
			// lilac magenta
			GradientModel.builder().add(0, 255, 203, 225).add(1.000, 199, 8, 100).build(),
			// lilac dark-magenta
			GradientModel.builder().add(0, 250, 214, 245).add(1.000, 143, 19, 58).build(),
			// pink red
			GradientModel.builder().add(0, 255, 201, 200).add(1.000, 219, 8, 4).build(),
			// rose gradient
			GradientModel.builder().add(0, 255, 221, 221).add(1.000, 143, 14, 11).build(),
			// red light-yellow green
			GradientModel.builder().add(0, 185, 21, 18).add(0.487, 255, 253, 189).add(1.000, 59, 148, 37).build(),
			// rainbow
			GradientModel
				.builder()
				.add(0, 255, 16, 0)
				.add(0.325, 255, 254, 0)
				.add(0.657, 11, 255, 254)
				.add(1.000, 15, 19, 255)
				.build(),
			// dim rainbow
			GradientModel
				.builder()
				.add(0, 151, 8, 0)
				.add(0.321, 153, 150, 0)
				.add(0.657, 7, 153, 153)
				.add(1.000, 9, 12, 153)
				.build(),
			// grayscale
			GradientModel.builder().add(0, 252, 252, 252).add(1.000, 0, 0, 0).build(),
			// yellow brown
			GradientModel.builder().add(0, 255, 255, 126).add(1.000, 110, 8, 1).build(),
			// yellow green navy
			GradientModel.builder().add(0, 251, 255, 123).add(0.336, 56, 224, 15).add(1.000, 15, 20, 122).build(),
			// yellow red
			GradientModel.builder().add(0, 245, 241, 0).add(1.000, 245, 0, 0).build(), };
}
