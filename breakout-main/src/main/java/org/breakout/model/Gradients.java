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
			// // Black to White
			// new GradientModel(new float[]
			// { 0f, 1f }, new Color[] { new Color(0, 0, 0), new Color(255, 255, 255)
			// }).reverse(),
			// // Blue Bright
			// new GradientModel(new float[]
			// { 0f, 1f }, new Color[] { new Color(0, 0, 255), new Color(197, 200, 255)
			// }).reverse(),
			// // Blue Light to Dark
			// new GradientModel(new float[]
			// { 0f, 1f }, new Color[] { new Color(34, 94, 140), new Color(214, 233, 242)
			// }).reverse(),
			// // Blue-Green Bright
			// new GradientModel(new float[]
			// { 0f, 1f }, new Color[] { new Color(17, 207, 147), new Color(206, 248, 244)
			// }).reverse(),
			// // Blue-Green Light to Dark
			// new GradientModel(new float[]
			// { 0f, 1f }, new Color[] { new Color(7, 72, 69), new Color(208, 240, 241)
			// }).reverse(),
			// // Brown Light to Dark
			// new GradientModel(new float[]
			// { 0f, 1f }, new Color[] { new Color(95, 65, 42), new Color(241, 240, 183)
			// }).reverse(),
			// // Brown to Blue Green Diverging, Bright
			// new GradientModel(new float[]
			// { 0f, 0.5f, 1f },
			// new Color[]
			// { new Color(18, 127, 145), new Color(251, 255, 197), new Color(154, 83, 29)
			// }),
			// // Brown to Blue Green Diverging, Dark
			// new GradientModel(new float[]
			// { 0f, 0.5f, 1f }, new Color[] { new Color(39, 95, 99), new Color(202, 205,
			// 105), new Color(107, 67, 42) }),
			// // Coefficient Bias
			// new GradientModel(new float[]
			// { 0f, 1f }, new Color[] { new Color(0, 50, 148), new Color(207, 212, 255)
			// }).reverse(),
			// // Cold to Hot Diverging
			// new GradientModel(new float[]
			// { 0f, .5f, 1f },
			// new Color[]
			// { new Color(215, 41, 32), new Color(251, 255, 197), new Color(197, 200, 255)
			// }),
			// // Condition Number
			// new GradientModel(new float[]
			// { 0f, .5f, 1f }, new Color[] { new Color(0, 96, 2), new Color(251, 255, 20),
			// new Color(253, 33, 5) })
			// .reverse(),
			// // Cyan to Purple
			// new GradientModel(new float[]
			// { 0f, .5f, 1f }, new Color[] { new Color(255, 0, 255), new Color(0, 0, 255),
			// new Color(0, 255, 255) })
			// .reverse(),
			// // Distance
			// new GradientModel(new float[]
			// { 0f, 116f / 260, 190f / 260, 1f },
			// new Color[]
			// { new Color(252, 194, 15), new Color(253, 4, 63), new Color(253, 3, 147), new
			// Color(0, 0, 255) }),
			// // Elevation #1
			// new GradientModel(new float[]
			// { 0f, 20f / 260, 42f / 260, 73f / 260, 80f / 260, 120f / 260, 157f / 260,
			// 197f / 250, 1f },
			// new Color[]
			// {
			// new Color(168, 241, 235),
			// new Color(170, 246, 187),
			// new Color(249, 253, 182),
			// new Color(0, 151, 51),
			// new Color(0, 128, 66),
			// new Color(248, 177, 13),
			// new Color(128, 0, 2),
			// new Color(106, 50, 16),
			// new Color(242, 243, 253) }).reverse(), };
			GradientModel
				.builder()
				.add(0.000f, 0, 0, 0)
				.add(0.144f, 37, 37, 37)
				.add(0.339f, 87, 87, 87)
				.add(0.487f, 125, 125, 125)
				.add(0.815f, 208, 208, 208)
				.add(1.000f, 252, 252, 252)
				.build(),
			GradientModel.builder().add(0.000f, 201, 200, 255).add(1.000f, 11, 7, 224).build(),
			GradientModel.builder().add(0.000f, 210, 229, 232).add(1.000f, 48, 100, 140).build(),
			GradientModel.builder().add(0.000f, 202, 245, 234).add(1.000f, 50, 207, 146).build(),
			GradientModel.builder().add(0.000f, 212, 240, 234).add(1.000f, 22, 79, 74).build(),
			GradientModel.builder().add(0.000f, 240, 236, 169).add(1.000f, 102, 72, 48).build(),
			GradientModel.builder().add(0.000f, 154, 83, 29).add(1.000f, 39, 131, 145).build(),
			GradientModel.builder().add(0.000f, 108, 67, 41).add(1.000f, 51, 102, 102).build(),
			GradientModel.builder().add(0.000f, 211, 212, 252).add(1.000f, 1, 58, 148).build(),
			GradientModel.builder().add(0.000f, 68, 116, 180).add(1.000f, 217, 53, 41).build(),
			GradientModel.builder().add(0.000f, 3, 96, 0).add(0.487f, 252, 252, 0).add(1.000f, 255, 42, 0).build(),
			GradientModel.builder().add(0.000f, 5, 241, 245).add(0.502f, 2, 0, 245).add(1.000f, 241, 0, 245).build(),
			GradientModel.builder().add(0.000f, 179, 236, 240).add(1.000f, 10, 13, 148).build(),
			GradientModel.builder().add(0.000f, 255, 194, 0).add(0.417f, 255, 0, 64).add(1.000f, 31, 0, 252).build(),
			GradientModel
				.builder()
				.add(0.000f, 174, 240, 228)
				.add(0.140f, 252, 252, 177)
				.add(0.277f, 0, 128, 64)
				.add(0.421f, 248, 184, 7)
				.add(0.554f, 135, 8, 1)
				.add(0.712f, 106, 50, 15)
				.add(1.000f, 250, 247, 250)
				.build(),
			GradientModel
				.builder()
				.add(0.000f, 118, 218, 203)
				.add(0.129f, 248, 252, 195)
				.add(0.554f, 141, 103, 43)
				.add(0.708f, 150, 150, 181)
				.add(0.856f, 182, 151, 182)
				.add(1.000f, 250, 245, 250)
				.build(),
			GradientModel.builder().add(0.000f, 252, 232, 211).add(1.000f, 196, 12, 12).build(),
			GradientModel.builder().add(0.000f, 218, 218, 218).add(1.000f, 69, 69, 69).build(),
			GradientModel.builder().add(0.000f, 203, 255, 203).add(1.000f, 16, 204, 16).build(),
			GradientModel.builder().add(0.000f, 219, 245, 233).add(1.000f, 35, 102, 52).build(),
			GradientModel.builder().add(0.000f, 29, 203, 16).add(0.506f, 10, 239, 242).add(1.000f, 11, 37, 227).build(),
			GradientModel.builder().add(0.000f, 255, 234, 200).add(1.000f, 240, 120, 7).build(),
			GradientModel.builder().add(0.000f, 250, 232, 211).add(1.000f, 171, 65, 36).build(),
			GradientModel
				.builder()
				.add(0.000f, 242, 241, 159)
				.add(0.255f, 255, 246, 0)
				.add(0.476f, 255, 43, 0)
				.add(0.672f, 240, 7, 238)
				.add(1.000f, 20, 30, 176)
				.build(),
			GradientModel
				.builder()
				.add(0.000f, 136, 36, 35)
				.add(0.251f, 240, 151, 17)
				.add(0.502f, 251, 250, 192)
				.add(1.000f, 41, 34, 125)
				.build(),
			GradientModel.builder().add(0.000f, 115, 76, 39).add(0.502f, 251, 250, 192).add(1.000f, 83, 15, 99).build(),
			GradientModel.builder().add(0.000f, 156, 29, 112).add(1.000f, 101, 112, 47).build(),
			GradientModel.builder().add(0.000f, 94, 43, 69).add(1.000f, 25, 61, 16).build(),
			GradientModel
				.builder()
				.add(0.000f, 193, 79, 57)
				.add(0.395f, 255, 254, 1)
				.add(0.590f, 4, 219, 0)
				.add(1.000f, 12, 49, 122)
				.build(),
			GradientModel.builder().add(0.000f, 42, 147, 200).add(1.000f, 232, 24, 21).build(),
			GradientModel.builder().add(0.000f, 255, 203, 255).add(1.000f, 199, 8, 199).build(),
			GradientModel.builder().add(0.000f, 75, 29, 148).add(1.000f, 26, 125, 15).build(),
			GradientModel.builder().add(0.000f, 64, 12, 86).add(1.000f, 27, 82, 16).build(),
			GradientModel.builder().add(0.000f, 220, 180, 230).add(1.000f, 113, 15, 242).build(),
			GradientModel.builder().add(0.000f, 229, 212, 242).add(1.000f, 93, 45, 112).build(),
			GradientModel.builder().add(0.000f, 255, 203, 225).add(1.000f, 199, 8, 100).build(),
			GradientModel.builder().add(0.000f, 250, 214, 245).add(1.000f, 143, 19, 58).build(),
			GradientModel.builder().add(0.000f, 255, 201, 200).add(1.000f, 219, 8, 4).build(),
			GradientModel.builder().add(0.000f, 255, 221, 221).add(1.000f, 143, 14, 11).build(),
			GradientModel.builder().add(0.000f, 195, 66, 54).add(1.000f, 54, 97, 207).build(),
			GradientModel.builder().add(0.000f, 104, 10, 9).add(1.000f, 19, 56, 97).build(),
			GradientModel.builder().add(0.000f, 245, 7, 0).add(1.000f, 20, 245, 0).build(),
			GradientModel
				.builder()
				.add(0.000f, 185, 21, 18)
				.add(0.487f, 255, 253, 189)
				.add(1.000f, 59, 148, 37)
				.build(),
			GradientModel.builder().add(0.000f, 94, 18, 9).add(0.506f, 200, 201, 101).add(1.000f, 19, 71, 17).build(),
			GradientModel.builder().add(0.000f, 56, 167, 0).add(1.000f, 255, 4, 0).build(),
			GradientModel
				.builder()
				.add(0.000f, 255, 16, 0)
				.add(0.325f, 255, 254, 0)
				.add(0.657f, 11, 255, 254)
				.add(1.000f, 15, 19, 255)
				.build(),
			GradientModel
				.builder()
				.add(0.000f, 151, 8, 0)
				.add(0.321f, 153, 150, 0)
				.add(0.657f, 7, 153, 153)
				.add(1.000f, 9, 12, 153)
				.build(),
			GradientModel.builder().add(0.000f, 255, 153, 151).add(1.000f, 153, 156, 255).build(),
			GradientModel.builder().add(0.000f, 111, 151, 87).add(1.000f, 252, 237, 251).build(),
			GradientModel
				.builder()
				.add(0.000f, 255, 245, 255)
				.add(0.148f, 241, 0, 255)
				.add(0.273f, 33, 0, 255)
				.add(0.421f, 9, 252, 255)
				.add(0.568f, 24, 255, 1)
				.add(0.705f, 252, 255, 0)
				.add(1.000f, 133, 13, 0)
				.build(),
			GradientModel.builder().add(0.000f, 252, 252, 252).add(1.000f, 0, 0, 0).build(),
			GradientModel.builder().add(0.000f, 255, 255, 126).add(1.000f, 110, 8, 1).build(),
			GradientModel
				.builder()
				.add(0.000f, 251, 255, 123)
				.add(0.336f, 56, 224, 15)
				.add(1.000f, 15, 20, 122)
				.build(),
			GradientModel.builder().add(0.000f, 245, 241, 0).add(1.000f, 245, 0, 0).build(),
			GradientModel.builder().add(0.000f, 236, 252, 203).add(1.000f, 158, 204, 18).build(),
			GradientModel.builder().add(0.000f, 214, 240, 174).add(1.000f, 96, 107, 45).build(), };
}
